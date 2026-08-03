package cn.gudqs7.plugins.common.util.structure;

import com.intellij.psi.PsiType;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PsiTypeUtilTest {

    @Test
    void 应该_当不同线程解析泛型时_不共享缓存() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        PsiTypeUtil.clearGeneric();
        try {
            PsiTypeUtil.genericMap().put("sample.Type", new PsiType[0]);

            assertTrue(PsiTypeUtil.genericMap().containsKey("sample.Type"));
            assertFalse(executor.submit(() -> PsiTypeUtil.genericMap().containsKey("sample.Type")).get());
        } finally {
            PsiTypeUtil.clearGeneric();
            executor.shutdownNow();
        }
    }
}
