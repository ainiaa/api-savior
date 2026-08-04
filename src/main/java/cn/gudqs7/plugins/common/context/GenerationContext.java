package cn.gudqs7.plugins.common.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/** Immutable settings snapshot owned by one document-generation task. */
public final class GenerationContext {

    private static final GenerationContext EMPTY = new GenerationContext(Collections.emptyMap());

    private final Map<String, String> settings;

    private GenerationContext(Map<String, String> settings) {
        this.settings = Collections.unmodifiableMap(new HashMap<>(settings));
    }

    public static GenerationContext empty() {
        return EMPTY;
    }

    public static GenerationContext of(Map<String, String> settings) {
        return settings == null || settings.isEmpty() ? EMPTY : new GenerationContext(settings);
    }

    public boolean isEmpty() {
        return settings.isEmpty();
    }

    public String get(String key, String defaultValue) {
        return settings.getOrDefault(key, defaultValue);
    }

    public Map<String, String> getSettings() {
        return settings;
    }
}
