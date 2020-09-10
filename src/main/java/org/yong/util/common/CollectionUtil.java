package org.yong.util.common;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;

/**
 * Collection集合工具
 */
public class CollectionUtil {

    /**
     * 迭代器过滤
     *
     * @param iterable  迭代器
     * @param predicate 断言, true-保留, false-删除
     * @param <E>       迭代器泛型类型
     * @return true-有变化, false-无变化
     */
    public static <E> boolean filter(Iterable<E> iterable, Predicate<E> predicate) {
        Iterator<E> iterator = iterable.iterator();
        boolean changed = false;
        while (iterator.hasNext()) {
            if (!predicate.apply(iterator.next())) {
                iterator.remove();
                changed = true;
            }
        }
        return changed;
    }

    /**
     * 连接列表
     *
     * @param iterable  迭代器
     * @param separator 分隔符
     * @return 连接结果字符串
     */
    public static String join(Iterable<?> iterable, String separator) {
        Iterator<?> iterator = iterable.iterator();
        StringBuilder buf = new StringBuilder();
        while (iterator.hasNext()) {
            buf.append(iterator.next()).append(separator);
        }
        return buf.substring(0, buf.length() - separator.length());
    }

    /**
     * 查找或追加集合元素
     *
     * @param col  集合
     * @param bean 查找条件
     * @return 查找成功返回集合中的元素, 否则追加bean到col并返回bean
     * @see Objects#equals(Object, Object)
     */
    public static <T> T findOrAppend(Collection<T> col, T bean) {
        for (T t : col) {
            if (Objects.equals(t, bean)) {
                return t;
            }
        }

        col.add(bean);
        return bean;
    }

    /**
     * 获取集合中唯一一个元素
     *
     * @param col 集合
     * @param <T> 集合泛型
     * @return 集合中第一个元素, 如果集合中存在多个元素(大于1个)总是返回null
     */
    public static <T> T getByOnlyOne(Collection<T> col) {
        if (CollectionUtils.isNotEmpty(col) && (1 == col.size())) {
            return col.iterator().next();
        }
        return null;
    }

