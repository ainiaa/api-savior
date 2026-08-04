package cn.gudqs7.plugins.savior.savior.more;

import cn.gudqs7.plugins.common.consts.CommonConst;
import cn.gudqs7.plugins.common.consts.MapKeyConstant;
import cn.gudqs7.plugins.common.enums.MoreCommentTagEnum;
import cn.gudqs7.plugins.common.enums.PluginSettingEnum;
import cn.gudqs7.plugins.common.pojo.resolver.CommentInfo;
import cn.gudqs7.plugins.common.pojo.resolver.StructureAndCommentInfo;
import cn.gudqs7.plugins.common.resolver.comment.AnnotationHolder;
import cn.gudqs7.plugins.common.util.JsonUtil;
import cn.gudqs7.plugins.common.util.PluginSettingHelper;
import cn.gudqs7.plugins.common.util.api.HttpUtil;
import cn.gudqs7.plugins.common.util.jetbrain.NotificationUtil;
import cn.gudqs7.plugins.savior.pojo.ApiMethodInfo;
import cn.gudqs7.plugins.savior.pojo.ComplexInfo;
import cn.gudqs7.plugins.savior.pojo.FieldCommentInfo;
import cn.gudqs7.plugins.savior.reader.Java2ComplexReader;
import cn.gudqs7.plugins.savior.savior.base.AbstractSavior;
import cn.gudqs7.plugins.savior.theme.Theme;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiMethod;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

/**
 * @author wq
 * @date 2021/5/19
 */
public class JavaToOneApiSavior extends AbstractSavior<JavaToOneApiSavior.OneApiRequest> {

    private final Java2ComplexReader java2ComplexReader;

    public JavaToOneApiSavior(Theme theme) {
        super(theme);
        this.java2ComplexReader = new Java2ComplexReader(theme);
    }

    /**
     * 必须在 ReadAction 内调用；返回值不再访问 PSI，可在读锁外上传。
     */
    public List<OneApiRequest> collectOneApiRequests(PsiClass psiClass, Project project) {
        AnnotationHolder psiClassHolder = AnnotationHolder.getPsiClassHolder(psiClass);
        CommentInfo commentInfo = psiClassHolder.getCommentInfo();
        boolean hidden = commentInfo.isHidden(false);
        if (hidden) {
            return Collections.emptyList();
        }
        String pid = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_PID.getTag(), "");

        String interfaceClassName = psiClass.getQualifiedName();
        List<OneApiRequest> requests = new ArrayList<>();

