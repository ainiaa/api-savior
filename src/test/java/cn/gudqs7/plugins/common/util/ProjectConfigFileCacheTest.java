package cn.gudqs7.plugins.common.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class ProjectConfigFileCacheTest {

    @Test
    void keepsConfigFilesSeparatedByModuleScope() {
        ProjectConfigFileCache cache = new ProjectConfigFileCache();
        VirtualFile moduleA = virtualFile("module-a");
        VirtualFile moduleB = virtualFile("module-b");

        cache.setConfigFile("module-a", moduleA);
        cache.setConfigFile("module-b", moduleB);

        assertSame(moduleA, cache.getConfigFile("module-a"));
        assertSame(moduleB, cache.getConfigFile("module-b"));
    }

    private VirtualFile virtualFile(String path) {
        return new LightVirtualFile(path);
    }
}
