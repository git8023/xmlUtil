package org.yong.util.file.xml;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.yong.util.file.xml.annotation.FieldType;
import org.yong.util.file.xml.annotation.XmlField;
import org.yong.util.file.xml.annotation.XmlTag;
import org.yong.util.common.StringUtil;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.Map.Entry;

/**
 * XML文件对象
 *
 * @author Huang.Yong
 */
@Slf4j
public class XMLObject implements Serializable {

    // @Fields serialVersionUID :
    private static final long serialVersionUID = 7702755997734263716L;

    /**
     * 属性列表
     */
    private Map<String, String> attrs = new HashMap<>();

    /**
     * 子标签集合, Key:tagName, Value:List&lt;XMLObject&gt;
     */
    private Map<String, List<XMLObject>> childTags = new LinkedHashMap<>();

    /**
     * 标签文本内容
     */
    private String content;

    /**
     * 是否根节点
     */
    private boolean rootElement;

    /**
     * 父级节点
     */
    private XMLObject parent;

    /**
     * 标签名
     */
    private String tagName;

    /**
     * 构建XML对象
     *
     * @param tagName 标签名
     */
    XMLObject(String tagName) {
        super();
        this.tagName = tagName;
    }

    /**
     * 构建XML对象
     *
     * @param tagName 标签名
     * @param content 标签体
     * @param attrs   标签属性表
     */
    XMLObject(String tagName, String content, Map<String, String> attrs) {
        super();
        this.tagName = tagName;
        this.content = content;
        this.attrs = attrs;
    }

    /**
     * 添加标签属性
     *
     * @param attrName  属性名
     * @param attrValue 属性值
     */
    public void addAttr(String attrName, String attrValue) {
        getAttrs().put(attrName, attrValue);
    }

    /**
     * 添加子标签
     *
     * @param xmlObject 子标签对象
     */
    public void addChildTag(XMLObject xmlObject) {
        Map<String, List<XMLObject>> localSubTags = getChildTags();

        // 验证是否已存在当前标签
        String tagName = xmlObject.tagName;
        List<XMLObject> subTags = localSubTags.computeIfAbsent(tagName, k -> new ArrayList<>());

        // 添加标签
        subTags.add(xmlObject);
    }

    /**
     * 追加到指定标签节点之后(外部)
     *
     * @param markerNode 标记性节点
     * @param sameLevel  当前节点必须和标记节点处于相同层次(<i>将相同节点几种到一起更便于维护和管理</i>)
     * @return boolean true-成功. false-失败,当前为根节点或<i>node</i>为根节点/漂浮状态时,
     * 或当前节点和标记节点不是同名节点(当<i>mustBeSameTagName = true</i>时)
     */
    public boolean appendAfter(XMLObject markerNode, boolean sameLevel) {
        // 追加前验证
        boolean valid = validationOuterEdit(markerNode);
        if (!valid) {
            return false;
        }

        // 同级元素验证
        if (sameLevel && !sameTagName(markerNode)) {
            log.debug("The marker node name must be the same as current node name");
            return false;
        }

        // 获取marker父节点
        XMLObject markerParent = markerNode.getParent();

        // 获取marker的同级节点列表, 找到marker在列表中的位置
        List<XMLObject> markerLevelChildren = markerParent.getChildTags(markerNode.getTagName());
        LinkedList<XMLObject> linkedList = Lists.newLinkedList(markerLevelChildren);
        int markerIdx = linkedList.indexOf(markerNode);

        // 漂浮当前节点
        this.setFloating();

        // 把当前节点追加到marker同级节点中, 位置在marker之前
        linkedList.add(markerIdx, this);
        markerLevelChildren.clear();
        markerLevelChildren.addAll(linkedList);

        // 当前节点父节点设置为marker父节点
        this.setParent(markerParent);

        return true;
    }

