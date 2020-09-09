package org.yong.util.file.xml.parser;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.XMLParser;

public class XMLParserTest2 {

    private XMLParser xmlParser;

    private XMLObject root;

    @Before
    public void before() throws Exception {
        String path = XMLParserTest2.class.getResource("/xml-test-transfer.xml").getFile();
        this.xmlParser = new XMLParser(path);
        this.root = xmlParser.parse();
    }

    @Test
    public void testTransferRoot() throws IOException {
        Map<String, List<XMLObject>> childTags = root.getChildTags();
        assertEquals(1, childTags.size());

        String path = XMLParserTest2.class.getResource("/").getPath() + "xml-test-transfer2-compact.xml";
        xmlParser.transferRoot(root, new File(path), true);

        path = XMLParserTest2.class.getResource("/").getPath() + "xml-test-transfer2-retract.xml";
        xmlParser.transferRoot(root, new File(path), false);
    }
}
