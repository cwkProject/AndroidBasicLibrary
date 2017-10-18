package org.cwk.android.library.network.communication;

import android.util.Log;

import org.cwk.android.library.network.util.NetworkCallback;
import org.cwk.android.library.network.util.RequestBodyBuilder;

import java.io.IOException;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 基于OkHttp实现的Put请求通讯组件类，
 * 默认表单提交类，不可扩展，
 * 数据提交使用application/x-www-form-urlencoded表单，
 * 默认UTF-8字符编码提交
 *
 * @author 超悟空
 * @version 1.0 2016/8/3
 * @since 1.0
 */
public class OkHttpPutCommunication extends Communication<Map<String, String>, String> {

    /**
     * 日志标签前缀
     */
    private static final String TAG = "OkHttpPutCommunication";

    @Override
    protected Request onCreateRequest(Map<String, String> sendData) {

        // 拼接参数
        RequestBody body;

        if (encoded != null) {
            body = RequestBodyBuilder.onBuildPostForm(sendData, encoded);
        } else {
            body = RequestBodyBuilder.onBuildPostForm(sendData);
        }

        return new Request.Builder().url(url).put(body).build();
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
            Log.e(TAG, "response error", e);

            return null;
        }
    }
}

