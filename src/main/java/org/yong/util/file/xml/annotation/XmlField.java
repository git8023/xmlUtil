package org.yong.util.file.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 属性映射, 实体属性可以映射为XML子标签或者属性.
 * 默认映射属性值, 且属性名与字段名一致
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface XmlField {

    /**
     * 获取被标记属性XML映射属性名或子标签名.<br>
     * 默认: 字段名
     */
    String name() default "";

    /**
     * 字段映射类型.<br>
     * 默认: 属性
     */
    FieldType type() default FieldType.ATTRIBUTE;

    /**
     * 设置路径会在解析 {@link #name() name} 之前跳转到指定子标签.
     */
    String[] path() default {};
}
