package org.yong.util.file.xml.parser;

import com.alibaba.fastjson.JSON;
import org.junit.Before;
import org.junit.Test;
import org.yong.util.entity.TourStation;
import org.yong.util.file.xml.XMLObject;
import org.yong.util.file.xml.XMLParser;
import sun.nio.cs.ISO_8859_2;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.Assert.*;

public class XMLParserTest3 {

    private XMLParser xmlParser;

    @Before
    public void before() {
        String xmlPath = XMLParserTest3.class.getResource("/test1_20200902_145714.xml").getFile();
        assertNotNull(xmlPath);

        File file = new File(xmlPath);
        assertTrue(file.exists());
        //xmlParser = new XMLParser(xmlPath, "GB2312");
        xmlParser = new XMLParser(xmlPath);
        //xmlParser = new XMLParser(xmlPath, StandardCharsets.ISO_8859_1);
    }

    @Test
    public void testParse() throws Exception {
        XMLObject root = xmlParser.parse();
        assertNotNull(root);

        //// PASS
        //// 多属性
        //XMLObject tourTaskXml = root.getChildTag("TourTask", 0);
        //TourTask tourTask = tourTaskXml.toBean(TourTask.class);
        //assertNotNull(tourTask);
        //System.out.println(tourTask);

        //// PASS
        //// 单属性
        //XMLObject tourLineXml = root.getChildTag("TourLine", 0);
        //TourLine tourLine = tourLineXml.toBean(TourLine.class);
        //assertNotNull(tourLine);
        //System.out.println(tourLine);

        // 子控件
        XMLObject stationListXml = root.getChildTag("TourStationList", 0);
        List<TourStation> stationList = stationListXml.toBeans("TourStation", TourStation.class);
        assertEquals(2, stationList.size());
        System.out.println(stationList.get(0));
        System.out.println(JSON.toJSONString(stationList.get(0)));
        //System.out.println(stationList.get(1));

        //String s = JSON.parseObject("test1", String.class);
        //System.out.println(s);
    }

    @Test
    public void testOther() {
        //System.out.println(ArrayList.class.isAssignableFrom(List.class));

        // true
        //System.out.println(Reflects.isCollection(new ArrayList<>().getClass()));

        //System.out.println(Collection.class.isInstance(List.class));
        //System.out.println(List.class.isInstance(Collection.class));
        //System.out.println(Collection.class.isInstance(new ArrayList<>()));
        //System.out.println(Collection.class.isInstance(new ArrayList<>().getClass()));
        //System.out.println(new ArrayList<>().getClass().isAssignableFrom(Collection.class));
        //System.out.println(Collection.class.isAssignableFrom(new ArrayList<>().getClass()));

        //System.out.println(JSON.toJSONString(new Date()));
        //System.out.println(int.class.isPrimitive());
        //System.out.println(Integer.class.isPrimitive());

        // 1599058642
        // 1599636963572
    }

}
