package cn.gudqs7.plugins.common.enums;


import java.util.*;

/**
 * 枚举类父类，code为String类型
 *
 * @author Jeff.Liu
 */
public interface StringEnumInterface extends EnumInterface {

    /**
     * 枚举类获取String类型code的方法
     *
     * @return xx
     */
    String getCode();

    /**
     * 根据code值获取对应的枚举类值
     *
     * @param code       枚举code值
     * @param defaultVal 默认值，不能为空
     * @param <E>        具体的枚举类类型
     *
     * @return 找不到返回默认值(defaultVal)
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & StringEnumInterface> E get(String code, E defaultVal) {
        if (defaultVal == null) {
            return null;
        }
        for (Enum<E> e : defaultVal.getClass().getEnumConstants()) {
            if (Objects.equals(((StringEnumInterface) e).getCode(), code)) {
                return (E) e;
            }
        }

        return defaultVal;
    }

    /**
     * 根据code值获取对应的枚举类描述
     *
     * @param code  枚举code值
     * @param clazz 默认值，不能为空
     * @param <E>   具体的枚举类类型
     *
     * @return 找不到返回默认值(defaultVal)
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & StringEnumInterface> String getDescByCode(String code, Class<E> clazz) {
        if (clazz == null) {
            return null;
        }
        for (E e : clazz.getEnumConstants()) {
            if (Objects.equals(e.getCode(), code)) {
                return e.getDesc();
            }
        }

        return "";
    }

    /**
     * 根据code值获取对应的枚举类值
     *
     * @param code  枚举code值
     * @param clazz 具体枚举类，不能为空
     * @param <E>   具体的枚举类类型
     *
     * @return 找不到返回空值(null)
     */
    static <E extends Enum<E> & StringEnumInterface> E get(String code, Class<E> clazz) {
        if (clazz == null) {
            return null;
        }
        for (E e : clazz.getEnumConstants()) {
            if (Objects.equals(e.getCode(), code)) {
                return e;
            }
        }
        return null;
    }

    /**
     * 根据code值获取对应的枚举类值
     *
     * @param code 枚举code值
     * @param e    具体枚举类，不能为空
     *
     * @return 找不到返回空值(null)
     */
    static boolean is(String code, StringEnumInterface e) {
        if (e == null) {
            return false;
        }
        return code.equals(e.getCode());
    }

    static <E extends Enum<E> & StringEnumInterface> Map<String, String> toMap(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyMap();
        }
        Map<String, String> map = new HashMap<>();
        for (E e : clazz.getEnumConstants()) {
            map.put(e.getCode(), e.getDesc());
        }
        return map;
    }

    /**
     * @param clazz
     * @param <E>
     *
     * @return
     */
    static <E extends Enum<E> & StringEnumInterface> List<Map<String, String>> toListMap(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<Map<String, String>> list = new ArrayList<>();
        for (E e : clazz.getEnumConstants()) {
            Map<String, String> map = new LinkedHashMap<>();
            map.put("code", e.getCode());
            map.put("desc", e.getDesc());
            list.add(map);
        }
        return list;
    }
}
