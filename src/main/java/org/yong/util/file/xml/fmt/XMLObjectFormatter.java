package org.yong.util.file.xml.fmt;

import org.yong.util.file.xml.XMLObject;

/**
 * {@link XMLObject}格式化接口
 *
 * @author Huang.Yong
 */
public interface XMLObjectFormatter {

    /**
     * 格式化指定节点中的所有内容(包括: TagName, Content, children)
     *
     * @param xmlObject 需要格式化的节点
     * @return StringBuilder 格式化后的内容
     */
    StringBuilder format(XMLObject xmlObject);

}
