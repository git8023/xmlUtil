package org.yong.util.file.xml.object;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.XMLParser;
import org.yong.util.file.xml.parser.XMLParserTest;

public class XMLObjectWriteTest {

    private XMLParser xmlParser;

    private XMLObject root;

    @Before
    public void before() throws Exception {
        String xmlPath = XMLParserTest.class.getResource("/xml-test-write.xml").getFile();
        assertNotNull(xmlPath);

        File file = new File(xmlPath);
        assertTrue(file.exists());

        xmlParser = new XMLParser(xmlPath);
        root = xmlParser.parse();
    }

    // 将当前节点插入到指定节点开始位置(内部)
    @Test
    public void testInsertBefore1() throws Exception {
        // 创建节点
        String tagName = "insertBeforeTag";
        String content = null;
        XMLObject newNode = XMLParser.createNode(tagName, content, null);

        XMLObject typeTag = root.getChildTag("type", 0);
        boolean result = newNode.insertBefore(typeTag);
        assertTrue(result);
    }

    @Test
    public void testInsertBefore2() throws Exception {
        List<XMLObject> list = root.getAllChildTags("child2");
        XMLObject child2Tag = list.get(0);
        boolean result = child2Tag.insertBefore(root);
        assertTrue(result);

        XMLObject typeTag1 = root.getChildTag("type1", 0);
        result = child2Tag.insertBefore(typeTag1);
        assertTrue(result);

        XMLObject typeTag = root.getChildTag("type", 0);
        Map<String, List<XMLObject>> childTags = typeTag.getChildTags();
        assertTrue(MapUtils.isEmpty(childTags));
    }

    // 将当前节点插入到指定节点结束位置(内部)
    @Test
    public void testInsertAfter() throws Exception {
        // 创建节点
        String tagName = "insertBeforeTag";
        String content = null;
        XMLObject newNode = XMLParser.createNode(tagName, content, null);

        XMLObject typeTag = root.getChildTag("type", 0);
        boolean result = newNode.insertAfter(typeTag);
        assertTrue(result);
    }

    // 将当前节点添加到指定节点之前(外部)
    @Test
    public void testAppendBefore() throws Exception {
        // 创建节点
        String tagName = "appendBeforeTag";
        String content = null;
        XMLObject newNode = XMLParser.createNode(tagName, content, null);

        boolean result = newNode.appendBefore(root, true);
        assertFalse(result);

        XMLObject typeTag = root.getChildTag("type", 0);
        result = newNode.appendBefore(typeTag, true);
        assertTrue(result);
    }

    // TODO 将当前节点添加到指定节点之后(外部)
    @Test
    public void testAppendAfter() throws Exception {
        // 创建节点
        String tagName = "type";
        String content = "新增的节点";
        XMLObject newNode = XMLParser.createNode(tagName, content, null);

        boolean result = newNode.appendAfter(root, true);
        assertFalse(result);

        XMLObject typeTag = root.getChildTag("type", 0);
        result = newNode.appendAfter(typeTag, true);
        assertTrue(result);
    }

}
