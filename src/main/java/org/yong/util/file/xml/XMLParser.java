package org.yong.util.file.xml;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Attribute;
import org.dom4j.Comment;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yong.util.file.xml.fmt.XMLObjectFormatter;
import org.yong.util.file.xml.fmt.XMLObjectFormatterFactory;

/**
 * @author Huang.Yong
 * @Description XML解析器, 注意:一个解析器只能绑定一个XML文件.
 * @Usage <pre>
 * // 获取解析器
 * XMLParser xmlParser = new XMLParser(xmlPath);
 * 
 * // 解析XML文件
 * // 解析完成后将通过 {@link XMLObject} 节点对象获取属性和标签体
 * XMLObject root = xmlParser.parse();
 * </pre>
 * 
 * @see XMLObject
 */
public class XMLParser {

	private final Logger LOGGER = LoggerFactory.getLogger(getClass());

	/** XML 文件路径 */
	private String path;

	/** 是否包含同级重复标签: 标签名一致, 属性列表一致 */
	@SuppressWarnings("unused")
	@Deprecated
	private Boolean allowDuplicateSubTag = true;

	/** 是否已经包含重复标签 */
	private Boolean hasDuplicateSubTag = null;

	/**
	 * @Title: XMLPaser
	 * @Description: 构建XML解析器
	 * @param path 文件路径, 绝对路径
	 */
	public XMLParser(String path) {
		super();
		this.path = path;
	}

	/**
	 * @Title: hasDuplicateSubTag
	 * @Description: 获取当前XML文件是否已经包含重复标签
	 * @return Boolean
	 *         <ul>
	 *         <li>true-已经包含</li>
	 *         <li>false-未包含</li>
	 *         <li>null-未检测(当<i>allowDuplicateSubTag=true</i>时总是返回<i>null</i>)</li>
	 *         </ul>
	 */
	public Boolean hasDuplicateSubTag() {
		return hasDuplicateSubTag;
	}

	/**
	 * @Title: setAllowDuplicateSubTag
	 * @Description: 设置是否允许包含相同子标签(相同标签名, 完全相同的属性名)
	 * @param allowDuplicateSubTag <ul>
	 *            <li>true-可以包含相同标签</li>
	 *            <li>false-不允许包含相同子标签, 如果包含则抛出异常</li>
	 *            <li>null-仅保留最后一个相同子标签</li>
	 *            </ul>
	 */
	@Deprecated
	public void setAllowDuplicateSubTag(Boolean allowDuplicateSubTag) {
		this.allowDuplicateSubTag = allowDuplicateSubTag;
	}

	/**
	 * @Title: parse
	 * @Description: 解析XML文件
	 *               <p>
	 *               <ul>
	 *               <li>如果当前标签含有子标签时, 将不会获取当前标签的<i>存文本内容</i>, <span
	 *               style="color:#F00">红色内容将会丢失</span></li>
	 *               <li>当前解析器不解析注释内容</li>
	 *               </ul>
	 * 
	 *               <pre>
	 * &lt;root&gt;
	 *   tag content
	 *   &lt;tag&gt;
	 *     child tag content
	 *     &lt;sub-tag id="subTag"&gt;sub tag content&lt;/sub-tag&gt;
	 *   &lt;/tag&gt;
	 *   <span style="color:#F00">&lt;!-- &lt;desc&gt; 这个内容将不会解析 &lt;/desc&gt; --&gt;</span>
	 *   <span style="color:#F00">&lt;!-- 这个内容将也不会解析 --&gt;</span>
	 * &lt;/root&gt;
	 * </pre>
	 * 
	 *               </P>
	 * @return XMLObject XML对象
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
	public XMLObject parse() throws MalformedURLException, DocumentException {
		Document document = getDocument();

		// 获取根节点名称
		Element rootElement = document.getRootElement();
		String tagName = rootElement.getName();

		// 构建 XMLObject 对象
		XMLObject xmlObject = new XMLObject(tagName);
		xmlObject.setParent(null);
		xmlObject.setRoot(Boolean.TRUE);

		// 解析XML
		parseNode(xmlObject, rootElement);
		return xmlObject;
	}

	/**
	 * @Title: parseNode
	 * @Description: 解析XML
	 * @param xmlObject 上个节点对象
	 * @param node 节点元素
	 */
	private void parseNode(XMLObject xmlObject, Element node) {
		for (int i = 0, size = node.nodeCount(); i < size; i++) {
			Node subNode = node.node(i);
			XMLObject subXmlObject = null;
			if (subNode instanceof Element) {
				subXmlObject = appendSubTag(xmlObject, subNode);
				parseNode(subXmlObject, (Element) subNode);
				subXmlObject.setParent(xmlObject);
			}

			if (subNode instanceof Comment) {
				continue;
			}
		}

		setAttrsAndContent(xmlObject, node);

		// 根据策略去除重复标签
		// if (null == allowDuplicateSubTag || !allowDuplicateSubTag) {
		// filterDuplicateSubTags(xmlObject);
		// }
	}

