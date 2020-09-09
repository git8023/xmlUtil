package org.yong.util.file.xml.annotation;

/**
 * 字段映射类型
 */
public enum FieldType {

    /**
     * 字段映射为标签属性. 值为复杂属性时属性值为JSON格式
     */
    ATTRIBUTE,

    /**
     * 字段映射为子标签(体)
     */
    TAG

}
