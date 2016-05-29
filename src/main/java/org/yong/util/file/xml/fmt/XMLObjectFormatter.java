package org.yong.util.file.xml.fmt;

import org.yong.util.file.xml.XMLObject;

/**
 * @Author Huang.Yong
 * @Description: {@link XMLObject}格式化接口
 * @Date 2016年5月29日 上午10:44:06
 * @Version 0.1
 */
public interface XMLObjectFormatter {

	/**
	 * @Title: format
	 * @Description: 格式化指定节点中的所有内容(包括: TagName, Content, children)
	 * @param xmlObject 需要格式化的节点
	 * @return StringBuilder 格式化后的内容
	 */
	public StringBuilder format(XMLObject xmlObject);

}
