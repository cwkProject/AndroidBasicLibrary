package org.cwk.android.library.network.communication;

import android.util.Log;

import org.cwk.android.library.network.util.NetworkCallback;
import org.cwk.android.library.network.util.NetworkRefreshProgressHandler;
import org.cwk.android.library.network.util.OnNetworkProgressListener;
import org.cwk.android.library.network.util.ProgressResponseBody;
import org.cwk.android.library.network.util.RequestBodyBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 基于OkHttp实现的文件下载请求通讯组件类，
 * 默认文件下载类，不可扩展，
 * 使用get请求访问下载地址
 *
 * @author 超悟空
 * @version 2.0 2016/3/7
 * @since 1.0
 */
public class OkHttpDownloadCommunication extends Communication<Map<String, String>, InputStream>
        implements NetworkRefreshProgressHandler {
    /**
     * 日志标签前缀
     */
    private static final String TAG = "OkHttpDownloadCommunication";

    /**
     * 下载进度监听器
     */
    private OnNetworkProgressListener onNetworkProgressListener = null;

    @Override
    public void setNetworkProgressListener(OnNetworkProgressListener onNetworkProgressListener) {
        this.onNetworkProgressListener = onNetworkProgressListener;
    }

    @Override
    protected OkHttpClient.Builder onRebuildClient(OkHttpClient okHttpClient) {
        OkHttpClient.Builder builder = okHttpClient.newBuilder();

        if (onNetworkProgressListener != null) {
            // 增加拦截器监听下载进度
            builder.networkInterceptors().add(chain -> {
                Response originalResponse = chain.proceed(chain.request());
                return originalResponse.newBuilder().body(new ProgressResponseBody
                        (originalResponse.body(), onNetworkProgressListener)).build();
            });
        }

        return builder;
    }

    @Override
    protected Request onCreateRequest(Map<String, String> sendData) {
        // 拼接参数
        String params;
        if (encoded != null) {
            params = RequestBodyBuilder.onBuildParameter(sendData, encoded);
        } else {
            params = RequestBodyBuilder.onBuildParameter(sendData);
        }

        // 最终请求地址
        String finalUrl = params.length() == 0 ? url : url + "?" + params;
        Log.v(TAG, "final url is " + finalUrl);

        return new Request.Builder().url(finalUrl).build();
    }

    @Override
    protected void onAsyncSuccess(ResponseBody body, NetworkCallback<InputStream> callback)
            throws IOException {
        callback.onFinish(true, body.byteStream());
    }

    @Override
    public InputStream response() {
        return response == null ? null : response.byteStream();
    }
}
