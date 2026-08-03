package cn.gudqs7.plugins.common.util;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PluginSettingHelperTest {

    @Test
    void 应该_当不同线程读取配置时_隔离状态() throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        PluginSettingHelper.clearConfigCache();
        try {
            PluginSettingHelper.saveConfigToCache(Collections.singletonMap("host", "main"));

            assertEquals("main", PluginSettingHelper.getConfigItem("host", "default"));
            assertEquals("default", executor.submit(() -> PluginSettingHelper.getConfigItem("host", "default")).get());
        } finally {
            PluginSettingHelper.clearConfigCache();
            executor.shutdownNow();
        }
    }

    @Test
    void 应该_当重新初始化配置时_清除上次任务残留值() {
        PluginSettingHelper.clearConfigCache();
        try {
            PluginSettingHelper.saveConfigToCache(Collections.singletonMap("old", "value"));
            PluginSettingHelper.saveConfigToCache(Collections.singletonMap("current", "value"));

            assertEquals("default", PluginSettingHelper.getConfigItem("old", "default"));
            assertEquals("value", PluginSettingHelper.getConfigItem("current", "default"));
        } finally {
            PluginSettingHelper.clearConfigCache();
        }
    }
}