    /**
     * 列表中查找位置
     *
     * @param list      列表
     * @param predicate 断言
     * @param <T>       列表元素类型
     * @return 未找到指定元素是返回-1, 否则返回元素位置
     */
    public static <T> int indexOf(List<T> list, Predicate<T> predicate) {
        int index = -1;
        if (null == list || null == predicate) {
            return index;
        }

        for (int i = 0, len = list.size(); i < len; i++) {
            T bean = list.get(i);
            if (predicate.apply(bean)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 可迭代集合转为Map<br> 如果能保证K的唯一性建议调用 {@link Maps#uniqueIndex(Iterable, Function)}
     *
     * @param iterable  可迭代集合
     * @param converter 转换器
     * @param <I>       可迭代集合泛型类型
     * @param <V>       Map.Value类型
     * @param <K>       Map.Key类型
     * @return Map集合
     * @see Maps#uniqueIndex(Iterable, Function)
     */
    @SuppressWarnings("unchecked")
    public static <I, V, K> Map<K, V> asMap(Iterable<I> iterable, MapConverter<I, V, K> converter) {
        Map<K, V> map = Maps.newLinkedHashMap();
        if (null == iterable)
            return Collections.EMPTY_MAP;

        for (I bean : iterable) {
            K k = converter.toKey(bean);
            V v = converter.toValue(bean, map.get(k));
            map.put(k, v);
        }
        return map;
    }

    /**
     * 列表类型转换
     *
     * @param src 源列表
     * @param <T> 目标类型
     * @param <S> 源类型
     * @return 目标类型列表
     */
    @SuppressWarnings("unchecked")
    public static <T, S extends T> List<T> cast(List<S> src) {
        return (List<T>) src;
    }

    /**
     * 集合合并
     *
     * @param dist   用于保存结果
     * @param src    被合并集合
     * @param unique true-保证合并后数据唯一(必须实现Comparable接口), false-不保证唯一性
     * @param <T>    集合泛型类型
     */
    public static <T> void merge(Collection<T> dist, Collection<T> src, boolean unique) {
        if (CollectionUtils.isEmpty(src)) {
            return;
        }

        if (!unique) {
            dist.addAll(src);
            return;
        }

        Set<T> set = Sets.newLinkedHashSet(dist);
        set.addAll(src);
        dist.clear();
        dist.addAll(set);
    }

    /**
     * 集合合并, 如果两个集合中包含同一个对象将通过handler处理, 否则直接追加到dist中. 该接口保证被添加的数据不会重复
     *
     * @param dist    用于保存结果
     * @param src     被合并集合
     * @param handler 合并处理接口
     * @param <T>     集合泛型类型
     */
    public static <T> void merge(Collection<T> dist, Collection<T> src, AttributeMergeHandler<T> handler) {
        List<T> _dist = Lists.newArrayList(dist);
        for (T b : src) {
            int index = _dist.indexOf(b);
            if (-1 != index) {
                T a = _dist.get(index);
                handler.merge(a, b);
            } else {
                _dist.add(b);
                dist.add(b);
            }
        }
    }

    /**
     * 按<i>avgSize</i>指定大小分割<i>col</i>列表
     *
     * @param col     目标集合
     * @param avgSize 平均值
     * @param fun     分段处理接口
     * @param <T>     集合类型
     * @return 分割后集合列表
     */
    public static <T> List<List<T>> segments(Collection<T> col, int avgSize, Function2<List<T>> fun) {
        if (CollectionUtils.isEmpty(col))
            return null;

        List<List<T>> ret = Lists.newArrayList();
        if (0 >= avgSize)
            throw new RuntimeException("avgSize必须大于0");

        List<T> seg = null;
        int i = 0;
        for (T t : col) {
            if (0 == i) {
                seg = Lists.newArrayList();
                ret.add(seg);
            }

            seg.add(t);
            i++;
            if (avgSize == i) {
                i = 0;
                if (null != fun) {
                    fun.apply(seg);
                }
            }
        }

        if (0 != i && null != fun) {
            fun.apply(seg);
        }

        return ret;
    }

    /**
     * 属性合并处理接口
     */
    public interface AttributeMergeHandler<T> {

        /**
         * 属性合并
         *
         * @param a 对象A
         * @param b 对象B
         */
        void merge(T a, T b);
    }

    /**
     * 断言接口
     *
     * @param <E>
     */
    public interface Predicate<E> {

        /**
         * @param data 数据
         * @return true-通过, false-不通过
         */
        boolean apply(E data);
    }

    /**
     * Collection集合数据处理接口
     *
     * @param <T>
     */
    public interface Function2<T> {
        void apply(T data);
    }

    /**
     * 对象转Map接口
     *
     * @param <I> 输入对象
     * @param <V> Map.Value
     * @param <K> Map.Key
     */
    public static abstract class MapConverter<I, V, K> {
        protected K toKey(I data) {
            throw new UnsupportedOperationException();
        }

        protected V toValue(I in) {
            throw new UnsupportedOperationException();
        }

        protected V toValue(I bean, V old) {
            return toValue(bean);
        }
    }

    /**
     * 对象转Map接口
     *
     * @param <I> 输入对象
     * @param <V> Map.Value
     * @param <K> Map.Key
     */
    public static class SingleMapConverter<I extends Map<K, V>, V, K> extends MapConverter<I, V, K> {

        public K toKey(I in) {
            return in.keySet().iterator().next();
        }

        public V toValue(I in) {
            return in.values().iterator().next();
        }
    }

    /**
     * 数据同步接口
     *
     * @param <T> 目标类型
     * @param <S> 源类型
     */
    public interface Synchronizer<T, S> {

        void sync(T dist, S src);
    }
}
