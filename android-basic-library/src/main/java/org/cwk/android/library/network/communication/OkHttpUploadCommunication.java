package org.cwk.android.library.network.communication;

import android.util.Log;

import org.cwk.android.library.network.util.NetworkCallback;
import org.cwk.android.library.network.util.OnNetworkProgressListener;
import org.cwk.android.library.network.util.NetworkRefreshProgressHandler;
import org.cwk.android.library.network.util.ProgressRequestBody;
import org.cwk.android.library.network.util.RequestBodyBuilder;

import java.io.IOException;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 基于OkHttp实现的文件上传请求通讯组件类，
 * 默认文件上传类，不可扩展，
 * 媒体类型multipart/form-data，
 * 同时传输的文本默认UTF-8字符编码提交，不支持其他编码
 *
 * @author 超悟空
 * @version 1.0 2016/7/16
 * @since 1.0
 */
public class OkHttpUploadCommunication extends Communication<Map<String, Object>, String>
        implements NetworkRefreshProgressHandler {

    /**
     * 上传进度监听器
     */
    private OnNetworkProgressListener onNetworkProgressListener = null;

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public OkHttpUploadCommunication(String tag) {
        super(tag);
    }

    @Override
    protected Request onCreateRequest(Map<String, Object> sendData) {
        // 拼接参数
        RequestBody body = RequestBodyBuilder.onBuildUploadForm(logTag, sendData);

        // 考虑是否包装上传进度
        if (onNetworkProgressListener != null) {
            body = new ProgressRequestBody(body, onNetworkProgressListener);
        }

        return new Request.Builder().url(url).post(body).build();
    }

    @Override
    protected void onAsyncSuccess(ResponseBody body, NetworkCallback<String> callback) throws
            IOException {
        callback.onFinish(true, body.string());
    }

    @Override
    public String response() {
        try {
            return response == null ? null : response.string();
        } catch (IOException e) {
            Log.e(logTag, "response error", e);

            return null;
        }
    }

    @Override
    public void setNetworkProgressListener(OnNetworkProgressListener onNetworkProgressListener) {
        this.onNetworkProgressListener = onNetworkProgressListener;
    }
}
