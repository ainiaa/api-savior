package cn.gudqs7.plugins.savior.action.base;

import cn.gudqs7.plugins.common.base.action.AbstractOnRightClickSavior;
import cn.gudqs7.plugins.savior.savior.more.JavaToPostmanSavior;
import cn.gudqs7.plugins.savior.theme.Theme;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

/**
 * @author wenquan
 * @date 2022/4/13
 */
public abstract class AbstractPostmanSavior extends AbstractOnRightClickSavior {

    private final JavaToPostmanSavior javaToPostmanSavior;

    public AbstractPostmanSavior(Theme theme) {
        javaToPostmanSavior = new JavaToPostmanSavior(theme);
    }

    @Override
    protected void checkPsiMethod(PsiMethod psiMethod, Project project, AnActionEvent e) {

    }

    @Override
    protected void checkPsiClass(PsiClass psiClass, Project project, AnActionEvent e) {

    }

    @Override
    protected String handlePsiClass0(Project project, PsiClass psiClass) {
        // todo 根据class 生成 postman 文档
        //return javaToPostmanSavior.generateApiByServiceInterface(psiClass, project);
        return "";
    }

    @Override
    protected String handlePsiMethod0(Project project, PsiMethod psiMethod, String psiClassName) {
        // todo 根据method 生成 postman 文档
        //return javaToPostmanSavior.generateDocByMethod(project, psiClassName, psiMethod, true);
        return "";
    }

    @Override
    protected String getTip() {
        return "已自动的将 postMan 文档复制到您的剪切板!\n您可在此预览后再去粘贴!";
    }

}
