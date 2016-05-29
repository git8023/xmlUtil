package org.yong.util.file.xml.fmt;

import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.fmt.impl.DefaultXMLObjectFormatter;

/**
 * @author Huang.Yong
 * @Description: {@link XMLObject}格式化工厂
 * @Date 2016年5月29日 上午10:42:34
 * @Version 0.1
 */
public class XMLObjectFormatterFactory {

	/**
	 * @Title: createFormatter
	 * @Description: 创建格式化工具
	 * @param compact true-紧缩排版的, false-缩进排版的
	 * @return XMLObjectFormatter 格式化工具
	 */
	public static XMLObjectFormatter createFormatter(boolean compact) {
		return new DefaultXMLObjectFormatter(compact);
	}

}
