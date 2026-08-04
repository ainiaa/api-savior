package cn.gudqs7.plugins.savior.action.base;

import cn.gudqs7.plugins.common.base.action.AbstractOnRightClickSavior;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AbstractReqDocerSaviorArchitectureTest {

    @Test
    void shouldExposeGenericBackgroundResultExecutor() throws Exception {
        Method method = AbstractOnRightClickSavior.class.getDeclaredMethod("generateWithProgress",
                Project.class, VirtualFile.class, Supplier.class, Consumer.class);

        assertTrue(Modifier.isProtected(method.getModifiers()));
    }

    @Test
    void shouldNotBypassSharedBackgroundGenerationForClassRequests() {
        assertThrows(NoSuchMethodException.class,
                () -> AbstractReqDocerSavior.class.getDeclaredMethod("handlePsiClass", Project.class, PsiClass.class));
    }
}