    /**
     * 追加到指定标签节点之前(外部)
     *
     * @param markerNode 标记性节点
     * @param sameLevel  当前节点必须和标记节点处于相同层次(<i>将相同节点几种到一起更便于维护和管理</i>)
     * @return boolean true-成功. false-失败,当前为根节点或<i>node</i>为根节点/漂浮状态时,
     * 或当前节点和标记节点不是同名节点(当<i>mustBeSameTagName = true</i>时)
     */
    public boolean appendBefore(XMLObject markerNode, boolean sameLevel) {
        boolean valid = validationOuterEdit(markerNode);
        if (!valid) {
            return false;
        }

        // 当前节点和标记节点必须是同名节点
        if (sameLevel && !sameTagName(markerNode)) {
            log.debug("The marker node name must be the same as current node name");
            return false;
        }

        // 获取标记节点父节点
        XMLObject markerNodeParent = markerNode.getParent();

        // 获取父节点中标记节点子节点集合
        List<XMLObject> markerLevelChildren = markerNodeParent.getChildTags(markerNode.getTagName());
        LinkedList<XMLObject> linkedList = Lists.newLinkedList(markerLevelChildren);

        // 找到标记节点在集合中的位置
        int markerIdx = linkedList.indexOf(markerNode);

        // 设置当前节点为漂浮状态
        this.setFloating();

        // 将当前节点移动到标记节点之前
        linkedList.add(markerIdx, this);
        markerLevelChildren.clear();
        markerLevelChildren.addAll(linkedList);

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof XMLObject)) return false;
        XMLObject xmlObject = (XMLObject) o;
        return rootElement == xmlObject.rootElement &&
                Objects.equals(attrs, xmlObject.attrs) &&
                Objects.equals(childTags, xmlObject.childTags) &&
                Objects.equals(content, xmlObject.content) &&
                Objects.equals(parent, xmlObject.parent) &&
                tagName.equals(xmlObject.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attrs, childTags, content, rootElement, parent, tagName);
    }

    @Override
    public String toString() {
        return "XMLObject{" +
                "attrs=" + attrs +
                //", childTags=" + childTags +
                ", content='" + content + '\'' +
                ", isRoot=" + rootElement +
                //", parent=" + parent +
                ", tagName='" + tagName + '\'' +
                '}';
    }

    /**
     * 获取当前节点下所有的子节点, 包括当前元素和后代元素
     *
     * @param childTagName 子节点标签名
     * @return List&lt;XMLObject&gt; 指定标签列表
     */
    public List<XMLObject> getAllChildTags(String childTagName) {
        List<XMLObject> result = Lists.newArrayList();
        getAllChild(this, childTagName, result);
        return result;
    }

    /**
     * 获取指定属性, 如果属性不存在或没有值, 总是返回{@link StringUtil#EMPTY_STRING}
     *
     * @param attrName 属性名
     * @return String 属性值
     */
    public String getAttr(String attrName) {
        String attrVal = getAttrs().get(attrName);
        return StringUtil.isNotEmpty(attrVal, true) ? attrVal : StringUtil.EMPTY_STRING;
    }

    /**
     * 获取标签属性列表
     *
     * @return Map&lt;String,String&gt; 标签属性集合, Key:属性名, Value:属性值
     */
    public Map<String, String> getAttrs() {
        if (null == this.attrs) {
            this.attrs = new HashMap<>();
        }
        return this.attrs;
    }

    /**
     * 获取指定子标签
     *
     * @param tagName 子标签名
     * @param index   第几个 <i>tagName</i> 指定的子标签
     * @return XMLObject XML 节点对象
     */
    public XMLObject getChildTag(String tagName, int index) {
        List<XMLObject> subTags = getChildTags(tagName);
        if (index >= subTags.size() || index < 0) {
            return null;
        }
        return subTags.get(index);
    }

    /**
     * 获取所有子标签
     *
     * @return Map&lt;String,List&lt;XMLObject&gt;&gt; 子标签集合, Key:标签名,
     * Value:当前标签下所有与标签名关联的一级子标签
     */
    public Map<String, List<XMLObject>> getChildTags() {
        if (null == this.childTags) {
            this.childTags = Maps.newHashMap();
        }
        return this.childTags;
    }

    /**
     * 获取子标签
     *
     * @param tagName 标签名
     * @return List&lt;XMLObject&gt; 当前标签包含的所有子标签, 总是返回合法的列表对象
     */
    public List<XMLObject> getChildTags(String tagName) {
        List<XMLObject> result = getChildTags().get(tagName);
        if (null == result) {
            result = Lists.newArrayList();
            getChildTags().put(tagName, result);
        }
        return result;
    }

    /**
     * 获取当前标签文本内容
     *
     * @return String 存文本内容
     */
    public String getContent() {
        return content;
    }

    /**
     * 获取当前节点父节点
     *
     * @return XMLObject 父节点对象
     */
    public XMLObject getParent() {
        return parent;
    }

    /**
     * 获取当前标签名
     *
     * @return String 标签名
     */
    public String getTagName() {
        return tagName;
    }

    /**
     * 是否包含指定属性名
     *
     * @param attrName 属性名
     * @return boolean true-包含指定属性, false-不包含指定属性
     */
    public boolean hasAttr(String attrName) {
        return (this.getAttrs().containsKey(attrName));
    }

    /**
     * 验证是否包含目标子标签
     *
     * @param subTag 目标子标签
     * @return boolean true-包含, false-不包含
     */
    public boolean hasChildTag(XMLObject subTag) {
        if (null == childTags || subTag == null) {
            return false;
        }

        String subTagName = subTag.getTagName();
        List<XMLObject> list = getChildTags().get(subTagName);
        return (null != list) && list.contains(subTag);
    }

    /**
     * 插入到指定节点之后(内部)
     *
     * @param parentNode 父节点
     * @return boolean true-成功, false-失败,当前为根节点或当前节点为漂浮状态时
     */
    public boolean insertAfter(XMLObject parentNode) {
        boolean valid = validationInnerEdit(parentNode);
        if (!valid) {
            log.warn("validation failed at before append");
            return false;
        }

        // 从原有的父节点中移出当前节点
        this.setFloating();

        // 获取当前节点在目标父节点的同级元素
        List<XMLObject> currLevelChildren = parentNode.getChildTags(this.getTagName());

        // 将当前节点添加到目标父节点内容的最末尾
        LinkedList<XMLObject> linkedList = Lists.newLinkedList(currLevelChildren);
        linkedList.addLast(this);
        currLevelChildren.clear();
        currLevelChildren.addAll(linkedList);

        return true;
    }

    /**
     * 插入到指定节点之前(内部)
     *
     * @param parentNode 父节点, 父节点不能是漂浮状态
     * @return boolean true-成功, false-失败,当前为根节点
     */
    public boolean insertBefore(XMLObject parentNode) {
        boolean valid = validationInnerEdit(parentNode);
        if (!valid) {
            return false;
        }

        // 获取目标父节点的子节点集合,
        // 子节点集合与当前节点名相关
        List<XMLObject> currLevelchildren = parentNode.getChildTags(this.getTagName());

        // 从原来所属父节点中移出当前节点
        setFloating();

        // 将当前节点插入到子节点第一个
        LinkedList<XMLObject> linkedList = Lists.newLinkedList(currLevelchildren);
        currLevelchildren.clear();
        linkedList.addFirst(this);
        currLevelchildren.addAll(linkedList);

        // 设置父节点
        this.setParent(parentNode);

        return true;
    }

    /**
     * 校验当前节点是否根节点
     *
     * @return boolean true-根节点, false-不是根节点
     */
    public boolean isRootElement() {
        return rootElement;
    }

    /**
     * 设置标签文本内容
     *
     * @param content 标签存文本内容
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * 设置当前标签名
     *
     * @param tagName 当前标签名
     */
    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 验证当前节点是否有效
     *
     * @return boolean true-有效节点, false-无效节点
     */
    public boolean valid() {
        return StringUtil.isNotEmpty(this.tagName, true);
    }

    /**
     * 映射为实体类
     *
     * @param cls 实体类字节码
     * @param <T> 实体类泛型
     * @return 实体类对象
     */
    public <T> T toBean(Class<T> cls) {
        validExpectTagName(cls);

        T bean;
        try {
            bean = cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // 填充一级属性
        // 私有属性
        List<Field> fields = Lists.newArrayList(Arrays.asList(cls.getFields()));
        fields.addAll(Arrays.asList(cls.getDeclaredFields()));
        for (Field field : fields) {

            // 不处理 final/static 修饰属性
            int modifiers = field.getModifiers();
            if (Modifier.isFinal(modifiers) || Modifier.isStatic(modifiers))
                continue;

            setValue(bean, field);
        }

        return bean;
    }

    /**
     * 指定控件解析为列表
     *
     * @param childTagName 子标签名
     * @param cls          实体类字节码
     * @param <T>          实体类类型
     * @return 实体类列表
     */
    public <T> List<T> toBeans(String childTagName, Class<T> cls) {
        List<T> beans = Lists.newArrayList();

        List<XMLObject> children = getAllChildTags(childTagName);
        for (XMLObject xmlObject : children) {
            T bean = xmlObject.toBean(cls);
            beans.add(bean);
        }

        return beans;
    }

    /**
     * 校验当前节点与另一个节点属于同名节点
     *
     * @param other 另一个节点
     * @return boolean true-同名节点,false-不是同名节点
     */
    private boolean sameTagName(XMLObject other) {
        return this.getTagName().equals(other.getTagName());
    }

    /**
     * 校验当前节点是否漂浮状态(只能代表当前节点, 而不能代表其上级节点是否也是漂浮状态)
     *
     * @return boolean true-漂浮状态, false-不是漂浮状态
     */
    private boolean isFloating() {
        // 不是根节点, 并且没有相应的父节点时
        return !this.isRootElement() && null == this.getParent();
    }

    /**
     * 追加到result中
     *
     * @param xmlObject    xml节点元素
     * @param childTagName 目标节点名称
     * @param result       用于保存节点列表
     */
    private void appendTag(XMLObject xmlObject, String childTagName, List<XMLObject> result) {
        if (StringUtil.equalsTwo(xmlObject.getTagName(), childTagName)) {
            result.add(xmlObject);
        }
    }

    /**
     * 获取所有子节点
     *
     * @param xmlObject    节点对象
     * @param childTagName 目标节点名称
     * @param result       用于保存节点列表
     */
    private void getAllChild(XMLObject xmlObject, String childTagName, List<XMLObject> result) {
        appendTag(xmlObject, childTagName, result);

        // 验证是否还有后代元素
        Map<String, List<XMLObject>> children = xmlObject.getChildTags();
        if (0 >= children.size()) {
            return;
        }

        // 遍历所有后代标签
        for (Entry<String, List<XMLObject>> child : children.entrySet()) {
            List<XMLObject> descendants = child.getValue();

            if (0 >= descendants.size()) {
                appendTag(xmlObject, childTagName, result);
                continue;
            }

            for (XMLObject descendant : descendants) {
                getAllChild(descendant, childTagName, result);
            }
        }
    }


    /**
     * 从父节点中将当前节点移出
     */
    private void setFloating() {
        if (this.isRootElement()) {
            throw new RuntimeException("Cann't delete root element");
        }

        XMLObject currParent = this.getParent();
        if (null != currParent) {
            Map<String, List<XMLObject>> currParentChildren = currParent.getChildTags();

            // 从父节点的子节点集合中删除当前节点
            String currTagName = this.getTagName();
            List<XMLObject> currLevelChildren = currParentChildren.get(currTagName);
            boolean removed = currLevelChildren.remove(this);
            if (!removed) {
                log.debug("Delete the current node failure in the current layer[" + currTagName + "]");
            }

            // 删除无效的子节点记录
            if (CollectionUtils.isEmpty(currLevelChildren)) {
                log.debug("Clear invalid children set[" + currTagName + "]");
                currParentChildren.remove(currTagName);
            }
            this.parent = null;
        }
    }

    /**
     * 设置父级节点
     *
     * @param parent 父节点对象
     */
    void setParent(XMLObject parent) {
        this.parent = parent;
    }

    /**
     * 设置是否根节点
     *
     * @param isRoot true-根节点, false-叶子节点
     */
    void setRootElement(boolean isRoot) {
        this.rootElement = isRoot;
    }


    /**
     * 编辑节点前结构验证, 当前节点不是root节点且父节点不是漂浮状态
     *
     * @param targetParent 目标父节点
     * @return true-验证通过, false-验证失败
     */
    private boolean validationInnerEdit(XMLObject targetParent) {
        if (!valid()) {
            log.debug("The current node is invalid");
            return false;
        } else if (this.isRootElement()) {
            log.debug("The current node cann't is root element");
            return false;
        }

        if (!targetParent.valid()) {
            log.debug("The target parent node is invalid");
            return false;
        } else if (targetParent.isFloating()) {
            log.debug("Taget the parent node is unstable");
            return false;
        }

        return true;
    }

    /**
     * 验证是否能执行编辑, 相对于标记节点
     *
     * @param markerNode 标记节点
     * @return boolean true-验证通过, false-验证失败
     */
    private boolean validationOuterEdit(XMLObject markerNode) {
        // 当前节点和标记节点不能是同一个节点
        if (this.equals(markerNode)) {
            log.debug("The marker node cann't equal to current node");
            return false;
        }

        // 当前节点不能是根节点
        if (this.isRootElement()) {
            log.debug("The current node cann't is root node");
            return false;
        }

        // 标记节点不能是根节点, 且不能为漂浮状态
        if (markerNode.isRootElement()) {
            log.debug("The marker node cann't is root node");
            return false;
        } else if (markerNode.isFloating()) {
            log.debug("The marker node is not stable");
            return false;
        }

        return true;
    }

    /**
     * 设置值
     *
     * @param bean  数据对象
     * @param field 字段
     */
    private void setValue(Object bean, Field field) {

        // 不处理没有添加字段注解的属性
        XmlField xmlField = field.getAnnotation(XmlField.class);
        if (null == xmlField)
            return;

        String name = getTargetTagName(xmlField, field.getName());
        FieldType type = xmlField.type();
        try {
            switch (type) {

                // 属性直接映射
                case ATTRIBUTE:
                    XMLObject target = parseFieldPath(xmlField);
                    if (null != target) {
                        String attr = target.getAttr(name);
                        Reflects.setJsonValue(bean, field, attr);
                    }
                    break;

                // 子标签
                case TAG:
                    setValueByTag(bean, field, xmlField);
                    break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从子标签填充复杂属性值
     *
     * @param bean     目标对象
     * @param field    字段对象
     * @param xmlField XML字段映射注解
     */
    private void setValueByTag(Object bean, Field field, XmlField xmlField) throws Exception {
        Class<?> fieldType = field.getType();
        field.setAccessible(true);

        // 支持 path + hierarchy 寻路
        XMLObject target = parseFieldPath(xmlField);
        if (null == target)
            return;

        if (trySimpleValueByTag(bean, field, target)
                || tryCollectionOrArray(bean, field)
                || tryCustomType(bean, field)) {
        }
    }

    /**
     * 尝试自定义类型
     *
     * @param bean  数据对象
     * @param field 字段对象
     * @return 成功处理返回true, 否则返回false(需要其他方式处理)
     */
    private boolean tryCustomType(Object bean, Field field) throws IllegalAccessException {

        XmlField xmlField = field.getAnnotation(XmlField.class);
        Class<?> fieldType = field.getType();
        // 自定义类型
        if (!fieldType.isAnnotationPresent(XmlTag.class))
            return false;

        // 复合属性只能解析唯一的子标签
        // 如果标签出现多个证明实体类属性类型定义错误
        String childTagName = getTargetTagName(xmlField, fieldType.getSimpleName());
        List<XMLObject> children = getChildTags(childTagName);
        if (1 < children.size())
            throw new RuntimeException("期望唯一子标签[" + childTagName + "]实际找到[" + children.size() + "]条");

        if (1 == children.size()) {
            XMLObject firstChildTag = children.get(0);
            Object val = firstChildTag.toBean(fieldType);
            field.set(bean, val);
        }

        return true;
    }

    /**
     * 尝试设置列表或者数组
     *
     * @param bean  数据对象
     * @param field 字段对象
     * @return 成功处理返回true, 否则返回false(需要其他方式处理)
     */
    private boolean tryCollectionOrArray(Object bean, Field field) throws IllegalAccessException {
        XmlField xmlField = field.getAnnotation(XmlField.class);
        Class<?> fieldType = field.getType();

        // 列表&数组
        Reflects.CollectionType collectionType = Reflects.isCollection(fieldType);
        if (null == collectionType)
            return false;

        // List & Set
        if (Reflects.CollectionType.LIST == collectionType || Reflects.CollectionType.SET == collectionType) {

            // 获取泛型类型
            // 如果没有泛型不设置当前值
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {

                // 泛型类型必须被 XmlTag 注解, 否则不予解析
                ParameterizedType pt = (ParameterizedType) genericType;
                Class<?> type = (Class<?>) pt.getActualTypeArguments()[0];
                if (!type.isAnnotationPresent(XmlTag.class)) {
                    log.warn("请检查泛型类型[" + type.getName() + "]是否添加 @XmlTag 注解");
                    return true;
                }

                String childTagName = getTargetTagName(xmlField, type.getSimpleName());
                List<?> val = toBeans(childTagName, type);

                // 如果目标集合是Set集合, 从List集合转
                if (Reflects.CollectionType.SET == collectionType)
                    field.set(bean, new LinkedHashSet<>(val));
                else
                    field.set(bean, val);
            }
            return true;
        }

        // Array
        if (Reflects.CollectionType.ARRAY == collectionType) {
            Class<?> type = fieldType.getComponentType();
            String childTagName = StringUtil.defaultString(xmlField.name(), type.getSimpleName());
            List<?> val = toBeans(childTagName, type);
            field.set(bean, val.toArray());
        }
        return true;
    }

    /**
     * 获取属性映射的目标标签或属性名称
     *
     * @param xmlField    映射注解
     * @param defaultName 默认名称
     * @return 映射名称
     */
    private String getTargetTagName(XmlField xmlField, String defaultName) {
        return StringUtil.defaultIfBlank(xmlField.name(), defaultName);
    }

    /**
     * 使用子标签设置简单值
     *
     * @param bean   数据对象
     * @param field  字段对象
     * @param target 目标标签
     * @return 成功处理返回true, 否则返回false(需要其他方式处理)
     */
    private boolean trySimpleValueByTag(Object bean, Field field, XMLObject target) throws Exception {
        Class<?> fieldType = field.getType();
        XmlField xmlField = field.getAnnotation(XmlField.class);

        // 只处理简单对象
        if (!Reflects.isSimpleType(fieldType))
            return false;

        // 子节点个数检查
        String childTagName = getTargetTagName(xmlField, fieldType.getSimpleName());
        List<XMLObject> children = target.getChildTags(childTagName);

        if (1 < children.size())
            throw new RuntimeException("期望唯一子标签[" + childTagName + "]实际找到[" + children.size() + "]条");

        if (1 == children.size()) {
            XMLObject firstChildTag = children.get(0);
            String val = firstChildTag.content;
            Reflects.setJsonValue(bean, field, val);
        }
        return true;
    }

    /**
     * 解析字段路径配置
     *
     * @param xmlField 字段配置
     * @return 目标XML节点
     */
    private XMLObject parseFieldPath(XmlField xmlField) {
        String[] path = xmlField.path();
        XMLObject target = this;

        List<String> rightPaths = new ArrayList<>();
        for (String node : path) {
            int tagIndex = 0;
            String[] split = node.split("\\[");
            if (2 < split.length) {
                log.info("成功解析路径: " + rightPaths);
                throw new RuntimeException("无效的子标签索引规则: " + node + ", 最多支持一维数组索引.");
            }

            if (2 == split.length) {
                node = split[0];
                String indexStr = split[1].substring(0, split[1].length() - 2);
                try {
                    tagIndex = Integer.parseInt(indexStr);
                } catch (NumberFormatException e) {
                    log.info("成功解析路径: " + rightPaths);
                    throw new RuntimeException("索引下标[" + indexStr + "]解析失败不能转化为数字", e);
                }
            }

            rightPaths.add(node);
            target = target.getChildTag(node, tagIndex);
        }
        return target;
    }

    /**
     * 校验期望标签名, 校验失败抛出异常
     *
     * @param cls 实体类字节码
     * @param <T> 实体类泛型
     */
    private <T> void validExpectTagName(Class<T> cls) {
        XmlTag xmlTag = cls.getAnnotation(XmlTag.class);
        if (null != xmlTag) {
            String expectName = xmlTag.value();
            expectName = StringUtil.trimToEmpty(expectName);
            if (StringUtil.isNotBlank(expectName) && !expectName.equals(tagName)) {
                throw new RuntimeException("期望标签名[" + expectName + "]与实际标签名[" + tagName + "]不一致");
            }
        }
    }

}