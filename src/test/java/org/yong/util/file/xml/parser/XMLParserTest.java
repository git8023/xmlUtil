package org.yong.util.file.xml.parser;

import static org.junit.Assert.*;

import java.io.File;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.XMLParser;

/**
 * XML解析器测试
 *
 * @author Huang.Yong
 * @version 0.1
 */
public class XMLParserTest {

    private XMLParser xmlParser;

    private XMLObject root;

    @Before
    public void before() throws Exception {
        String xmlPath = XMLParserTest.class.getResource("/xml-test.xml").getFile();
        assertNotNull(xmlPath);

        File file = new File(xmlPath);
        assertTrue(file.exists());

        xmlParser = new XMLParser(xmlPath);

        // 首次解析
        testParse();
    }

    @Test
    public void testParse() throws Exception {
        root = xmlParser.parse();
        assertNotNull(root);
    }

    @Test
    public void testJsonParse() {
        Integer v = JSON.parseObject("1", Integer.class);
        System.out.println(v);

        String s = JSON.parseObject("1", String.class);
        System.out.println(s);
    }
}
