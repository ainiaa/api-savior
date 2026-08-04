package cn.gudqs7.plugins.common.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;

/** Resolves the content-root scope shared by module-aware configuration. */
public final class ModuleScope {

    private ModuleScope() {
    }

    public static String of(Project project, VirtualFile file) {
        if (file != null) {
            VirtualFile contentRoot = ProjectFileIndex.getInstance(project).getContentRootForFile(file);
            if (contentRoot != null) {
                return contentRoot.getPath();
            }
        }
        String basePath = project.getBasePath();
        return basePath == null ? "" : basePath;
    }

    public static boolean same(Project project, VirtualFile first, VirtualFile second) {
        return first != null && second != null && of(project, first).equals(of(project, second));
    }
}
