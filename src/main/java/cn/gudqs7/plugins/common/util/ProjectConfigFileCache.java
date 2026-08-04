package cn.gudqs7.plugins.common.util;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 当前项目的文档配置文件缓存，由项目关闭生命周期自动释放。
 */
public class ProjectConfigFileCache implements Disposable {

    private final Map<String, VirtualFile> configFiles = new ConcurrentHashMap<>();
    private final Map<String, ConfigSnapshot> configs = new ConcurrentHashMap<>();

    @Nullable
    public VirtualFile getConfigFile(String scope) {
        return configFiles.get(scope);
    }

    public void setConfigFile(String scope, VirtualFile configFile) {
        configFiles.put(scope, configFile);
    }

    @Nullable
    Map<String, String> getConfig(String scope, VirtualFile configFile) {
        ConfigSnapshot snapshot = configs.get(scope);
        if (snapshot == null || !snapshot.configFile.equals(configFile)
                || snapshot.modificationStamp != configFile.getModificationStamp()) {
            return null;
        }
        return snapshot.values;
    }

    void setConfig(String scope, VirtualFile configFile, Map<String, String> values) {
        configs.put(scope, new ConfigSnapshot(configFile, configFile.getModificationStamp(),
                Collections.unmodifiableMap(new HashMap<>(values))));
    }

    @Override
    public void dispose() {
        configFiles.clear();
        configs.clear();
    }

    private static final class ConfigSnapshot {

        private final VirtualFile configFile;
        private final long modificationStamp;
        private final Map<String, String> values;

        private ConfigSnapshot(VirtualFile configFile, long modificationStamp, Map<String, String> values) {
            this.configFile = configFile;
            this.modificationStamp = modificationStamp;
            this.values = values;
        }
    }
}
