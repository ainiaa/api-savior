package cn.gudqs7.plugins.common.util;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;

/**
 * 当前项目的文档配置文件缓存，由项目关闭生命周期自动释放。
 */
public class ProjectConfigFileCache implements Disposable {

    private volatile VirtualFile configFile;

    @Nullable
    public VirtualFile getConfigFile() {
        return configFile;
    }

    public void setConfigFile(VirtualFile configFile) {
        this.configFile = configFile;
    }

    @Override
    public void dispose() {
        configFile = null;
    }
}
