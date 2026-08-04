package cn.gudqs7.plugins.common.context;

import cn.gudqs7.plugins.common.util.IndexIncrementUtil;
import cn.gudqs7.plugins.common.util.PluginSettingHelper;
import cn.gudqs7.plugins.common.util.WebEnvironmentUtil;
import cn.gudqs7.plugins.common.util.structure.PsiTypeUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Owns all per-generation thread state and guarantees cleanup on the worker thread.
 */
public final class GenerationSession implements AutoCloseable {

    private GenerationSession(Project project, VirtualFile contextFile) {
        PluginSettingHelper.initConfig(project, contextFile);
    }

    public static GenerationSession open(Project project, VirtualFile contextFile) {
        return new GenerationSession(project, contextFile);
    }

    @Override
    public void close() {
        PluginSettingHelper.clearConfigCache();
        PsiTypeUtil.clearGeneric();
        IndexIncrementUtil.clear();
        WebEnvironmentUtil.emptyIp();
    }
}
