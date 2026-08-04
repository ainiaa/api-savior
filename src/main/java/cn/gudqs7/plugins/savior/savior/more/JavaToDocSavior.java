package cn.gudqs7.plugins.savior.savior.more;

import cn.gudqs7.plugins.common.consts.MapKeyConstant;
import cn.gudqs7.plugins.common.pojo.resolver.CommentInfo;
import cn.gudqs7.plugins.common.pojo.resolver.ResponseCodeInfo;
import cn.gudqs7.plugins.common.pojo.resolver.StructureAndCommentInfo;
import cn.gudqs7.plugins.common.util.JsonUtil;
import cn.gudqs7.plugins.common.util.StringTool;
import cn.gudqs7.plugins.common.util.file.FreeMarkerUtil;
import cn.gudqs7.plugins.savior.pojo.ApiMethodInfo;
import cn.gudqs7.plugins.savior.pojo.ApiDocument;
import cn.gudqs7.plugins.savior.pojo.FieldLevelInfo;
import cn.gudqs7.plugins.savior.savior.base.AbstractSavior;
import cn.gudqs7.plugins.savior.theme.Theme;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
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
        Project project = apiMethodInfo.getProject();
        PsiMethod publicMethod = apiMethodInfo.getPublicMethod();
        CommentInfo commentInfo = apiMethodInfo.getCommentInfo();
        String interfaceClassName = apiMethodInfo.getInterfaceClassName();
        StructureAndCommentInfo paramStructureAndCommentInfo = apiMethodInfo.getParamStructureAndCommentInfo();
        StructureAndCommentInfo returnStructureAndCommentInfo = apiMethodInfo.getReturnStructureAndCommentInfo();
        Map<String, List<FieldLevelInfo>> paramLevelMap = java2ApiReader.read(paramStructureAndCommentInfo);
        Map<String, List<FieldLevelInfo>> returnLevelMap = java2ApiReader.read(returnStructureAndCommentInfo);
        Map<String, Object> java2jsonMap = java2JsonReader.read(paramStructureAndCommentInfo);
        Map<String, Object> returnJava2jsonMap = java2JsonReader.read(returnStructureAndCommentInfo);
        String java2jsonStr = JsonUtil.toJson(java2jsonMap);
        String returnJava2jsonStr = JsonUtil.toJson(returnJava2jsonMap.getOrDefault(MapKeyConstant.RETURN_FIELD_NAME, new Object()));

        ApiDocument document = collectDataByStr(publicMethod, commentInfo, interfaceClassName,
                paramLevelMap, returnLevelMap, java2jsonStr, returnJava2jsonStr);

        theme.afterCollectData(document, project, publicMethod, interfaceClassName, commentInfo,
                paramStructureAndCommentInfo, returnStructureAndCommentInfo,
                java2jsonMap, returnJava2jsonMap, java2jsonStr, returnJava2jsonStr);

        return document;
    }

    private ApiDocument collectDataByStr(
            PsiMethod publicMethod, CommentInfo commentInfo, String interfaceClassName,
            Map<String, List<FieldLevelInfo>> paramLevelMap, Map<String, List<FieldLevelInfo>> returnLevelMap, String java2jsonStr, String returnJava2jsonStr
    ) {
        String url = commentInfo.getUrl("");
        String contentType = commentInfo.getContentType(theme.getDefaultContentType());
        String method = commentInfo.getMethod("");
        String methodName = publicMethod.getName();
        String interfaceName = commentInfo.getValue(methodName);
        String notes = commentInfo.getNotes("");
        List<ResponseCodeInfo> responseCodeInfoList = commentInfo.getResponseCodeInfoList();

        ApiDocument document = new ApiDocument();
        document.setInterfaceName(StringTool.replaceMd(interfaceName));
        document.setInterfaceNotes(notes);
        document.setQualifiedMethodName(interfaceClassName + "#" + methodName);
        document.setUrl(url);
        document.setMethod(method);
        document.setContentType(contentType);
        document.setParamLevelMap(paramLevelMap);
        document.setReturnLevelMap(returnLevelMap);
        document.setJsonExample(java2jsonStr);
        document.setReturnJsonExample(returnJava2jsonStr);
        document.setResponseCodeInfoList(responseCodeInfoList);
        return document;
    }

}
