package cn.gudqs7.plugins.common.util;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Caches the server port per content root until PSI changes. */
public final class ServerPortCache implements Disposable {

    private final Project project;
    private final Map<String, Entry> entries = new ConcurrentHashMap<>();

    public ServerPortCache(Project project) {
        this.project = project;
    }

    public String getPort(PsiFile containingFile) {
        String scope = scope(containingFile);
        long modificationCount = PsiManager.getInstance(project).getModificationTracker().getModificationCount();
        Entry current = entries.get(scope);
        if (current != null && current.modificationCount == modificationCount) {
            return current.port;
        }
        String port = WebEnvironmentUtil.findPortByConfigFile(project, containingFile);
        entries.put(scope, new Entry(modificationCount, port));
        return port;
    }

    @Override
    public void dispose() {
        entries.clear();
    }

    private String scope(PsiFile containingFile) {
        return ModuleScope.of(project, containingFile == null ? null : containingFile.getVirtualFile());
    }

    private static final class Entry {

        private final long modificationCount;
        private final String port;

        private Entry(long modificationCount, String port) {
            this.modificationCount = modificationCount;
            this.port = port;
        }
    }
}
