package org.cwk.android.library.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于打印长日志
 *
 * @author 超悟空
 * @version 1.0 2018/7/21
 * @since 1.0
 */
public class LogUtil {

    /**
     * 日志分段长度
     */
    private static final int LOG_BUFFER_SIZE = 1024 * 3;

    /**
     * 打印v级别的日志
     *
     * @param tag 日志标签
     * @param msg 日志内容
     */
    public static void v(String tag , String msg) {
        for (String message : chunked(msg , LOG_BUFFER_SIZE)) {
            Log.v(tag , message);
        }
    }

    /**
     * 按照指定大小将字符串截取成一组子字符串
     *
     * @param src  原字符串
     * @param size 截取的大小
     *
     * @return 截取后的一组字符串
     */
    private static List<String> chunked(String src , int size) {
        int length = src.length();

        List<String> result = new ArrayList<>((length + size - 1) / size);

        int index = 0;
        while (index < length) {
            int end = index + size;
            int coercedEnd = end > length ? length : end;
            result.add(src.substring(index , coercedEnd));
            index += size;
        }

        return result;
    }
}
