package org.yong.util.file.xml.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 指明实体类可以被映射为XML文档描述
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XmlTag {

    /**
     * 当指定当前值时在执行反序列化时必须检测目标标签和当前值是否完全一致.
     * 如果不一致需要抛出异常.
     *
     * @return 指定需要映射的标签名称
     */
    String value() default "";

}
