package cn.gudqs7.plugins.common.util;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 当前项目的文档配置文件缓存，由项目关闭生命周期自动释放。
 */
public class ProjectConfigFileCache implements Disposable {

    private final Map<String, VirtualFile> configFiles = new ConcurrentHashMap<>();

    @Nullable
    public VirtualFile getConfigFile(String scope) {
        return configFiles.get(scope);
    }

    public void setConfigFile(String scope, VirtualFile configFile) {
        configFiles.put(scope, configFile);
    }

    @Override
    public void dispose() {
        configFiles.clear();
    }
}
