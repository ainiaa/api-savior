package cn.gudqs7.plugins.common.enums;


import java.util.*;

/**
 * 枚举类父类，code为Integer类型
 *
 * @author pb
 */
public interface IntegerEnumInterface extends EnumInterface {

    /**
     * 枚举类获取Integer类型code的方法
     *
     * @return xx
     */
    Integer getCode();

    static <E extends Enum<E> & IntegerEnumInterface> List<Integer> getCodeList(Class<E> clazz) {

        List<Integer> result = new ArrayList<>();
        for (E e : clazz.getEnumConstants()) {
            result.add(e.getCode());
        }

        return result;
    }

    /**
     * 根据code值获取对应的枚举类值
     *
     * @param code       枚举code值
     * @param defaultVal 默认值，不能为空
     * @param <E>        具体的枚举类类型
     * @return 找不到返回默认值(defaultVal)
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & IntegerEnumInterface> E get(Integer code, E defaultVal) {
        if (defaultVal == null) {
            return null;
        }
        for (Enum<E> e : defaultVal.getClass().getEnumConstants()) {
            if (Objects.equals(((IntegerEnumInterface) e).getCode(), code)) {
                return (E) e;
            }
        }

        return defaultVal;
    }

    /**
     * 根据code值获取对应的枚举类值
     *
     * @param code  枚举code值
     * @param clazz 具体枚举类，不能为空
     * @param <E>   具体的枚举类类型
     * @return 找不到返回空值(null)
     */
    static <E extends Enum<E> & IntegerEnumInterface> E get(Integer code, Class<E> clazz) {
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
     * 根据code值获取对应的枚举类描述
     *
     * @param code       枚举code值
     * @param clazz 默认值，不能为空
     * @param <E>        具体的枚举类类型
     * @return 找不到返回默认值(defaultVal)
     */
    @SuppressWarnings("unchecked")
    static <E extends Enum<E> & IntegerEnumInterface> String getDescByCode(Integer code, Class<E> clazz) {
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

    static <E extends Enum<E> & IntegerEnumInterface> Map<Integer, String> toMap(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyMap();
        }
        Map<Integer, String> map = new HashMap<>();
        for (E e : clazz.getEnumConstants()) {
            map.put(e.getCode(), e.getDesc());
        }
        return map;
    }

    /**
     *
     * @param clazz
     * @param <E>
     * @return
     */
    static <E extends Enum<E> & IntegerEnumInterface> List<Map<String, Object>> toListMap(Class<E> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (E e : clazz.getEnumConstants()) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("code", e.getCode());
            map.put("desc", e.getDesc());
            list.add(map);
        }
        return list;
    }

}
