package cn.gudqs7.plugins.common.base.action;

import com.intellij.psi.PsiClass;
import com.intellij.openapi.project.Project;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.File;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractBatchDocerSaviorTest {

    @Test
    void retainsDistinctClassesWithoutQualifiedNames() {
        Set<PsiClass> classes = new TreeSet<>(AbstractBatchDocerSavior.psiClassComparator());

        classes.add(psiClass(null));
        classes.add(psiClass(null));

        assertEquals(2, classes.size());
    }

    @Test
    void deduplicatesClassesWithSameQualifiedName() {
        Set<PsiClass> classes = new TreeSet<>(AbstractBatchDocerSavior.psiClassComparator());

        classes.add(psiClass("example.UserController"));
        classes.add(psiClass("example.UserController"));

        assertEquals(1, classes.size());
    }

    @Test
    void keepsExistingOutputWhenBatchIsCancelled() throws IOException {
        Path docRoot = Files.createTempDirectory("api-savior-doc-root");
        Path stagingRoot = Files.createTempDirectory("api-savior-doc-staging");
        Path existingFile = docRoot.resolve("previous.md");
        Files.write(existingFile, "existing".getBytes(StandardCharsets.UTF_8));
        Files.write(stagingRoot.resolve("previous.md"), "replacement".getBytes(StandardCharsets.UTF_8));

        new TestBatchDocerSavior().cancel(stagingRoot.toString(), docRoot.toString());

        assertTrue(Files.exists(existingFile));
        assertEquals("existing", new String(Files.readAllBytes(existingFile), StandardCharsets.UTF_8));
        assertTrue(Files.notExists(stagingRoot));
    }

    @Test
    void replacesPreviousOutputOnlyAfterStagingCompletes() throws IOException {
        Path docRoot = Files.createTempDirectory("api-savior-doc-root");
        Path stagingRoot = Files.createTempDirectory("api-savior-doc-staging");
        Files.write(docRoot.resolve("previous.md"), "old".getBytes(StandardCharsets.UTF_8));
        Files.write(stagingRoot.resolve("current.md"), "new".getBytes(StandardCharsets.UTF_8));

        new TestBatchDocerSavior().commit(stagingRoot.toString(), docRoot.toString());

        assertTrue(Files.notExists(docRoot.resolve("previous.md")));
        assertEquals("new", new String(Files.readAllBytes(docRoot.resolve("current.md")), StandardCharsets.UTF_8));
    }

    @Test
    void restoresPreviousOutputWhenStagingMoveFails() throws IOException {
        Path docRoot = Files.createTempDirectory("api-savior-doc-root");
        Path stagingRoot = Files.createTempDirectory("api-savior-doc-staging");
        Files.write(docRoot.resolve("previous.md"), "old".getBytes(StandardCharsets.UTF_8));
        Files.write(stagingRoot.resolve("current.md"), "new".getBytes(StandardCharsets.UTF_8));

        assertThrows(IOException.class, () -> new FailingBatchDocerSavior().commit(stagingRoot.toString(), docRoot.toString()));

        assertEquals("old", new String(Files.readAllBytes(docRoot.resolve("previous.md")), StandardCharsets.UTF_8));
    }

    @Test
    void usesStableSuffixForConflictingOutputNames() {
        Set<String> outputPaths = new HashSet<>();

        assertEquals("User.md", AbstractBatchDocerSavior.uniqueFullFileName("doc/module", "User", "md", "a.User", outputPaths));
        String conflictingName = AbstractBatchDocerSavior.uniqueFullFileName("doc/module", "User", "md", "b.User", outputPaths);

        assertTrue(conflictingName.matches("User-[a-z0-9]+\\.md"));
    }

    private PsiClass psiClass(String qualifiedName) {
        return (PsiClass) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PsiClass.class},
                (proxy, method, args) -> "getQualifiedName".equals(method.getName()) ? qualifiedName : null);
    }

    private static class TestBatchDocerSavior extends AbstractBatchDocerSavior<Void> {

        void cancel(String stagingRootPath, String projectPath) {
            handleCancelTask(stagingRootPath, projectPath);
        }

        void commit(String stagingRootPath, String docRootPath) throws IOException {
            commitDocRoot(stagingRootPath, docRootPath);
        }

        @Override
        protected void refreshProject(String projectFilePath) {
            // no IDEA project is needed to verify cancellation does not delete existing files
        }

        @Override
        protected boolean isNeedDealPsiClass(PsiClass psiClass, Project project) {
            return false;
        }
    }

    private static class FailingBatchDocerSavior extends TestBatchDocerSavior {

        @Override
        protected void moveDirectory(File source, File target) throws IOException {
            if (source.getName().startsWith("api-savior-doc-staging")) {
                Files.createDirectories(target.toPath());
                Files.write(target.toPath().resolve("incomplete.md"), "partial".getBytes(StandardCharsets.UTF_8));
                throw new IOException("staging move failed");
            }
            super.moveDirectory(source, target);
        }
    }
}
