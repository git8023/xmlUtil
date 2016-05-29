# XML - Util
简单封装 [`Apache dom4j(1.6.1)`](http://www.dom4j.org/dom4j-1.6.1/ "官方网站"), 封装后将使用面向对象的思维读写 XML 文件</br>
关于 dom4j 的使用就不做过多介绍.<br/>

# 将XML-Util导入到项目中
* [下载](https://github.com/git8023/xmlUtil/archive/master.zip "XML-Util")项目到本地
* 解压后, 将文件夹`/release`下的`/xml-util-x.x-SNAPSHOT.jar`拷贝并`build`到项目中
* 源码位于文件夹`/release`下的`xmlUtil-src-x.x.jar`

# 版本
* v1.1 <b>new</b><br>
  新增功能:<br>
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
* v1.0<br>
  实现最基本的`XML`读取操作

# 如何使用
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

# `maven`依赖
```xml
<!-- v1.1 -->
<dependency>
    <groupId>commons-io</groupId>
    <artifactId>commons-io</artifactId>
    <version>2.4</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.4</version>
</dependency>

<!-- v1.1 -->
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.12</version>
</dependency>
<dependency>
    <groupId>dom4j</groupId>
    <artifactId>dom4j</artifactId>
    <version>1.6.1</version>
</dependency>
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>12.0</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-collections4</artifactId>
    <version>4.0</version>
</dependency>
<dependency>
    <groupId>log4j</groupId>
    <artifactId>log4j</artifactId>
    <version>1.2.17</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-api</artifactId>
    <version>1.6.1</version>
</dependency>
<dependency>
    <groupId>org.slf4j</groupId>
    <artifactId>slf4j-log4j12</artifactId>
    <version>1.7.6</version>
</dependency>
```
