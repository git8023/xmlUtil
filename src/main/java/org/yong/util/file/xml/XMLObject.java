package org.yong.util.file.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	/** 属性列表 */
	private Map<String, String> attrs = new HashMap<String, String>();

	/** 子标签集合, Key:tagName, Value:List&lt;XMLObject&gt; */
	private Map<String, List<XMLObject>> childTags = new LinkedHashMap<String, List<XMLObject>>();

	/** 标签文本内容 */
	private String content;

	/** 是否根节点 */
	private boolean isRoot;

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	/** 父级节点 */
	private XMLObject parent;

	/** 标签名 */
	private String tagName;

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
	 * @Title: XMLObject
	 * @Description: 构建XML对象
	 * @param tagName 标签名
	 * @param content 标签体
	 * @param attrs 标签属性表
	 */
	XMLObject(String tagName, String content, Map<String, String> attrs) {
		super();
		this.tagName = tagName;
		this.content = content;
		this.attrs = attrs;
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
	 * @Title: appendAfter
	 * @Description: 追加到指定标签节点之后(外部)
	 * @param markerNode 标记性节点
	 * @param sameLevel 当前节点必须和标记节点处于相同层次(<i>将相同节点几种到一起更便于维护和管理</i>)
	 * @return boolean true-成功. false-失败,当前为根节点或<i>node</i>为根节点/漂浮状态时,
	 *         或当前节点和标记节点不是同名节点(当<i>mustBeSameTagName = true</i>时)
	 */
	public boolean appendAfter(XMLObject markerNode, boolean sameLevel) {
		// 追加前验证
		boolean valid = validationOuterEdit(markerNode);
		if (!valid) {
			return false;
		}

		// 同级元素验证
		if (sameLevel && !sameTagName(markerNode)) {
			LOGGER.debug("The marker node name must be the same as current node name");
			return false;
		}

		// 获取marker父节点
		XMLObject markerParent = markerNode.getParent();

		// 获取marker的同级节点列表, 找到marker在列表中的位置
		List<XMLObject> markerLevelChildren = markerParent.getChildTags(markerNode.getTagName());
		LinkedList<XMLObject> linkedList = Lists.newLinkedList(markerLevelChildren);
		int markerIdx = linkedList.indexOf(markerNode);

		// 漂浮当前节点
		this.setFloating();

		// 把当前节点追加到marker同级节点中, 位置在marker之前
		linkedList.add(markerIdx, this);
		markerLevelChildren.clear();
		markerLevelChildren.addAll(linkedList);

		// 当前节点父节点设置为marker父节点
		this.setParent(markerParent);

		return true;
	}

	/**
	 * @Title: appendBefore
	 * @Description: 追加到指定标签节点之前(外部)
	 * @param markerNode 标记性节点
	 * @param sameLevel 当前节点必须和标记节点处于相同层次(<i>将相同节点几种到一起更便于维护和管理</i>)
	 * @return boolean true-成功. false-失败,当前为根节点或<i>node</i>为根节点/漂浮状态时,
	 *         或当前节点和标记节点不是同名节点(当<i>mustBeSameTagName = true</i>时)
	 */
	public boolean appendBefore(XMLObject markerNode, boolean sameLevel) {
		boolean valid = validationOuterEdit(markerNode);
		if (!valid) {
			return false;
		}

		// 当前节点和标记节点必须是同名节点
		if (sameLevel && !sameTagName(markerNode)) {
			LOGGER.debug("The marker node name must be the same as current node name");
			return false;
		}

		// 获取标记节点父节点
		XMLObject markerNodeParent = markerNode.getParent();

		// 获取父节点中标记节点子节点集合
		List<XMLObject> markerLevelChildren = markerNodeParent.getChildTags(markerNode.getTagName());
		LinkedList<XMLObject> linkedList = Lists.newLinkedList(markerLevelChildren);

		// 找到标记节点在集合中的位置
		int markerIdx = linkedList.indexOf(markerNode);

		// 设置当前节点为漂浮状态
		this.setFloating();

		// 将当前节点移动到标记节点之前
		linkedList.add(markerIdx, this);
		markerLevelChildren.clear();
		markerLevelChildren.addAll(linkedList);

		return true;
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
	 * @Title: getContent
	 * @Description: 获取当前标签文本内容
	 * @return String 存文本内容
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @Title: getParent
	 * @Description: 获取当前节点父节点
	 * @return XMLObject 父节点对象
	 */
	public XMLObject getParent() {
		return parent;
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
	 * @Title: hasAttr
	 * @Description: 是否包含指定属性名
	 * @param attrName 属性名
	 * @return boolean true-包含指定属性, false-不包含指定属性
	 */
	public boolean hasAttr(String attrName) {
		return (this.getAttrs().containsKey(attrName));
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

	/**
	 * @Title: insertBefore
	 * @Description: 插入到指定节点之后(内部)
	 * @param parentNode 父节点
	 * @return boolean true-成功, false-失败,当前为根节点或当前节点为漂浮状态时
	 */
	public boolean insertAfter(XMLObject parentNode) {
		boolean valid = validationInnerEdit(parentNode);
		if (!valid) {
			LOGGER.warn("validation failed at before append");
			return false;
		}

		// 从原有的父节点中移出当前节点
		this.setFloating();

		// 获取当前节点在目标父节点的同级元素
		List<XMLObject> currLevelChildren = parentNode.getChildTags(this.getTagName());

		// 将当前节点添加到目标父节点内容的最末尾
		LinkedList<XMLObject> linkedList = Lists.newLinkedList(currLevelChildren);
		linkedList.addLast(this);
		currLevelChildren.clear();
		currLevelChildren.addAll(linkedList);

		return true;
	}

	/**
	 * @Title: insertBefore
	 * @Description: 插入到指定节点之前(内部)
	 * @param parentNode 父节点, 父节点不能是漂浮状态
	 * @return boolean true-成功, false-失败,当前为根节点
	 */
	public boolean insertBefore(XMLObject parentNode) {
		boolean valid = validationInnerEdit(parentNode);
		if (!valid) {
			return false;
		}

		// 获取目标父节点的子节点集合,
		// 子节点集合与当前节点名相关
		List<XMLObject> currLevelchildren = parentNode.getChildTags(this.getTagName());

		// 从原来所属父节点中移出当前节点
		setFloating();

		// 将当前节点插入到子节点第一个
		LinkedList<XMLObject> linkedList = Lists.newLinkedList(currLevelchildren);
		currLevelchildren.clear();
		linkedList.addFirst(this);
		currLevelchildren.addAll(linkedList);

		// 设置父节点
		this.setParent(parentNode);

		return true;
	}

	/**
	 * @Title: isFloating
	 * @Description: 校验当前节点是否漂浮状态(只能代表当前节点, 而不能代表其上级节点是否也是漂浮状态)
	 * @return boolean true-漂浮状态, false-不是漂浮状态
	 */
	private boolean isFloating() {
		// 不是根节点, 并且没有相应的父节点时
		return !this.isRoot() && null == this.getParent();
	}

	/**
	 * @Title: isRoot
	 * @Description: 校验当前节点是否根节点
	 * @return boolean true-根节点, false-不是根节点
	 */
	public boolean isRoot() {
		return isRoot;
	}

	/**
	 * @Title: sameTagName
	 * @Description: 校验当前节点与另一个节点属于同名节点
	 * @param other 另一个节点
	 * @return boolean true-同名节点,false-不是同名节点
	 */
	private boolean sameTagName(XMLObject other) {
		return this.getTagName().equals(other.getTagName());
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
	 * @Title: remove
	 * @Description: 从父节点中将当前节点移出
	 */
	private void setFloating() {
		if (this.isRoot()) {
			throw new RuntimeException("Cann't delete root element");
		}

		XMLObject currParent = this.getParent();
		if (null != currParent) {
			Map<String, List<XMLObject>> currParentChildren = currParent.getChildTags();

			// 从父节点的子节点集合中删除当前节点
			String currTagName = this.getTagName();
			List<XMLObject> currLevelChildren = currParentChildren.get(currTagName);
			boolean removed = currLevelChildren.remove(this);
			if (!removed) {
				LOGGER.debug("Delete the current node failure in the current layer[" + currTagName + "]");
			}

			// 删除无效的子节点记录
			if (CollectionUtils.isEmpty(currLevelChildren)) {
				LOGGER.debug("Clear invalid children set[" + currTagName + "]");
				currParentChildren.remove(currTagName);
			}
			this.parent = null;
		}
	}

	/**
	 * @Title: setParent
	 * @Description: 设置父级节点
	 * @param parent 父节点对象
	 */
	void setParent(XMLObject parent) {
		this.parent = parent;
	}

	/**
	 * @Title: setRoot
	 * @Description: 设置是否根节点
	 * @param isRoot true-根节点, false-叶子节点
	 */
	void setRoot(boolean isRoot) {
		this.isRoot = isRoot;
	}

	/**
	 * @Title: setTagName
	 * @Description: 设置当前标签名
	 * @param tagName 当前标签名
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	@Override
	public String toString() {
		return "XMLObject [tagName=" + tagName + ", content=" + content + ", attrs=" + attrs + "]";
	}

	/**
	 * @Title: valid
	 * @Description: 验证当前节点是否有效
	 * @return boolean true-有效节点, false-无效节点
	 */
	public boolean valid() {
		return StringUtil.isNotEmpty(this.tagName, true);
	}

	/**
	 * @Title: validationInnerEdit
	 * @Description: 编辑节点前结构验证, 当前节点不是root节点且父节点不是漂浮状态
	 * @param targetParent 目标父节点
	 * @return true-验证通过, false-验证失败
	 */
	private boolean validationInnerEdit(XMLObject targetParent) {
		if (!valid()) {
			LOGGER.debug("The current node is invalid");
			return false;
		} else if (this.isRoot()) {
			LOGGER.debug("The current node cann't is root element");
			return false;
		}

		if (!targetParent.valid()) {
			LOGGER.debug("The target parent node is invalid");
			return false;
		} else if (targetParent.isFloating()) {
			LOGGER.debug("Taget the parent node is unstable");
			return false;
		}

		return true;
	}

	/**
	 * @Title: validationOuterEdit
	 * @Description: 验证是否能执行编辑, 相对于标记节点
	 * @param markerNode 标记节点
	 * @return boolean true-验证通过, false-验证失败
	 */
	private boolean validationOuterEdit(XMLObject markerNode) {
		// 当前节点和标记节点不能是同一个节点
		if (this.equals(markerNode)) {
			LOGGER.debug("The marker node cann't equal to current node");
			return false;
		}

		// 当前节点不能是根节点
		if (this.isRoot()) {
			LOGGER.debug("The current node cann't is root node");
			return false;
		}

		// 标记节点不能是根节点, 且不能为漂浮状态
		if (markerNode.isRoot()) {
			LOGGER.debug("The marker node cann't is root node");
			return false;
		} else if (markerNode.isFloating()) {
			LOGGER.debug("The marker node is not stable");
			return false;
		}

		return true;
	}
}