package cn.gudqs7.plugins.common.util.structure;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wq
 */
public class ResolverContextHolder {

    public static final String HIDDEN_KEYS = "hiddenKeyList";
    public static final String ONLY_KEYS = "onlyKeyList";

    private static final ThreadLocal<Map<String, Object>> CONTEXT_DATA = ThreadLocal.withInitial(HashMap::new);

    public static <T> void addData(String key, T data) {
        CONTEXT_DATA.get().put(key, data);
    }
    
    public static void removeAll() {
        CONTEXT_DATA.remove();
    }

    public static <T> T getData(String key) {
        return (T) CONTEXT_DATA.get().get(key);
    }


}
