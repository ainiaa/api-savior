package cn.gudqs7.plugins.common.util;

import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonUtilTest {

    @Test
    void appliesCustomGsonConfiguration() {
        String json = JsonUtil.toJson(new Value("original"), gsonBuilder ->
                gsonBuilder.registerTypeAdapter(Value.class,
                        (JsonSerializer<Value>) (value, type, context) -> new JsonPrimitive("custom")));

        assertEquals("\"custom\"", json);
    }

    private static final class Value {
        private final String value;

        private Value(String value) {
            this.value = value;
        }
    }
}
