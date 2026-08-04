package cn.gudqs7.plugins.common.context;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GenerationContextTest {

    @Test
    void snapshotsSettingsForTheWholeGeneration() {
        Map<String, String> settings = new HashMap<>();
        settings.put("host", "before");

        GenerationContext context = GenerationContext.of(settings);
        settings.put("host", "after");

        assertEquals("before", context.get("host", "default"));
        assertThrows(UnsupportedOperationException.class, () -> context.getSettings().put("host", "changed"));
    }
}
