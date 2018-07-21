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
 * 基于OkHttp实现的Post请求通讯组件类，
 * 默认表单提交类，不可扩展，
 * 数据提交使用application/x-www-form-urlencoded表单，
 * 默认UTF-8字符编码提交
 *
 * @author 超悟空
 * @version 2.0 2016/3/7
 * @since 1.0
 */
public class OkHttpPostCommunication extends Communication<Map<String, String>, String> {

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public OkHttpPostCommunication(String tag) {
        super(tag);
    }

    @Override
    protected Request onCreateRequest(Map<String, String> sendData) {

        // 拼接参数
        RequestBody body = RequestBodyBuilder.onBuildPostForm(logTag , sendData , encoded);

        return new Request.Builder().url(url).post(body).build();
    }

    @Override
    protected void onAsyncSuccess(ResponseBody body , NetworkCallback<String> callback) throws
            IOException {
        callback.onFinish(true , code , body.string());
    }

    @Override
    public String response() {
        try {
            return response == null ? null : response.string();
        } catch (IOException e) {
            Log.e(logTag , "response error" , e);

            return null;
        }
    }
}
