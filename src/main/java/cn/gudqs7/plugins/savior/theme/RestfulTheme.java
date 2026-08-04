package cn.gudqs7.plugins.savior.theme;

import cn.gudqs7.plugins.common.consts.MapKeyConstant;
import cn.gudqs7.plugins.common.enums.FieldType;
import cn.gudqs7.plugins.common.pojo.resolver.CommentInfo;
import cn.gudqs7.plugins.common.pojo.resolver.RequestMapping;
import cn.gudqs7.plugins.common.pojo.resolver.StructureAndCommentInfo;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import cn.gudqs7.plugins.common.resolver.RequestMappingResolver;
import cn.gudqs7.plugins.common.util.JsonUtil;
import cn.gudqs7.plugins.savior.enums.ThemeType;
import cn.gudqs7.plugins.savior.pojo.ApiDocument;
import cn.gudqs7.plugins.savior.pojo.PostmanKvInfo;
import cn.gudqs7.plugins.savior.reader.Java2BulkReader;
import cn.gudqs7.plugins.savior.util.RestfulUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wq
 */
public class RestfulTheme implements Theme {

    private static final Theme INSTANCE = new RestfulTheme();

    private final Java2BulkReader java2BulkReader;

    public RestfulTheme() {
        java2BulkReader = new Java2BulkReader(this);
    }

    public static Theme getInstance() {
        return INSTANCE;
    }

    @Override
    public ThemeType getThemeType() {
        return ThemeType.RESTFUL;
    }

    @Override
    public String getPathPrefix() {
        return "restful";
    }

    @Override
    public String getDefaultContentType() {
        return RequestMapping.ContentType.X_WWW_FORM_URLENCODED;
    }

    @Override
    public boolean handleMethodHidden(AnnotationHolder annotationHolder) {
        // 过滤非 Controller 的方法
        return !RequestMappingResolver.hasMappingAnnotation(annotationHolder);
    }

    @Override
    public void afterCollectData(ApiDocument document, Project project, PsiMethod publicMethod, String interfaceClassName, CommentInfo commentInfo, StructureAndCommentInfo paramStructureAndCommentInfo, StructureAndCommentInfo returnStructureAndCommentInfo, Map<String, Object> java2jsonMap, Map<String, Object> returnJava2jsonMap, String java2jsonStr, String returnJava2jsonStr) {
        if (java2jsonMap == null || java2jsonMap.isEmpty()) {
            document.setJsonExample("");
            return;
        }
        // 1.获取 json示例 或 bulk 示例
        // 2.补全 URL query 部分
        String url = commentInfo.getUrl("");
        String contentType = commentInfo.getContentType(getDefaultContentType());
        String method = commentInfo.getMethod("");
        String method0 = RestfulUtil.getFirstMethod(method);
        boolean firstMethodIsGet = RequestMapping.Method.GET.equals(method0);

        HashMap<String, Object> data = new HashMap<>(2);
        data.put("removeRequestBody", true);
        List<PostmanKvInfo> queryList = java2BulkReader.read(paramStructureAndCommentInfo, data);
        String query = RestfulUtil.getUrlQuery(queryList, false);

        if (firstMethodIsGet) {
            // GET
            url = url + query;
                        document.setJsonExample(RestfulUtil.getPostmanBulkByKvList(queryList));
        } else {
            switch (contentType) {
                case RequestMapping.ContentType.APPLICATION_JSON:
                    // POST + requestBody
                    url = url + query;
                    Object key = java2jsonMap.get(MapKeyConstant.HAS_REQUEST_BODY);
                    if (key instanceof String) {
                        String key0 = (String) key;
                        document.setJsonExample(JsonUtil.toJson(java2jsonMap.get(key0)));
                    }
                    break;
                case RequestMapping.ContentType.FORM_DATA:
                case RequestMapping.ContentType.X_WWW_FORM_URLENCODED:
                    // POST + form-data || POST + x-www/form-data
                    List<PostmanKvInfo> kvList = java2BulkReader.read(paramStructureAndCommentInfo);
                        document.setJsonExample(RestfulUtil.getPostmanBulkByKvList(kvList));
                    break;
                default:
                    break;
            }
        }
        document.setUrl(url);
    }

    @Override
    public boolean handleParameter(StructureAndCommentInfo structureAndCommentInfo, Map<String, Object> map, String fieldName) {
        if (structureAndCommentInfo.isRequestBody()) {
            map.put(MapKeyConstant.HAS_REQUEST_BODY, fieldName);
            return false;
        }
        // 只要包含多个子节点都需要打散
        return FieldType.POJO.getType().equals(structureAndCommentInfo.getFieldTypeCode());
    }

}
