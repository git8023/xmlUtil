package org.yong.util.file.xml;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.yong.util.file.FileUtil;
import org.yong.util.file.xml.fmt.XMLObjectFormatter;
import org.yong.util.file.xml.fmt.XMLObjectFormatterFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * XML解析器, 注意:一个解析器只能绑定一个XML文件.
 *
 * <pre>
 * // 获取解析器
 * XMLParser xmlParser = new XMLParser(xmlPath);
 *
 * // 解析XML文件
 * // 解析完成后将通过 {@link XMLObject} 节点对象获取属性和标签体
 * XMLObject root = xmlParser.parse();
 * </pre>
 *
 * @author Huang.Yong
 * @see XMLObject
 */
@Slf4j
public class XMLParser {

    /**
     * XML 文件路径
     */
    private final String path;

    /**
     * 文件编码, 默认使用 UTF-8
     */
    private final String fileEncoding;

    /**
     * 构建XML解析器
     *
     * @param path 文件路径
     */
    public XMLParser(String path) {
        this(path, StandardCharsets.UTF_8);
    }

    /**
     * 构建XML解析器
     *
     * @param path         文件路径
     * @param fileEncoding 文件编码
     */
    public XMLParser(String path, String fileEncoding) {
        this.path = path;
        this.fileEncoding = fileEncoding;
    }

    /**
     * 构建XML解析器
     *
     * @param path         文件路径
     * @param fileEncoding 文件编码
     */
    public XMLParser(String path, Charset fileEncoding) {
        this(path, fileEncoding.name());
    }

    /**
     * 解析XML文件
     * <p>
     *               <ul>
     *               <li>如果当前标签含有子标签时, 将不会获取当前标签的<i>存文本内容</i>,
     *               <span style="color:#F00">红色内容将会丢失</span></li>
     *               <li>当前解析器不解析注释内容</li>
     *               </ul>
     *
     * <pre>
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
     *
     * @return XMLObject XML对象
     */
    public XMLObject parse() throws Exception {
        Document document = getDocument();

        // 获取根节点名称
        Element rootElement = document.getRootElement();
        String tagName = rootElement.getName();

        // 构建 XMLObject 对象
        XMLObject xmlObject = new XMLObject(tagName);
        xmlObject.setParent(null);
        xmlObject.setRootElement(Boolean.TRUE);

        // 解析XML
        parseNode(xmlObject, rootElement);
        return xmlObject;
    }

    /**
     * 解析XML
     *
     * @param xmlObject 上个节点对象
     * @param node      节点元素
     */
    private void parseNode(XMLObject xmlObject, Element node) {
        for (int i = 0, size = node.nodeCount(); i < size; i++) {
            Node subNode = node.node(i);
            if (subNode instanceof Comment) {
                continue;
            }

            XMLObject subXmlObject;
            if (subNode instanceof Element) {
                subXmlObject = appendSubTag(xmlObject, subNode);
                parseNode(subXmlObject, (Element) subNode);
                subXmlObject.setParent(xmlObject);
            }
        }

        setAttrsAndContent(xmlObject, node);
    }

    /**
     * 设置属性和内容
     *
     * @param xmlObject XML节点映射对象
     * @param node      XML节点
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
     * 追加子标签
     *
     * @param xmlObject 当前节点对象
     * @param subNode   子节点
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
     * 设置标签值
     *
     * @param xmlObject XMLObject对象
     * @param node      与XMLObject对象关联的节点
     */
    private void setContent(XMLObject xmlObject, Element node) {
        String content = node.getTextTrim();
        xmlObject.setContent(content);
    }

    /**
     * 设置属性
     *
     * @param xmlObject XMLObject对象
     * @param node      与XMLObject对象关联的节点
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
     * 获取XML文件根节点
     *
     * @return Document 根节点
     */
    private Document getDocument() throws Exception {
        SAXReader saxReader = new SAXReader();
        saxReader.setEncoding(fileEncoding);
        File file = getXMLFile();
        return saxReader.read(file);
    }

    /**
     * 获取XML文件对象
     *
     * @return File XML文件对象
     */
    private File getXMLFile() {
        if (path == null || 0 >= path.length()) {
            throw new RuntimeException("Path invalid[path=" + path + "]");
        }
        return new File(path);
    }

    /**
     * 转换为文件
     *
     * @param root       根元素
     * @param outputFile 输出文件
     * @param compact    true-紧凑排版, false-缩进排版
     * @return boolean true-转换成功, false-转换失败
     */
    public static boolean transfer(XMLObject root, File outputFile, boolean compact) throws IOException {
        // root校验
        if (null == root || !root.isRootElement()) {
            log.debug("指定节点不是有效根节点");
            return false;
        }

        // 后缀检测
        if (!outputFile.getName().endsWith(".xml")) {
            log.debug("输出文件不是.xml文件");
            return false;
        }
        FileUtil.createFile(outputFile);

        // 类型检测
        if (!outputFile.isFile()) {
            log.debug("outputFile 不是文件类型");
            return false;
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
     * 创建新标签
     *
     * @param tagName 标签名
     * @param content 标签体
     * @param attrs   属性列表
     * @return XMLObject 新节点对象
     */
    public static XMLObject createNode(String tagName, String content, Map<String, String> attrs) {
        XMLObject newNode = new XMLObject(tagName, content, attrs);
        newNode.setParent(null);
        return newNode;
    }

}
