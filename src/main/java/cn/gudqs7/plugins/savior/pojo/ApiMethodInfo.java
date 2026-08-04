package cn.gudqs7.plugins.savior.pojo;

import cn.gudqs7.plugins.common.pojo.resolver.CommentInfo;
import cn.gudqs7.plugins.common.pojo.resolver.StructureAndCommentInfo;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

/**
 * 单个接口方法的解析结果。
 */
public class ApiMethodInfo {

    private final Project project;
    private final String interfaceClassName;
    private final PsiMethod publicMethod;
    private final CommentInfo commentInfo;
    private final StructureAndCommentInfo paramStructureAndCommentInfo;
    private final StructureAndCommentInfo returnStructureAndCommentInfo;

    public ApiMethodInfo(Project project, String interfaceClassName, PsiMethod publicMethod, CommentInfo commentInfo,
                         StructureAndCommentInfo paramStructureAndCommentInfo, StructureAndCommentInfo returnStructureAndCommentInfo) {
        this.project = project;
        this.interfaceClassName = interfaceClassName;
        this.publicMethod = publicMethod;
        this.commentInfo = commentInfo;
        this.paramStructureAndCommentInfo = paramStructureAndCommentInfo;
        this.returnStructureAndCommentInfo = returnStructureAndCommentInfo;
    }

    public Project getProject() {
        return project;
    }

    public String getInterfaceClassName() {
        return interfaceClassName;
    }

    public PsiMethod getPublicMethod() {
        return publicMethod;
    }

    public CommentInfo getCommentInfo() {
        return commentInfo;
    }

    public StructureAndCommentInfo getParamStructureAndCommentInfo() {
        return paramStructureAndCommentInfo;
    }

    public StructureAndCommentInfo getReturnStructureAndCommentInfo() {
        return returnStructureAndCommentInfo;
    }
}
