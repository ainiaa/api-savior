package cn.gudqs7.plugins;

import org.junit.jupiter.api.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PluginRuntimeContractTest {

    @Test
    void loadsKeyIdeaExtensionsFromPluginDescriptor() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/META-INF/plugin.xml")) {
            assertNotNull(input);
            org.w3c.dom.Document descriptor = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(input);
            assertEquals("211.6693.111", descriptor.getElementsByTagName("idea-version")
                    .item(0).getAttributes().getNamedItem("since-build").getNodeValue());
            assertEquals("true", projectServiceAttribute(descriptor,
                    "cn.gudqs7.plugins.search.ApiIndexService", "preload"));
        }

        for (String className : Arrays.asList(
                "cn.gudqs7.plugins.search.ApiSearchContributor",
                "cn.gudqs7.plugins.search.ApiIndexService",
                "cn.gudqs7.plugins.generate.postfix.GeneratePostfixTemplateProvider",
                "cn.gudqs7.plugins.savior.action.restful.RestfulDocSaviorAction",
                "cn.gudqs7.plugins.savior.action.batch.HtmlDocerSaviorAction")) {
            assertDoesNotThrow(() -> Class.forName(className));
        }
    }

    private String projectServiceAttribute(org.w3c.dom.Document descriptor, String implementation, String attribute) {
        org.w3c.dom.NodeList services = descriptor.getElementsByTagName("projectService");
        for (int index = 0; index < services.getLength(); index++) {
            org.w3c.dom.Node service = services.item(index);
            org.w3c.dom.Node implementationAttribute = service.getAttributes().getNamedItem("serviceImplementation");
            if (implementation.equals(implementationAttribute.getNodeValue())) {
                return service.getAttributes().getNamedItem(attribute).getNodeValue();
            }
        }
        return null;
    }
}
