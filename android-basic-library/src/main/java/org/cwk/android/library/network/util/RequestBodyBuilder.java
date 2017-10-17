package org.cwk.android.library.network.util;

import android.support.annotation.NonNull;
import android.util.Log;

import org.cwk.android.library.struct.FileInfo;
import org.cwk.android.library.util.MIMEUtil;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * 请求参数构建器
 *
 * @author 超悟空
 * @version 1.0 2016/3/19
 * @since 1.0
 */
public class RequestBodyBuilder {

    /**
     * 日志标签前缀
     */
    private static final String TAG = "RequestBodyBuilder";

    /**
     * 默认编码
     */
    private static final String CHARSET = "UTF-8";

    /**
     * 拼接参数字符串，用于get请求参数，默认utf-8编码
     *
     * @param sendData 请求参数对
     * @param encoded  编码方式
     *
     * @return 拼接完成的字符串
     */
    @NonNull
    public static String onBuildParameter(Map<String, String> sendData, String encoded) {
        // 请求参数装配器参数
        StringBuilder params = new StringBuilder();

        try {
            // 遍历sendData集合并加入请求参数对象
            if (sendData != null && !sendData.isEmpty()) {
                int count = sendData.size();
                Log.v(TAG, "onBuildParameter sendData count is " + count);

                // 遍历并追加参数
                for (Map.Entry<String, String> dataEntry : sendData.entrySet()) {

                    Log.v(TAG, "parameter is " + dataEntry.getKey() + " = " + dataEntry.getValue());

                    if (dataEntry.getValue() != null) {
                        params.append(dataEntry.getKey());
                        params.append('=');
                        params.append(URLEncoder.encode(dataEntry.getValue(), encoded));
                        params.append('&');
                    }
                }
                // 移除末尾的'&'
                if (params.length() > 0 && params.lastIndexOf("&") == params.length() - 1) {
                    params.deleteCharAt(params.length() - 1);
                }
            }
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "URLEncoder error", e);
        }
        return params.toString();
    }

    /**
     * 拼接参数字符串，用于get请求参数
     *
     * @param sendData 请求参数对
     *
     * @return 拼接完成的字符串
     */
    @NonNull
    public static String onBuildParameter(Map<String, String> sendData) {
        return onBuildParameter(sendData, CHARSET);
    }

    /**
     * 创建文本post表单
     *
     * @param sendData 要发送的参数对
     * @param encoded  编码方式
     *
     * @return 装配好的表单
     */
    public static RequestBody onBuildPostForm(Map<String, String> sendData, String encoded) {
        FormBody.Builder builder = new FormBody.Builder(Charset.forName(encoded));

        // 遍历sendData集合并加入请求参数对象
        if (sendData != null && !sendData.isEmpty()) {
            int count = sendData.size();
            Log.v(TAG, "onBuildPostForm sendData count is " + count);

            // 遍历并追加参数
            for (Map.Entry<String, String> dataEntry : sendData.entrySet()) {
                Log.v(TAG, "parameter is " + dataEntry.getKey() + " = " + dataEntry.getValue());
                if (dataEntry.getValue() != null) {
                    // 加入表单
                    builder.add(dataEntry.getKey(), dataEntry.getValue());
                }
            }
        }

        return builder.build();
    }

    /**
     * 创建文本post表单，默认utf-8编码
     *
     * @param sendData 要发送的参数对
     *
     * @return 装配好的表单
     */
    public static RequestBody onBuildPostForm(Map<String, String> sendData) {
        return onBuildPostForm(sendData, CHARSET);
    }

    /**
     * 创建上传post表单
     *
     * @param sendData 要发送的参数对
     *
     * @return 装配好的表单
     */
    public static RequestBody onBuildUploadForm(Map<String, Object> sendData) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);

        // 遍历sendData集合并加入请求参数对象
        if (sendData != null && !sendData.isEmpty()) {
            int count = sendData.size();
            Log.v(TAG, "onBuildUploadForm sendData count is " + count);

            // 遍历并追加参数
            for (Map.Entry<String, Object> dataEntry : sendData.entrySet()) {
                Log.v(TAG, "parameter is " + dataEntry.getKey() + " = " + dataEntry.getValue());

                if (dataEntry.getValue() instanceof FileInfo) {
                    // 参数是文件包装类型
                    FileInfo value = (FileInfo) dataEntry.getValue();
                    // 加入表单
                    builder.addFormDataPart(dataEntry.getKey(), value.getFileName(), RequestBody
                            .create(MediaType.parse(value.getMimeType()), value.getFile()));
                    continue;
                }

                if (dataEntry.getValue() instanceof File) {
                    // 参数是文件类型
                    File value = (File) dataEntry.getValue();
                    // 加入表单
                    builder.addFormDataPart(dataEntry.getKey(), value.getName(), RequestBody
                            .create(MediaType.parse(MIMEUtil.getMimeType(value)), value));
                    continue;
                }

                // 处理剩余情况，包括String，Integer，Boolean等类型
                if (dataEntry.getValue() != null) {
                    // 参数不为空
                    // 加入表单
                    builder.addFormDataPart(dataEntry.getKey(), String.valueOf(dataEntry.getValue
                            ()));
                }
            }
        }

        return builder.build();
    }

    /**
     * 创建上传流
     *
     * @param path 要上传的文件路径
     *
     * @return 装配好的表单
     */
    public static RequestBody onBuildUploadStream(String path) {
        if (path != null) {
            File file = new File(path);

            if (file.exists()) {
                Log.v(TAG, "onBuildUploadStream sendStream is " + path);
                return RequestBody.create(MediaType.parse("application/octet-stream"), file);
            } else {
                Log.d(TAG, "no file " + path);
            }
        }

        return RequestBody.create(MediaType.parse("application/octet-stream"), new byte[]{});
    }
}
