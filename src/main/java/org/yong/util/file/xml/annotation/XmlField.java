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
     * 指明 {@link #path() path} 元素层级关系
     * <pre>
     * &lt;root>
     *   &lt;first attr="f_1" />
     *   &lt;first attr="f_2">
     *     &lt;second target_attr="sec_1">
     *   &lt;/first>
     * &lt;/root>
     *
     * // 当前标签: root
     * //    目标: second 标签的(attr)属性值设置到 secondAttrVal
     * {@code @XmlField(
     *            name="target_attr",
     *            path={"first[1]", "second"},
     *            hierarchyTypes={PathRelation.CHILDREN, PathRelation.CHILDREN}
     *        )}
     * private String secondAttrVal;
     * </pre>
     * <p>
     * 此属性需要 {@link #path() path} 配合使用, 配置长度必须与 {@link #path() path} 一致.
     *
     * FIXME 逻辑未支持 hierarchy
     */
    PathRelation[] hierarchy() default PathRelation.CURRENT;

    /**
     * 设置路径会在解析 {@link #name() name} 之前跳转到指定路径.
     * <br>此属性需要 {@link #hierarchy() hierarchy} 配合使用, 元素个数必须和 {@link #hierarchy() hierarchy} 一致.
     * FIXME 逻辑未支持 path
     */
    String[] path() default "";
}
