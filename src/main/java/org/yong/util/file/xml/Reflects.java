package org.yong.util.file.xml;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class Reflects {

    private static final Set<Class<?>> SIMPLE_TYPES = Sets.newHashSet();

    static {
        SIMPLE_TYPES.addAll(Arrays.asList(
                Integer.class,
                Short.class,
                Long.class,
                Float.class,
                Double.class,
                Character.class,
                Byte.class,
                Boolean.class,

                String.class,
                Date.class
        ));
    }

    public enum CollectionType {
        /**
         * 数组
         */
        ARRAY,
        /**
         * List列表
         */
        LIST,
        /**
         * Set集合
         */
        SET
    }

    /**
     * 设置值
     *
     * @param bean    数据对象
     * @param field   字段
     * @param jsonStr json值
     * @param <T>     对象类型
     */
    public static <T> void setJsonValue(T bean, Field field, String jsonStr) throws IllegalAccessException {
        if (!field.isAccessible())
            field.setAccessible(true);

        // 默认为字符串值
        Object val = jsonStr;
        if (null == val)
            return;

        // 非字符串需要JSON解析
        Class<?> type = field.getType();
        if (String.class != type)
            val = JSON.parseObject(jsonStr, type);

        if (null != val)
            field.set(bean, val);
    }

    /**
     * 校验指定字节码是否为简单类型: int/short/long, float/double, char/byte/boolean, String, Date
     *
     * @param cls 字节码
     * @return true-是简单数据类型, false-复杂数据类型
     */
    public static boolean isSimpleType(Class<?> cls) {
        return cls.isPrimitive() || SIMPLE_TYPES.contains(cls);
    }

    /**
     * 校验指定字节码是否为集合类型: List, Set, T[]
     *
     * @param cls 字节码
     * @return 如果是集合类型返回true, 否则返回false
     */
    public static CollectionType isCollection(Class<?> cls) {
        if (cls.isArray())
            return CollectionType.ARRAY;

        if (List.class.isAssignableFrom(cls))
            return CollectionType.LIST;

        if (Set.class.isAssignableFrom(cls))
            return CollectionType.SET;

        return null;
    }

    /**
     * 获取指定类公开/继承/私有字段
     *
     * @param cls 目标类字节码
     * @param <T> 目标类类型
     * @return 字段列表
     */
    public static <T> List<Field> getFields(Class<T> cls) {
        List<Field> fields = Lists.newArrayList(Arrays.asList(cls.getFields()));
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        return fields;
    }

    /**
     * 校验指定字段是否静态或者常量
     *
     * @param field 目标字段
     * @return 静态或常量返回true, 否则返回false
     */
    public static boolean isStaticOrFinal(Field field) {
        int modifiers = field.getModifiers();
        return Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers);
    }
}
