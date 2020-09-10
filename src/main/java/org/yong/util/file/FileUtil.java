package org.yong.util.file;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.yong.util.common.StringUtil;

/**
 * 文件工具
 *
 * @author yong2
 */
@Slf4j
public class FileUtil extends FileUtils {

    /**
     * 创建文件所在目录
     *
     * @param filePath 完整文件名
     * @return filePath 指向的文件对象
     */
    public static File createFileParentDir(String filePath) {
        File file = new File(filePath);
        File dir = file.getParentFile();
        if (!dir.exists())
            dir.mkdirs();
        return file;
    }

    /**
     * 创建文件所在目录
     *
     * @param file 文件对象
     * @return 创建成功返回true, 否则返回false
     */
    public static boolean createFileParentDir(File file) {
        File dir = file.getParentFile();
        if (!dir.exists())
            return dir.mkdirs();
        return false;
    }

    /**
     * 创建文件, 已存在就删除
     *
     * @param file 目标文件
     * @return 创建成功返回true, 否则返回false
     */
    public static boolean createFile(File file) {
        createFileParentDir(file);
        if (file.exists() && file.delete())
            log.debug("删除已存在文件");

        try {
            return file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 列出目录中所有文件
     *
     * @param dir 目录
     * @return 文件映射, Key-文件名, Value-文件
     */
    public static Map<String, File> mapDir(File dir) {
        if (null == dir || dir.isFile())
            return null;
        Map<String, File> map = Maps.newLinkedHashMap();
        File[] files = dir.listFiles();
        if (null == files)
            return map;
        for (File file : files)
            map.put(file.getName(), file);
        return map;
    }

    /**
     * 读取文件内容
     *
     * @param file    文件
     * @param charSet 字符集
     * @return 内容列表
     */
    public static List<String> readLines(File file, String charSet) {
        try {
            return FileUtils.readLines(file, charSet);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件名后缀, 不包含点号(.)
     *
     * @param fileName 文件名
     * @return 文件名后缀
     */
    private static String getSuffix(String fileName) {
        if (null == fileName)
            return "unknow";

        int index = fileName.lastIndexOf('.');
        if (-1 == index)
            return StringUtil.EMPTY;
        return fileName.substring(index + 1);
    }

    /**
     * 从文件末尾开始读取文本行
     * <pre>
     * 1. 如果0>=lineTotal总是返回空列表;
     * 2. 如果lineTotal>maxLines列表长度为文件总行数;
     * 3. 否则列表长度和lineTotal相等;
     * </pre>
     *
     * @param file      文件对象
     * @param lineTotal 期望读取的总行数
     * @return 文本信息列表, 顺序与文件内容一致
     */
    public static List<String> reverseReadLines(File file, int lineTotal) {
        List<String> lines = Lists.newArrayList();
        int surplus = lineTotal;
        if (0 >= surplus)
            return lines;

        try (RandomAccessFile rf = new RandomAccessFile(file, "r")) {
            long len = rf.length();
            long start = rf.getFilePointer();

            // 从最后一个字节开始读取
            long nextEnd = start + len - 1;
            rf.seek(nextEnd);

            int c;
            while (nextEnd > start) {

                // 当前读取的字节正好是换行符
                // 读取这一行
                c = rf.read();
                if (c == '\n' || c == '\r') {
                    String line = rf.readLine();
                    if (null != line) {
                        lines.add(line);
                        if (0 >= --surplus)
                            break;
                    }

                    nextEnd--;
                }

                nextEnd--;
                rf.seek(nextEnd);

                // 当文件指针退至文件开始处，输出第一行
                if (nextEnd == 0) {
                    String line = rf.readLine();
                    if (null != line) {
                        lines.add(line);
                        if (0 >= --surplus)
                            break;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Collections.reverse(lines);
        List<String> ret = Lists.newArrayList();
        for (String s : lines)
            ret.add(StringUtil.encode(s, "ISO-8859-1", StandardCharsets.UTF_8.name()));

        return ret;
    }
}
