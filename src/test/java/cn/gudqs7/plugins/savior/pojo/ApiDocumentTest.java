package cn.gudqs7.plugins.savior.pojo;

import cn.gudqs7.plugins.common.util.file.FreeMarkerUtil;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiDocumentTest {

    @Test
    void rendersFreemarkerTemplateWithoutMapBackedModel() {
        ApiDocument document = new ApiDocument();
        document.setInterfaceName("Query user");
        document.setInterfaceNotes("");
        document.setQualifiedMethodName("sample.UserApi#query");
        document.setUrl("/users/{id}");
        document.setMethod("GET");
        document.setContentType("application/json");
        document.setJsonExample("{}");
        document.setReturnJsonExample("{}");
        document.setParamLevelMap(Collections.emptyMap());
        document.setReturnLevelMap(Collections.emptyMap());
        document.setResponseCodeInfoList(Collections.emptyList());

        String rendered = FreeMarkerUtil.renderTemplate("restful/method.ftl", document);

        assertTrue(rendered.contains("Query user"));
        assertTrue(rendered.contains("/users/{id}"));
    }

    @Test
    void rendersSmallUtf8TemplateWithoutDependingOnPlatformDefaultCharset() {
        assertTrue(FreeMarkerUtil.renderTemplate("encoding/utf8.ftl", Collections.emptyMap()).contains("中文内容"));
    }
}
