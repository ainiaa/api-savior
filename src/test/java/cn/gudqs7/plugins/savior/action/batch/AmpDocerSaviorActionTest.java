package cn.gudqs7.plugins.savior.action.batch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AmpDocerSaviorActionTest {

    @Test
    void generateYamlKeepsApisAsTopLevelValue() throws Exception {
        Map<String, Object> apis = new LinkedHashMap<>();
        apis.put("/users", Collections.singletonMap("get", "list"));

        String yaml = AmpDocerSaviorAction.generateYaml(apis);
        JsonNode root = new YAMLMapper().readTree(yaml);

        assertEquals("list", root.path("apis").path("/users").path("get").asText());
    }
}
