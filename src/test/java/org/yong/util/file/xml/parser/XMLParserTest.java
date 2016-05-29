package org.yong.util.file.xml.parser;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.XMLParser;

/**
 * @Author Huang.Yong
 * @Description: XML解析器测试
 * @Date 2016年5月26日 下午10:10:08
 * @Version 0.1
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
}
