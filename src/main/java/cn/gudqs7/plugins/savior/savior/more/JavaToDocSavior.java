package cn.gudqs7.plugins.savior.savior.more;

import cn.gudqs7.plugins.common.util.file.FreeMarkerUtil;
import cn.gudqs7.plugins.savior.pojo.ApiMethodInfo;
import cn.gudqs7.plugins.savior.pojo.ApiDocument;
import cn.gudqs7.plugins.savior.savior.base.AbstractSavior;
import cn.gudqs7.plugins.savior.theme.Theme;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author wq
 * @date 2021/5/19
 */
public class JavaToDocSavior extends AbstractSavior<ApiDocument> {

    public JavaToDocSavior(Theme theme) {
        super(theme);
    }

    public String generateApiByServiceInterface(PsiClass psiClass, Project project) {
        return generateApiByServiceInterfaceV2(psiClass, project).getLeft();
    }

    public Pair<String, List<String>> generateApiByServiceInterfaceV2(PsiClass psiClass, Project project) {
        String interfaceClassName = psiClass.getQualifiedName();
        List<String> apiNameList = new ArrayList<>();
        StringBuilder allDoc = new StringBuilder();

        List<PsiMethod> methods = getMethodList(psiClass);
        for (PsiMethod method : methods) {
            Pair<String, String> pair = generateDocByMethodV2(project, interfaceClassName, method, false);
            String doc = pair.getLeft();
            if (StringUtils.isNotBlank(doc)) {
                allDoc.append(doc);
            }
            String apiName = pair.getRight();
            if (apiName != null) {
                apiNameList.add(apiName);
            }
        }
        return Pair.of(allDoc.toString(), apiNameList);
    }

    public String generateDocByMethod(Project project, String interfaceClassName, PsiMethod publicMethod, boolean jumpHidden) {
        return generateDocByMethodV2(project, interfaceClassName, publicMethod, jumpHidden).getLeft();
    }

    public Pair<String, String> generateDocByMethodV2(Project project, String interfaceClassName, PsiMethod publicMethod, boolean jumpHidden) {
        ApiDocument data = getDataByMethod(project, interfaceClassName, publicMethod, jumpHidden);
        if (data == null) {
            return Pair.of("", null);
        }
        String apiName = data.getInterfaceName();
        String template = FreeMarkerUtil.renderTemplate(theme.getMethodPath(), data);
        return Pair.of(template + "\n\n", apiName);
    }

    @Override
    protected ApiDocument getDataByStructureAndCommentInfo(ApiMethodInfo apiMethodInfo, Map<String, Object> param) {
        return createApiDocument(apiMethodInfo);
    }

}
