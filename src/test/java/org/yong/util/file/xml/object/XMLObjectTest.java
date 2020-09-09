package org.yong.util.file.xml.object;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.XMLParser;
import org.yong.util.file.xml.parser.XMLParserTest;

public class XMLObjectTest {

    private XMLParser xmlParser;

    private XMLObject root;

    @Before
    public void before() throws Exception {
        String xmlPath = XMLParserTest.class.getResource("/xml-test-2.xml").getFile();
        assertNotNull(xmlPath);

        File file = new File(xmlPath);
        assertTrue(file.exists());

        xmlParser = new XMLParser(xmlPath);
        root = xmlParser.parse();
    }

    @Test
    public void testGetTagName() {
        String tagName = root.getTagName();
        System.out.println(root.getAttrs().hashCode());
        System.out.println(root.getChildTags().get("div").get(0).getAttrs().hashCode());
        assertEquals("root", tagName);
    }

    @Test
    public void testGetContent() {
        // 如果节点中包含子节点, 当前节点标签体将被忽略
        String content = root.getContent();
        assertEquals("", content);

        // 空标签体测试
        XMLObject firstChild = root.getChildTag("first-child", 0);
        assertNotNull(firstChild);
        assertEquals("", firstChild.getContent());

        // 有内容的标签体测试
        XMLObject contentTag = root.getChildTag("only-tag-content", 0);
        assertNotNull(contentTag);
        assertTrue(0 < contentTag.getContent().length());
    }

    @Test
    public void testGetAttrs() {
        Map<String, String> attrs = root.getAttrs();
        assertNotNull(attrs);
        assertEquals(1, attrs.size());
    }

    @Test
    public void testGetAttr() {
        XMLObject tag = root.getChildTag("only-tag-attrs", 0);
        assertNotNull(tag);
        assertEquals("TagName", tag.getAttr("name"));
    }

    @Test
    public void testHasAttr() {
        XMLObject tag = root.getChildTag("only-tag-attrs", 0);
        assertNotNull(tag);
        assertTrue(tag.hasAttr("name"));
        assertFalse(tag.hasAttr("TagName"));
    }

    @Test
    public void testGetChildTags() {
        List<XMLObject> childTags = root.getChildTags("child-eq");
        assertNotNull(childTags);
        assertEquals(2, childTags.size());
        assertEquals("child-eq", childTags.get(0).getTagName());
        assertEquals("child-eq", childTags.get(1).getTagName());
    }

    @Test
    public void testGetChildTag() {
        XMLObject tag = root.getChildTag("only-tag-attrs", 0);
        assertNotNull(tag);
        assertEquals("only-tag-attrs", tag.getTagName());
    }

    @Test
    public void testHasChildTag() {
        XMLObject subTag = XMLParser.createNode("other", null, null);
        boolean hasChildTag = root.hasChildTag(subTag);
        assertFalse(hasChildTag);

        // XMLObject.equals
        XMLObject childEq = XMLParser.createNode("child-eq", null, null);
        hasChildTag = root.hasChildTag(childEq);
        assertFalse(hasChildTag);

        XMLObject childTag = root.getChildTag("child-eq", 1);
        hasChildTag = root.hasChildTag(childTag);
        assertTrue(hasChildTag);
    }

    @Test
    public void testGetAllChildTags() throws Exception {
        // 接口 XMLObject.getAllChildTags(String) 将获取以当前元素为出发点查找后代元素(包括当前元素)
        // 其中也包括根元素
        List<XMLObject> roots = root.getAllChildTags("root");
        assertEquals(1, roots.size());
    }
}
