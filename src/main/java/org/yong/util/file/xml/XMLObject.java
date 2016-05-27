package org.yong.util.file.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.yong.util.string.StringUtil;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @Author Huang.Yong
 * @Description: XML文件对象
 */
public class XMLObject implements Serializable {

	// @Fields serialVersionUID :
	private static final long serialVersionUID = 7702755997734263716L;

	/** 标签名 */
	private String tagName;

	/** 标签文本内容 */
	private String content;

	/** 属性列表 */
	private Map<String, String> attrs = new HashMap<String, String>();

	/** 子标签集合, Key:tagName, Value:List&lt;XMLObject&gt; */
	private Map<String, List<XMLObject>> childTags = new LinkedHashMap<String, List<XMLObject>>();

	/**
	 * @Title: XMLObject
	 * @Description: 构建XML对象
	 * @param tagName 标签名
	 */
	XMLObject(String tagName) {
		super();
		this.tagName = tagName;
	}

	/**
	 * @Title: getTagName
	 * @Description: 获取当前标签名
	 * @return String 标签名
	 */
	public String getTagName() {
		return tagName;
	}

	/**
	 * @Title: setTagName
	 * @Description: 设置当前标签名
	 * @param tagName 当前标签名
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @Title: getContent
	 * @Description: 获取当前标签文本内容
	 * @return String 存文本内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @Title: setContent
	 * @Description: 设置标签文本内容
	 * @param content 标签存文本内容
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @Title: getAttrs
	 * @Description: 获取标签属性列表
	 * @return Map&lt;String,String&gt; 标签属性集合, Key:属性名, Value:属性值
	 */
	public Map<String, String> getAttrs() {
		if (null == this.attrs) {
			this.attrs = new HashMap<String, String>();
		}
		return this.attrs;
	}

	/**
	 * @Title: getAttr
	 * @Description: 获取指定属性, 如果属性不存在或没有值, 总是返回{@link StringUtil#EMPTY_STRING}
	 * @param attrName 属性名
	 * @return String 属性值
	 */
	public String getAttr(String attrName) {
		String attrVal = getAttrs().get(attrName);
		return StringUtil.isNotEmpty(attrVal, true) ? attrVal : StringUtil.EMPTY_STRING;
	}

	/**
	 * @Title: addAttr
	 * @Description: 添加标签属性
	 * @param attrName 属性名
	 * @param attrValue 属性值
	 */
	public void addAttr(String attrName, String attrValue) {
		getAttrs().put(attrName, attrValue);
	}

	/**
	 * @Title: hasAttr
	 * @Description: 是否包含指定属性名
	 * @param attrName 属性名
	 * @return boolean true-包含指定属性, false-不包含指定属性
	 */
	public boolean hasAttr(String attrName) {
		return (this.getAttrs().containsKey(attrName));
	}

	/**
	 * @Title: getChildTags
	 * @Description: 获取所有子标签
	 * @return Map&lt;String,List&lt;XMLObject&gt;&gt; 子标签集合, Key:标签名,
	 *         Value:当前标签下所有与标签名关联的一级子标签
	 */
	public Map<String, List<XMLObject>> getChildTags() {
		if (null == this.childTags) {
			this.childTags = Maps.newHashMap();
		}
		return this.childTags;
	}

	/**
	 * @Title: addChildTag
	 * @Description: 添加子标签
	 * @param xmlObject 子标签对象
	 */
	public void addChildTag(XMLObject xmlObject) {
		Map<String, List<XMLObject>> localSubTags = getChildTags();

		// 验证是否已存在当前标签
		String tagName = xmlObject.tagName;
		List<XMLObject> subTags = localSubTags.get(tagName);
		if (subTags == null) {
			subTags = new ArrayList<XMLObject>();
			localSubTags.put(tagName, subTags);
		}

		// 添加标签
		subTags.add(xmlObject);
	}

	/**
	 * @Title: getChildTags
	 * @Description: 获取子标签
	 * @param tagName 标签名
	 * @return List&lt;XMLObject&gt; 当前标签包含的所有子标签, 总是返回合法的列表对象
	 */
	public List<XMLObject> getChildTags(String tagName) {
		List<XMLObject> result = getChildTags().get(tagName);
		if (null == result) {
			result = Lists.newArrayList();
			getChildTags().put(tagName, result);
		}
		return result;
	}

	/**
	 * @Title: getChildTag
	 * @Description: 获取指定子标签
	 * @param tagName 子标签名
	 * @param index 第几个 <i>tagName</i> 指定的子标签
	 * @return XMLObject XML 节点对象
	 */
	public XMLObject getChildTag(String tagName, int index) {
		List<XMLObject> subTags = getChildTags(tagName);
		if (index >= subTags.size() || index < 0) {
			return null;
		}
		return subTags.get(index);
	}

	/**
	 * @Title: hasChildTag
	 * @Description: 验证是否包含目标子标签
	 * @param subTag 目标子标签
	 * @return boolean true-包含, false-不包含
	 */
	public boolean hasChildTag(XMLObject subTag) {
		if (null == childTags || subTag == null) {
			return false;
		}

		String subTagName = subTag.getTagName();
		List<XMLObject> list = getChildTags().get(subTagName);

		return ((null != list) && (-1 != list.indexOf(subTag)));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((attrs == null) ? 0 : attrs.hashCode());
		result = prime * result + ((tagName == null) ? 0 : tagName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		XMLObject other = (XMLObject) obj;
		if (attrs == null) {
			if (other.attrs != null)
				return false;
		} else if (!attrs.equals(other.attrs))
			return false;
		if (tagName == null) {
			if (other.tagName != null)
				return false;
		} else if (!tagName.equals(other.tagName))
			return false;
		return true;
	}

	/**
	 * @Title: getAllChildTags
	 * @Description: 获取当前节点下所有的子节点, 包括当前元素和后代元素
	 * @param childTagName 子节点标签名
	 * @return List&lt;XMLObject&gt; 指定标签列表
	 */
	public List<XMLObject> getAllChildTags(String childTagName) {
		List<XMLObject> result = Lists.newArrayList();
		getAllChild(this, childTagName, result);
		return result;
	}

	/**
	 * @Title: getAllChild
	 * @Description: 获取所有子节点
	 * @param xmlObject 节点对象
	 * @param childTagName 目标节点名称
	 * @param result 用于保存节点列表
	 */
	private void getAllChild(XMLObject xmlObject, String childTagName, List<XMLObject> result) {
		appendTag(xmlObject, childTagName, result);

		// 验证是否还有后代元素
		Map<String, List<XMLObject>> children = xmlObject.getChildTags();
		if (0 >= children.size()) {
			return;
		}

		// 遍历所有后代标签
		for (Entry<String, List<XMLObject>> child : children.entrySet()) {
			List<XMLObject> descendants = child.getValue();

			if (0 >= descendants.size()) {
				appendTag(xmlObject, childTagName, result);
				continue;
			}

			for (XMLObject descendant : descendants) {
				getAllChild(descendant, childTagName, result);
			}
		}
	}

	/**
	 * @Title: appendTag
	 * @Description: 追加到result中
	 * @param xmlObject xml节点元素
	 * @param childTagName 目标节点名称
	 * @param result 用于保存节点列表
	 */
	private void appendTag(XMLObject xmlObject, String childTagName, List<XMLObject> result) {
		if (StringUtil.equalsTwo(xmlObject.getTagName(), childTagName)) {
			result.add(xmlObject);
		}
	}

	@Override
	public String toString() {
		return "XMLObject [tagName=" + tagName + ", content=" + content + ", attrs=" + attrs + "]";
	}
}