package org.yong.util.file.xml.parser;

import org.yong.util.file.xml.parser.iface.SimpleValueParser;

/**
 * JSON解析器, 默认支持所有操作
 */
public class DefaultSimpleValueParser implements SimpleValueParser<Object> {

    @Override
    public boolean isSupport(Class<Object> type) {
        return true;
    }

    @Override
    public Object fromXml(Class<?> type, String value) {
        return SimpleValueParser.super.fromXml(type, value);
    }

    @Override
    public String fromBean(Object value) {
        return SimpleValueParser.super.fromBean(value);
    }
}
