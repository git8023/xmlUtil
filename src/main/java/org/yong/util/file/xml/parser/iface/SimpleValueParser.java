package org.yong.util.file.xml.parser.iface;

import com.alibaba.fastjson.JSON;
import org.yong.util.common.StringUtil;

import java.lang.reflect.Type;

/**
 * 字段值解析器接口, 实现字段值和字符串之间转换工作.
 * 接口实现需要在调用XML映射 {@link org.yong.util.file.xml.XMLObject#toBean(Class) 单个实例}
 * (或 {@link org.yong.util.file.xml.XMLObject#toBeans(String, Class) 实例列表})
 * 之前 {@link org.yong.util.file.xml.parser.FieldValueParserFactory#reg(SimpleValueParser) 手动注册}
 *
 * @param <T> 解析器支持的泛型类型
 * @version 1.3
 */
public interface SimpleValueParser<T> {

    /**
     * 判断是否支持type类型
     *
     * @param type 字段类型
     * @return 支持该类型转换返回true, 否则返回false
     */
    default boolean isSupport(Class<T> type) {
        return false;
    }

    /**
     * 获取精准支持类型, 该类型可以加快查找速度. 如果返回null则需要通过
     * {@link #isSupport(Class)} 确认是否匹配.
     *
     * @return 精准支持类型
     */
    default Class<T> getPreciseType() {
        return null;
    }

    /**
     * 解析XML值, 作用于数据字段值回填
     *
     * @param type  目标类型
     * @param value 字符串类型值
     * @return 解析结果
     */
    @SuppressWarnings({"unchecked"})
    default T fromXml(Class<?> type, String value) {
        if (String.class == type)
            return (T) value;
        return JSON.parseObject(value, (Type) type);
    }

    /**
     * 解析数据对象字段值, 作用于XML标签属性名(标签体)
     *
     * @param value 字段值
     * @return XML标签属性名(标签体)
     */
    default <D> String fromBean(D value) {
        if (null == value)
            return StringUtil.EMPTY;

        if (value instanceof CharSequence)
            return String.valueOf(value);

        return JSON.toJSONString(value);
    }
}
