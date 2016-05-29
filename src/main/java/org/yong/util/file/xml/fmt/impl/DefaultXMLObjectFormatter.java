package org.yong.util.file.xml.fmt.impl;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.fmt.XMLObjectFormatter;
import org.yong.util.string.StringUtil;

public class DefaultXMLObjectFormatter implements XMLObjectFormatter {

	private static final String NEW_LINE = "\n";

	/** 单个缩进位 */
	private static final String RETRACT_VALUE = "    ";

	private String newLine;

	/** 排版规则:true-紧缩的, false-缩进的 */
	private boolean compact;

	/** 当前节点层次 */
	private int nodeLevel = 0;

	/**
	 * @Title: DefaultXMLObjectFormatter
	 * @Description: 获取新实例
	 * @param compact true-紧缩排版的, false-缩进排版的
	 */
	public DefaultXMLObjectFormatter(boolean compact) {
		this.compact = compact;
		this.newLine = (compact ? StringUtil.EMPTY_STRING : NEW_LINE);
	}

	/**
	 * @Title: getNewLine
	 * @Description: 获取换行符
	 * @return String 换行符
	 */
	public String getNewLine() {
		return newLine;
	}

	@Override
	public StringBuilder format(XMLObject xmlObject) {
		StringBuilder content = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		String currentNewLine = getNewLine();
		format(xmlObject, content, currentNewLine);
		return content;
	}

	/**
	 * @Title: format
	 * @Description: 格式化指定节点
	 * @param xmlObject 节点对象
	 * @param contentRepository 用于保存格式化内容的容器
	 * @param currentNewLine
	 */
	private void format(XMLObject xmlObject, StringBuilder contentRepository, String currentNewLine) {
		// 获取缩进占位符, retract 与 nodeLevel 相关
		String retact = createRetract();

		// 构建标签头, 总是以空格结尾: "[retract] + <[tagName] "
		contentRepository.append(retact).append(createTagStart(xmlObject));

		// 构建标签属性 : "[attrName='value'] [attrName='value'] ... >"
		contentRepository.append(createAttrs(xmlObject));

		// 追加行结束符号 : [NEW_LINE]
		contentRepository.append(currentNewLine);

		// 构建标签体, [retract] + [content] + [NEW_LINE]
		String content = xmlObject.getContent();
		contentRepository.append(createContent(content));

		Map<String, List<XMLObject>> childTags = xmlObject.getChildTags();
		// 如果不存在子标签和者标签体
		if (MapUtils.isEmpty(childTags) && StringUtil.isEmpty(content, true)) {
			// 在最后一个 ">" 前面插入 "/", 使标签闭合
			int idx = contentRepository.lastIndexOf(">");
			contentRepository.insert(idx, "/");
			// 结束程序
			return;
		}

		// 如果存在子标签
		for (Entry<String, List<XMLObject>> me : childTags.entrySet()) {
			// 标签层级 : nodeLevel+1
			this.nodeLevel++;

			List<XMLObject> childrenOfEach = me.getValue();
			for (XMLObject child : childrenOfEach) {
				// 递归构建子标签
				format(child, contentRepository, currentNewLine);
			}

			// 标签层级 : nodeLevel-1
			this.nodeLevel--;
		}

		// 构建标签尾 "[retract] + </[tagName]>"
		contentRepository.append(retact).append(createTagEnd(xmlObject)).append(currentNewLine);
	}

	/**
	 * @Title: createContent
	 * @Description: 创建标签体
	 * @param content 标签体的值
	 * @return String XML文件中的标签体值
	 */
	private String createContent(String content) {
		StringBuilder ctt = new StringBuilder();

		if (StringUtil.isNotEmpty(content, true)) {
			ctt.append(createRetract());
			if (!this.compact) {
				ctt.append(DefaultXMLObjectFormatter.RETRACT_VALUE);
			}
			ctt.append(content).append(getNewLine());
		}

		return ctt.toString();
	}

	/**
	 * @Title: createTagEnd
	 * @Description: 创建标签结束字符串
	 * @param xmlObject 节点对象
	 * @return String 标签结束字符串
	 */
	private String createTagEnd(XMLObject xmlObject) {
		return "</" + xmlObject.getTagName() + ">";
	}

	/**
	 * @Title: createAttrs
	 * @Description: 创建属性字符串([attrName]=[attrValue] [attrName]=[attrValue] ... )
	 * @param xmlObject 节点对象
	 * @return String 属性字符串
	 */
	private String createAttrs(XMLObject xmlObject) {
		StringBuilder attrContent = new StringBuilder();

		Map<String, String> attrs = xmlObject.getAttrs();
		if (MapUtils.isNotEmpty(attrs)) {
			for (Entry<String, String> me : attrs.entrySet()) {

				String attrName = StringUtils.trimToEmpty(me.getKey());
				if (StringUtil.isNotEmpty(attrName, true)) {
					String attrVal = StringUtils.trimToEmpty(me.getValue());
					attrContent.append(" ").append(attrName).append("=").append("\"").append(attrVal).append("\"");
				}
			}

		}

		attrContent.append(">");
		return attrContent.toString();
	}

	/**
	 * @Title: createTagStart
	 * @Description: 创建标签开始字符串(&lt;[TagName] )
	 * @param xmlObject 节点对象
	 * @return String 标签开始字符串
	 */
	private String createTagStart(XMLObject xmlObject) {
		return "<" + xmlObject.getTagName();
	}

	/**
	 * @Title: createRetract
	 * @Description: 创建缩进位字符串
	 * @return String 缩进位字符串
	 */
	private String createRetract() {
		String retract = StringUtil.EMPTY_STRING;
		if (!this.compact) {
			for (int i = 0; i < this.nodeLevel; i++) {
				retract += DefaultXMLObjectFormatter.RETRACT_VALUE;
			}
		}
		return retract;
	}
}
