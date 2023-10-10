package cn.gudqs7.plugins.common.util;

import com.google.gson.GsonBuilder;

import java.util.function.Consumer;

/**
 * @author wenquan
 * @date 2022/3/31
 */
public class JsonUtil {

    public static <T> T fromJson(String jsonString, Class<T> aClass) {
        return JsonUtils.toJavaObject(jsonString, aClass);
//        Gson gson = new Gson();
//        return gson.fromJson(jsonString, aClass);
    }

    public static <T> String toJson(T value) {
        return JsonUtils.toJsonString(value);
//        return toJson(value, gsonBuilder -> {
//        });
    }

    public static <T> String toJson(T value, Consumer<GsonBuilder> gsonBuilderConsumer) {
        return JsonUtils.toJsonString(value);
//        GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
//        gsonBuilderConsumer.accept(gsonBuilder);
//        Gson gson = gsonBuilder.create();
//        return gson.toJson(value);
    }

}
