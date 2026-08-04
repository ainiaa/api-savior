package cn.gudqs7.plugins.savior.action.rpc;

import cn.gudqs7.plugins.common.resolver.RequestMappingResolver;
import cn.gudqs7.plugins.common.util.structure.PsiClassUtil;
import cn.gudqs7.plugins.savior.action.base.AbstractDocerSavior;
import cn.gudqs7.plugins.savior.theme.ThemeHelper;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;

/**
 * @author wq
 */
public class RpcDocSaviorAction extends AbstractDocerSavior {

    public RpcDocSaviorAction() {
        super(ThemeHelper.getRpcTheme());
    }

    @Override
    protected void checkPsiMethod(PsiMethod psiMethod, Project project, AnActionEvent e) {
        // 不带 mapping 注解的
        if (RequestMappingResolver.hasMethodMapping(psiMethod)) {
            notVisible(e);
        }
    }

    @Override
    protected void checkPsiClass(PsiClass psiClass, Project project, AnActionEvent e) {
        if (!PsiClassUtil.isNormalInterface(psiClass, project)) {
            notVisible(e);
        }
    }

}