	/**
	 * @Title: setAttrsAndContent
	 * @Description: 设置属性和内容
	 * @param xmlObject XML节点映射对象
	 * @param node XML节点
	 */
	private void setAttrsAndContent(XMLObject xmlObject, Element node) {
		if (null == xmlObject || null == node) {
			return;
		}

		if (xmlObject.getAttrs().isEmpty()) {
			setAttributes(xmlObject, node);
			setContent(xmlObject, node);
		}
	}

	/**
	 * @Title: appendSubTag
	 * @Description: 追加子标签
	 * @param xmlObject 当前节点对象
	 * @param subNode 子节点
	 * @return XMLObject 子节点映射对象
	 */
	private XMLObject appendSubTag(XMLObject xmlObject, Node subNode) {
		// 获取子标签
		String subTagName = subNode.getName();
		XMLObject subXmlObject = new XMLObject(subTagName);

		// 添加子标签
		xmlObject.addChildTag(subXmlObject);
		return subXmlObject;
	}

	/**
	 * @Title: setContent
	 * @Description: 设置标签值
	 * @param xmlObject XMLObject对象
	 * @param node 与XMLObject对象关联的节点
	 */
	private void setContent(XMLObject xmlObject, Element node) {
		String content = node.getTextTrim();
		xmlObject.setContent(content);
	}

	/**
	 * @Title: setAttributes
	 * @Description: 设置属性
	 * @param xmlObject XMLObject对象
	 * @param node 与XMLObject对象关联的节点
	 */
	private void setAttributes(XMLObject xmlObject, Element node) {
		for (int i = 0, size = node.attributeCount(); i < size; i++) {
			// 获取属性
			Attribute attr = node.attribute(i);
			// 获取属性名
			String attrName = attr.getName();

			// // 验证是否重名属性
			// if (xmlObject.hasAttr(attrName) && isAttributeStrictest) {
			// throw new RuntimeException("Duplicate attribute[" + attrName +
			// "]");
			// }

			// 保存属性
			xmlObject.addAttr(attrName, attr.getValue());
		}
	}

	/**
	 * @Title: getDocument
	 * @Description: 获取XML文件根节点
	 * @return Document 根节点
	 * @throws DocumentException
	 * @throws MalformedURLException
	 */
	private Document getDocument() throws MalformedURLException, DocumentException {
		SAXReader saxReader = new SAXReader();
		File file = getXMLFile();
		Document document = saxReader.read(file);
		return document;
	}

	/**
	 * @Title: getXMLFile
	 * @Description: 获取XML文件对象
	 * @return File XML文件对象
	 */
	private File getXMLFile() {
		if (path == null || 0 >= path.length()) {
			throw new RuntimeException("Path invalid[path=" + path + "]");
		}
		return new File(path);
	}

	/**
	 * @Title: transferRoot
	 * @Description: 转换为文件中, 必须从根节点开始
	 * @param root 根元素
	 * @param outputFile 输出文件
	 * @param compact true-紧凑排版, false-缩进排版
	 * @return boolean true-转换成功, false-转换失败
	 * @throws IOException
	 */
	public boolean transferRoot(XMLObject root, File outputFile, boolean compact) throws IOException {
		// root校验
		if (null == root || !root.isRoot()) {
			LOGGER.debug("The target node is invald root element");
			return false;
		}

		// 输出文件校验
		if (null == outputFile) {
			LOGGER.debug("The target output file is invalid");
			return false;
		} else if (!outputFile.exists()) {
			LOGGER.debug("The target output file is not exist, will create it");
			File parent = outputFile.getParentFile();
			if (!parent.exists()) {
				parent.mkdirs();
			}
			// outputFile.createNewFile();
		} else {
			LOGGER.warn("The target output file is not empty, will clean it");
		}

		// 格式化输出
		// 创建格式化输出工具
		XMLObjectFormatter formatter = XMLObjectFormatterFactory.createFormatter(compact);

		// 执行格式化
		StringBuilder content = formatter.format(root);

		// 将格式化内容写入文件
		FileUtils.write(outputFile, content, false);

		return true;
	}

	/**
	 * @Title: createNode
	 * @Description: 创建新标签
	 * @param tagName 标签名
	 * @param content 标签体
	 * @param attrs 属性列表
	 * @return XMLObject 新节点对象
	 */
	public static XMLObject createNode(String tagName, String content, Map<String, String> attrs) {
		XMLObject newNode = new XMLObject(tagName, content, attrs);
		newNode.setParent(null);
		return newNode;
	}

}
