package cn.gudqs7.plugins.common.util;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.testFramework.LightVirtualFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Collections;

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

    @Test
    void cachesConfigUntilTheConfigFileChanges() {
        ProjectConfigFileCache cache = new ProjectConfigFileCache();
        LightVirtualFile configFile = new LightVirtualFile("docer-config.properties", "theme=one");

        cache.setConfig("module-a", configFile, Collections.singletonMap("theme", "one"));

        assertEquals("one", cache.getConfig("module-a", configFile).get("theme"));
        configFile.setContent(null, "theme=two", false);
        assertNull(cache.getConfig("module-a", configFile));
    }

    private VirtualFile virtualFile(String path) {
        return new LightVirtualFile(path);
    }
}