        List<PsiMethod> methods = getMethodList(psiClass);
        for (PsiMethod method : methods) {
            String actionName = getMethodActionName(method);
            if (StringUtils.isBlank(actionName)) {
                continue;
            }
            OneApiRequest request = createOneApiRequest(project, interfaceClassName, method, pid);
            if (request != null) {
                requests.add(request);
            }
        }
        return requests;
    }

    private OneApiRequest createOneApiRequest(Project project, String interfaceClassName, PsiMethod publicMethod, String pid) {
        Map<String, Object> param = new HashMap<>(16);
        if (StringUtils.isNotBlank(pid)) {
            param.put("pid", pid);
        }
        return getDataByMethod(project, interfaceClassName, publicMethod, param, false);
    }

    @Override
    protected OneApiRequest getDataByStructureAndCommentInfo(ApiMethodInfo apiMethodInfo, Map<String, Object> param) {
        PsiMethod publicMethod = apiMethodInfo.getPublicMethod();
        CommentInfo commentInfo = apiMethodInfo.getCommentInfo();
        String interfaceClassName = apiMethodInfo.getInterfaceClassName();
        StructureAndCommentInfo paramStructureAndCommentInfo = apiMethodInfo.getParamStructureAndCommentInfo();
        StructureAndCommentInfo returnStructureAndCommentInfo = apiMethodInfo.getReturnStructureAndCommentInfo();
        if (PluginSettingHelper.configNotExists()) {
            return null;
        }

        String methodName = publicMethod.getName();
        String interfaceName = commentInfo.getValue(methodName);
        String actionName = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_ACTION_NAME.getTag(), null);
        String dataSize = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_DATA_SIZE.getTag(), "2");
        String currentTag = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_CURRENT_TAG.getTag(), "default");
        String projectCode = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_PROJECT_CODE);
        String catalogId = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_CATALOG_ID);
        String cookie = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_COOKIE);
        String mode = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_MODE);
        String createUrl = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_CREATE_URL);
        String updateUrl = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_UPDATE_URL);
        String updateTagUrl = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_TAG_URL);
        boolean noTag = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_NO_TAG, false);
        boolean noMain = PluginSettingHelper.getConfigItem(PluginSettingEnum.ONE_API_NO_MAIN, false);

        String pid = param.getOrDefault("pid", "").toString();
        if (StringUtils.isNotBlank(pid)) {
            catalogId = pid;
        }

        if (StringUtils.isBlank(actionName)
                || StringUtils.isBlank(projectCode)
                || StringUtils.isBlank(catalogId)
                || StringUtils.isBlank(cookie)
        ) {
            return null;
        }

        String defaultTagName = "default";
        String apiName = actionName + ".json";

        Map<String, String> header = new LinkedHashMap<>(4);
        header.put("content-type", "application/json");
        header.put("accept", "application/json, text/json");
        header.put("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36");
        header.put("cookie", cookie);

        if (noMain && noTag) {
            return null;
        }

        Map<String, Object> returnJava2jsonMap = java2ComplexReader.read(returnStructureAndCommentInfo);
        //noinspection unchecked
        returnJava2jsonMap = (Map<String, Object>) returnJava2jsonMap.getOrDefault(MapKeyConstant.RETURN_FIELD_NAME, new HashMap<>());

        Map<String, Object> java2jsonMap = noMain ? Collections.emptyMap() : java2ComplexReader.read(paramStructureAndCommentInfo);
        boolean create = StringUtils.isBlank(mode) || "create".equals(mode);
        String mainBody = noMain ? null : createMainRequestBody(java2jsonMap, returnJava2jsonMap, interfaceName,
                projectCode, catalogId, create ? defaultTagName : currentTag, apiName);
        String tagBody = noTag ? null : createTagRequestBody(returnJava2jsonMap, projectCode, defaultTagName, apiName, dataSize);
        return new OneApiRequest(actionName, create ? createUrl : updateUrl, updateTagUrl, mainBody, tagBody, header);
    }

    public void upload(OneApiRequest request) {
        if (request.mainBody != null) {
            String mainResponse = HttpUtil.sendHttpWithBody(request.mainUrl, "POST", request.mainBody, request.header);
            showErrorTip(request.actionName, mainResponse, "create");
        }
        if (request.tagBody != null) {
            String tagResponse = HttpUtil.sendHttpWithBody(request.updateTagUrl, "POST", request.tagBody, request.header);
            showErrorTip(request.actionName, tagResponse, "update tag");
        }
    }

    public void upload(List<OneApiRequest> requests) {
        upload(requests, () -> {
        });
    }

    public void upload(List<OneApiRequest> requests, Runnable cancellationCheck) {
        for (OneApiRequest request : requests) {
            cancellationCheck.run();
            upload(request);
            cancellationCheck.run();
        }
    }

    private String createTagRequestBody(Map<String, Object> resultJava2json, String projectCode, String defaultTagName, String apiName, String dataSize) {
        Map<String, Object> data = getResultExample(resultJava2json, dataSize, true);
        Map<String, Object> tagResponse = new LinkedHashMap<>(8);
        tagResponse.put("successResponse", true);
        tagResponse.put("code", "200");
        tagResponse.put("data", data);
        tagResponse.put("requestId", "@guid");

        Map<String, Object> tagBody = new LinkedHashMap<>(8);
        tagBody.put("_csrf", "");
        tagBody.put("tagName", defaultTagName);
        tagBody.put("apiName", apiName);
        tagBody.put("projectCode", projectCode);
        tagBody.put("tagResponse", tagResponse);

        return toJson(tagBody);
    }

    private String toJson(Map<String, Object> tagBody) {
        TypeAdapter<ComplexInfo> typeAdapter = new TypeAdapter<>() {
            @Override
            public void write(JsonWriter jsonWriter, ComplexInfo complexInfo) throws IOException {
                if (complexInfo == null) {
                    jsonWriter.nullValue();
                    return;
                }
                CommentInfo commentInfo = complexInfo.getCommentInfo();
                if (commentInfo != null) {
                    String mockjs = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_MOCK_VAL.getTag(), "");
                    if (StringUtils.isNotBlank(mockjs)) {
                        jsonWriter.value(mockjs);
                        return;
                    }
                }
                Object realVal = complexInfo.getRealVal();
                if (realVal instanceof Boolean) {
                    Boolean val = (Boolean) realVal;
                    jsonWriter.value(val);
                } else if (realVal instanceof Long) {
                    Long val = (Long) realVal;
                    jsonWriter.value(val);
                } else if (realVal instanceof Double) {
                    Double val = (Double) realVal;
                    jsonWriter.value(val);
                } else if (realVal instanceof Number) {
                    Number val = (Number) realVal;
                    jsonWriter.value(val);
                } else {
                    jsonWriter.value(complexInfo.toString());
                }
            }

            @Override
            public ComplexInfo read(JsonReader jsonReader) throws IOException {
                return null;
            }
        };
        return JsonUtil.toJson(tagBody, (gsonBuilder -> {
            gsonBuilder.registerTypeAdapter(ComplexInfo.class, typeAdapter);
        }));
    }

    private String createMainRequestBody(Map<String, Object> java2json, Map<String, Object> resultJava2json, String interfaceName,
                                         String projectCode, String catalogId, String currentTag, String apiName) {
        List<Map<String, Object>> requestParamList = getRequestParamList(java2json);
        List<Map<String, Object>> responseParamList = getResponseParamList(resultJava2json);
        Map<String, Object> creator = new LinkedHashMap<>(32);
        creator.put("workNo", "SYSTEM");
        creator.put("name", "SYSTEM");
        creator.put("nickName", "SYSTEM");

        Map<String, Object> requestBody = new LinkedHashMap<>(32);
        requestBody.put("_csrf", "");
        requestBody.put("pid", Integer.parseInt(catalogId));
        requestBody.put("catalogId", Integer.parseInt(catalogId));
        requestBody.put("projectCode", projectCode);
        requestBody.put("apiName", apiName);
        requestBody.put("method", "ALL");
        requestBody.put("description", interfaceName);
        requestBody.put("currentTag", currentTag);
        requestBody.put("requestParams", requestParamList);
        requestBody.put("responseParams", responseParamList);
        requestBody.put("headerParams", new ArrayList<>());
        requestBody.put("delay", 0);
        requestBody.put("mockjs", true);
        requestBody.put("creator", creator);
        requestBody.put("modifier", creator);
        requestBody.put("gmtCreate", "2022-03-29T12:19:37.000Z");
        requestBody.put("gmtModified", "2022-03-29T12:19:37.000Z");

        return JsonUtil.toJson(requestBody);
    }

    private void showErrorTip(String actionName, String createRes, final String operate) {
        String content = null;
        if (!createRes.startsWith("{")) {
            content = actionName + " " + operate + " error, cause by " + createRes;
        } else {
            Map map = JsonUtil.fromJson(createRes, Map.class);
            if (map != null) {
                Object success = map.get("success");
                if (success instanceof Boolean) {
                    Boolean success0 = (Boolean) success;
                    if (success0) {
                        content = actionName + " " + operate + " success!";
                    } else {
                        content = actionName + " " + operate + " error, cause by " + createRes;
                    }
                }
            }
        }
        if (content != null) {
            NotificationUtil.showTips(content);
        }
    }

    @NotNull
    private Map<String, Object> getResultExample(Map<String, Object> resultJava2json, String dataSize, boolean needUpper) {
        Map<String, Object> map = new LinkedHashMap<>(8);

        for (Map.Entry<String, Object> entry : resultJava2json.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            getMapByVal(dataSize, map, key, value, needUpper);
        }

        return map;
    }

    private void getMapByVal(String dataSize, Map<String, Object> map, String key, Object value, boolean needUpper) {
        String upperKey = key;
        if (needUpper) {
            upperKey = key.substring(0, 1).toUpperCase() + key.substring(1);
        }

        if (value instanceof ComplexInfo) {
            ComplexInfo complexInfo = (ComplexInfo) value;
            FieldCommentInfo fieldCommentInfo = complexInfo.getFieldCommentInfo();
            CommentInfo commentInfo = complexInfo.getCommentInfo();
            if (commentInfo != null) {
                upperKey = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_FIELD.getTag(), upperKey);
                upperKey = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_MOCK_KEY.getTag(), upperKey);
            }
            map.put(upperKey, value);
        } else if (value instanceof Map) {
            Map<String, Object> mapValue = (Map<String, Object>) value;
            // only first level need upper
            map.put(upperKey, getResultExample(mapValue, dataSize, false));
        } else if (value instanceof List) {
            List list = (List) value;
            if (list.size() >= 1) {
                Object val = list.get(0);
                if (StringUtils.isNotBlank(dataSize)) {
                    list.clear();
                    int dataSize0 = Integer.parseInt(dataSize);
                    for (int i = 0; i < dataSize0; i++) {
                        Map<String, Object> map0 = new LinkedHashMap<>(8);
                        getMapByVal(dataSize, map0, key, val, false);
                        if (map0.keySet().size() > 0) {
                            String afterKey = new ArrayList<>(map0.keySet()).get(0);
                            Object afterVal = map0.get(afterKey);
                            if (afterVal != null) {
                                list.add(afterVal);
                            }
                        }
                    }
                    if (list.size() == 0) {
                        list.add(val);
                    }
                }
            }
            map.put(upperKey, list);
        }
    }

    @NotNull
    private List<Map<String, Object>> getResponseParamList(Map<String, Object> resultJava2json) {
        List<Map<String, Object>> responseList = new ArrayList<>();
        Map<String, Object> properties = new LinkedHashMap<>(2);
        for (Map.Entry<String, Object> entry : resultJava2json.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            String upperKey = key.substring(0, 1).toUpperCase() + key.substring(1);

            if (value instanceof ComplexInfo) {
                ComplexInfo complexInfo = (ComplexInfo) value;
                FieldCommentInfo fieldCommentInfo = complexInfo.getFieldCommentInfo();
                CommentInfo commentInfo = complexInfo.getCommentInfo();
                String example = fieldCommentInfo.getExample();
                String fieldDesc = fieldCommentInfo.getFieldDesc();
                if (StringUtils.isNotBlank(fieldDesc)) {
                    fieldDesc = fieldDesc.replaceAll(CommonConst.BREAK_LINE, "\n");
                }
                if (commentInfo != null) {
                    upperKey = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_FIELD.getTag(), upperKey);
                }

                Object realVal = complexInfo.getRealVal();

                String type = null;
                if (realVal instanceof Integer) {
                    type = "Number";
                } else if (realVal instanceof Long) {
                    type = "Number";
                } else if (realVal instanceof Float) {
                    type = "Number";
                } else if (realVal instanceof Double) {
                    type = "Number";
                } else if (realVal instanceof Boolean) {
                    type = "Boolean";
                } else if (realVal instanceof String) {
                    type = "String";
                }
                if (type != null) {
                    responseList.add(getResponse(upperKey, type, fieldDesc));
                }
            } else if (value instanceof List) {
                responseList.add(getResponse(upperKey, "List", "详见Mock数据以及语雀文档"));
            }
        }
        return responseList;
    }

    @NotNull
    private List<Map<String, Object>> getRequestParamList(Map<String, Object> java2json) {
        Set<String> existsParam = initExistsParam();
        List<Map<String, Object>> requestParamList = new ArrayList<>();
        addParameterByConfig(existsParam, requestParamList, PluginSettingEnum.ONE_API_PARAM_REQ);

        for (Map.Entry<String, Object> entry : java2json.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof ComplexInfo) {
                ComplexInfo complexInfo = (ComplexInfo) value;
                CommentInfo commentInfo = complexInfo.getCommentInfo();
                Object realVal = complexInfo.getRealVal();
                FieldCommentInfo fieldCommentInfo = complexInfo.getFieldCommentInfo();
                String example = fieldCommentInfo.getExample();
                String fieldDesc = fieldCommentInfo.getFieldDesc();
                if (StringUtils.isNotBlank(fieldDesc)) {
                    fieldDesc = fieldDesc.replaceAll(CommonConst.BREAK_LINE, "\n");
                }

                String require = "false";
                String upperKey = key.substring(0, 1).toUpperCase() + key.substring(1);
                if (commentInfo != null) {
                    upperKey = commentInfo.getSingleStr(MoreCommentTagEnum.AMP_FIELD.getTag(), upperKey);
                    require = String.valueOf(commentInfo.isRequired(false));
                }

                String afterIgnoreCase = upperKey.toLowerCase();
                if (existsParam.contains(afterIgnoreCase)) {
                    continue;
                }
                existsParam.add(afterIgnoreCase);

                String type;

                if (realVal instanceof Integer) {
                    type = "Number";
                } else if (realVal instanceof Long) {
                    type = "Number";
                } else if (realVal instanceof Float) {
                    type = "Number";
                } else if (realVal instanceof Double) {
                    type = "Number";
                } else if (realVal instanceof Boolean) {
                    type = "Boolean";
                } else if (realVal instanceof String) {
                    type = "String";
                } else {
                    continue;
                }
                requestParamList.add(getParameter(upperKey, type, require, fieldDesc));
            }

        }
        return requestParamList;
    }

    @NotNull
    private Set<String> initExistsParam() {
        Set<String> param = new HashSet<>(8);
        param.add("callertype");
        param.add("calleruid");
        return param;
    }

    private void addParameterByConfig(Set<String> existsParam, List<Map<String, Object>> parameterList, PluginSettingEnum configKey) {
        Set<String> systemKeySet = new LinkedHashSet<>();
        String systemParamKey = PluginSettingHelper.getConfigItem(configKey);
        if (StringUtils.isNotBlank(systemParamKey)) {
            String[] systemKeyArray = systemParamKey.split(",");
            systemKeySet.addAll(Arrays.asList(systemKeyArray));
        }

        for (String systemKey : systemKeySet) {
            String[] array = systemKey.split("#");
            if (array.length > 2) {
                String key = array[0];
                String type = array[1];
                String require = array[2];

                String afterIgnoreCase = key.toLowerCase();
                if (existsParam.contains(afterIgnoreCase)) {
                    continue;
                }
                existsParam.add(afterIgnoreCase);

                String title = null;
                if (array.length > 3) {
                    title = array[3];
                }
                parameterList.add(getParameter(key, type, require, title));
            }
        }
    }

    private Map<String, Object> getResponse(String key, String type, String title) {
        Map<String, Object> response = new LinkedHashMap<>(8);
        response.put("name", key);
        response.put("type", type);
        response.put("description", title);
        return response;
    }

    @NotNull
    private Map<String, Object> getParameter(String key, String type, String require, String title) {
        Map<String, Object> parameter = new LinkedHashMap<>(32);
        parameter.put("require", Boolean.parseBoolean(require));
        parameter.put("name", key);
        parameter.put("type", type);
        parameter.put("description", title);
        return parameter;
    }

    public static class OneApiRequest {

        private final String actionName;
        private final String mainUrl;
        private final String updateTagUrl;
        private final String mainBody;
        private final String tagBody;
        private final Map<String, String> header;

        private OneApiRequest(String actionName, String mainUrl, String updateTagUrl, String mainBody, String tagBody,
                              Map<String, String> header) {
            this.actionName = actionName;
            this.mainUrl = mainUrl;
            this.updateTagUrl = updateTagUrl;
            this.mainBody = mainBody;
            this.tagBody = tagBody;
            this.header = header;
        }
    }

}
