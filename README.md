# XML - Util
简单封装 [`Apache dom4j(1.6.1)`](http://www.dom4j.org/dom4j-1.6.1/ "官方网站"), 封装后将使用面向对象的思维读写 XML 文件</br>
关于 dom4j 的使用就不做过多介绍.<br/>

# 将XML-Util导入到项目中
* [下载](https://github.com/git8023/xmlUtil/archive/master.zip "XML-Util")项目到本地
* 解压后, 将文件夹`/release`下的`/xml-util-1.0-SNAPSHOT.jar`拷贝并`build`到项目中

# 如何使用
* 获取`XMLParser`对象
  * 获取`.xml`文件的绝对路径<br/>
  ```Java
  // 这里以`XMLParserTest`为例
  String xmlPath = XMLParserTest.class.getResource("/xml-test-2.xml").getFile();<br/>
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
<pre>
&lt;dependency&gt;
    &lt;groupId&gt;dom4j&lt;/groupId&gt;
    &lt;artifactId&gt;dom4j&lt;/artifactId&gt;
    &lt;version&gt;1.6.1&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;com.google.guava&lt;/groupId&gt;
    &lt;artifactId&gt;guava&lt;/artifactId&gt;
    &lt;version&gt;12.0&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;log4j&lt;/groupId&gt;
    &lt;artifactId&gt;log4j&lt;/artifactId&gt;
    &lt;version&gt;1.2.17&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;org.slf4j&lt;/groupId&gt;
    &lt;artifactId&gt;slf4j-api&lt;/artifactId&gt;
    &lt;version&gt;1.6.1&lt;/version&gt;
&lt;/dependency&gt;
&lt;dependency&gt;
    &lt;groupId&gt;org.slf4j&lt;/groupId&gt;
    &lt;artifactId&gt;slf4j-log4j12&lt;/artifactId&gt;
    &lt;version&gt;1.7.6&lt;/version&gt;
&lt;/dependency&gt;
<pre>
