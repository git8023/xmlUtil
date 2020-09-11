package org.yong.util.file.xml.parser;

import org.yong.util.file.xml.parser.iface.SimpleValueParser;

import java.util.HashMap;

/**
 * 字段值解析器工厂, 通过类型获取解析器.
 *
 * @version 1.3
 */
public class FieldValueParserFactory {

    private static final HashMap<Class<?>, SimpleValueParser<?>> STORE = new HashMap<>();
    private static final SimpleValueParser<?> DEFAULT = new DefaultSimpleValueParser();

    /**
     * 通过类型获取解析器
     *
     * @param type 类型
     * @return 解析器
     */
    public static SimpleValueParser<?> getFactory(Class<?> type) {
        return STORE.getOrDefault(type, DEFAULT);
    }

    /**
     * 注册解析器
     *
     * @param parser 解析器
     * @param <T>    解析器泛型类型
     */
    public static <T> void reg(SimpleValueParser<T> parser) {
        Class<T> preciseType = parser.getPreciseType();
        if (null != preciseType)
            STORE.put(preciseType, parser);
    }

}
