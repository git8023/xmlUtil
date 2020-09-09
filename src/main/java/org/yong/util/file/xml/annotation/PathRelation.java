package org.yong.util.file.xml.annotation;

/**
 * XML层级关系类型
 */
public enum PathRelation {

    /**
     * 当前标签
     */
    CURRENT,

    /**
     * 父标签
     */
    PARENT,

    /**
     * 同级标签
     */
    SIBLING,

    /**
     * 子标签
     */
    CHILDREN
}
