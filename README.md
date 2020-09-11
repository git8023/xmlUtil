# XML - Util
简单封装 [`Apache dom4j(1.6.1)`](http://www.dom4j.org/dom4j-1.6.1/ "官方网站"), 封装后将使用面向对象的思维读写 XML 文件</br>.

# 版本

* Version 1.3
    1. 新增字段解析器接口 `SimpleValueParser`
    2. 新增`实体类`转化为`XMLObject`接口`XMLObject.of(T)`
    3. 实体类导出XML文件示例:  
    ```java
      // 数据对象必须使用 @XmlTag 注解,
      // 并且只导出 @XmlField 注解的字段.
      // 注意: 自定义复杂属性也要遵循此规则
      Object data = ...;
      XMLObject root = XMLObject.of(data);
  
      // 目标节点必须是根节点
      root.setRootElement(true);
  
      // true-紧凑排版, false-缩进排版
      boolean compact = true;
      XMLParser.transfer(root, new File("outFilePath.xml"), compact);
    ```

* Version 1.2
    1. 新增实体类注解`@XmlTag`  
       `@XmlTag`注解的实体类可以通过`XMLObject.toBean()`映射到实体类
    2. 新增实体类字段注解`@XmlField`  
       `@XmlField`注解的字段可以通过`XMLObject.toBean()`映射, 同时该注解具备子孙标签查询功能.
    3. 新增转换接口`XMLObject.toBean(java.lang.Class)`
       把当前`XML`节点映射到参数指定的实体类中, 实体类必须使用`@XmlTag`注解, 目标字段必须使用`@XmlField`注解.
       组合自定义属性类也需要满足相同规则.
    4. 新增转换接口`XMLObject.toBeans(java.lang.String, java.lang.Class)`
       在当前节点中查找`子节点`并映射到`实体类`, 实体类规则同 `#toBean(java.lang.Class)`
    5. 构造解析器`XMLParser`支持指定文件编码
    6. 注解示例
        ```java
        // @XmlTag 建立TourAction和XMLObject映射关系 
        @XmlTag
        public class TourAction {
        
            @XmlField
            // @XmlField(name = "mountDvsType", type = FieldType.ATTRIBUTE)
            // 以上两种注解方式完全一致
            private int mountDvsType;
        
            @XmlField
            private String actionName;
        
            @XmlField
            private int detectedDvsType;
        
            /*
             * <TourAction mountDvsType="1" actionName="默认动作名称" detectedDvsType="0">
             *     <SensorInfo time="1599058642"/>
             * </TourAction>
             * 通过 path&name 读取子孙节点属性(或标签体)的值
             * path + name => TourAction.SensorInfo[time]
             */
            @XmlField(name = "time", type = FieldType.ATTRIBUTE, path = "SensorInfo")
            private long sensorInfoTime;
        
            /*
             * <SnapshotPosition arm1="0" arm2="90" arm3="0" arm4="0" arm5="0" arm6="0" zoom="1" thermal="1">
             *     <SnapshotSample image="仪表_20200902_145725.jpg" analysis="" time="1599058645"/>
             * </SnapshotPosition>
             * 指明 snapshotPosition 数据来源是直接子标签, 具体映射方案由 SnapshotPosition 类的 @XmlTag & @XmlField 决定
             */
            @XmlField(type = FieldType.TAG)
            private SnapshotPosition snapshotPosition;
        }
        ```

    版本测试详情查看`XMLParserTest3`.   
    下期更新计划:
    1. 调用者支持自定义字符串转`Class<T>`任意类型接口.   
       当前仅支持: JSONString->String, JSONString->JSONObject->Any  


* Version 1.1  
  * 使用`XMLParser`创建新节点<br/>
  ```Java
  // XMLObject XMLParser.createNode(String tagName, String content, Map<String, String> attrs)
  XMLObject newNode = XMLParser.createNode(tagName, content, attrs);
  ```
  * 将新创建的节点插入到现有结构中
    * 添加到指定节点`内部`
      * 插入到指定节点内部第一个子节点的位置, 已存在的节点将依次向后移动<br/>
      ```Java
      // boolean XMLObject.insertBefore(XMLObject parentNode)
      boolean success = newNode.insertBefore(targetParentNode);
      ```
      * 插入到指定节点内部最后一个子节点的位置<br/>
      ```Java
      // boolean XMLObject.insertAfter(XMLObject parentNode)
      boolean success = newNode.insertAfter(targetParentNode);
      ```
    * 添加到指定节点`外部`
      * 追加到标记节点之前
      ```Java
      // boolean XMLObject.appendBefore(XMLObject markerNode, boolean sameLevel)  
      // markerNode必须与newNode是同名节点(推荐)  
      boolean success = newNode.appendBefore(markerNode, true);  
      // markerNode可以是任意节点    
      boolean success = newNode.appendBefore(markerNode, false);  
      ```
      * 追加到标记节点之后
      ```Java
      // boolean XMLObject.appendAfter(XMLObject markerNode, boolean sameLevel)  
      // markerNode必须与newNode是同名节点(推荐)  
      boolean success = newNode.appendBefore(markerNode, true);  
      // markerNode可以是任意节点  
      boolean success = newNode.appendBefore(markerNode, false);  
      ```
  
  * 导出`XML`文件
  ```Java
	// boolean XMLParser.transferRoot(XMLObject root, File outputFile, boolean compact) throws IOException
	// 导出缩进排版的文件
	String path = XMLParserTest2.class.getResource("/").getPath() + "xml-transfer2-retract.xml";
	boolean success = xmlParser.transferRoot(root, new File(path), false);
	
	// 导出紧凑格排版的文件
	path = XMLParserTest2.class.getResource("/").getPath() + "xml-test-transfer2-compact.xml";
	success = xmlParser.transferRoot(root, new File(path), true);
  ```
* Version1.0   
  实现最基本的`XML`读取操作

# 基础解析XML
* 获取`XMLParser`对象
  * 获取`.xml`文件的绝对路径<br/>
  ```Java
  // 这里以`XMLParserTest`为例
  String xmlPath = XMLParserTest.class.getResource("/xml-test-2.xml").getFile();
  ```
  * 获取`XMLParser`对象. 一个`XMLParser`对象即代表一份被解析的`.xml`文件<br>
  ```Java
  XMLParser xmlParser = new XMLParser(xmlPath);
  ```
* 读取`节点`内容
  * 解析`.xml`文件, 获取`XMLObject`对象
  ```Java
  XMLObject root = xmlParser.parse();
  ```
  * 操作`XMLObject`实例对象来读取文件中的内容<br/>
  使用`XMLObject`实例对象可以获取`标签名`, `属性列表`, `子标签实例`, 等等...(API 详情请在`/doc/index.html`中查阅)
