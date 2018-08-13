package org.cwk.android.library.network.communication;

import android.util.Log;

import org.cwk.android.library.network.util.NetworkCallback;
import org.cwk.android.library.network.util.RequestBodyBuilder;

import java.io.IOException;
import java.util.Map;

import okhttp3.Request;
import okhttp3.ResponseBody;

/**
 * 基于OkHttp实现的Get请求通讯组件类
 *
 * @author 超悟空
 * @version 2.0 2016/3/7
 * @since 1.0
 */
public class OkHttpGetCommunication extends OkHttpCommunication<Map<String, String>, String> {

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public OkHttpGetCommunication(String tag) {
        super(tag);
    }

    @Override
    protected void onCreateRequest(Request.Builder builder , Map<String, String> sendData) {

        // 拼接参数
        String params = RequestBodyBuilder.onBuildParameter(logTag , sendData , encoded);

        // 最终请求地址
        String finalUrl = params.length() == 0 ? url : url + "?" + params;
        Log.v(logTag , "final url:" + finalUrl);

        builder.url(finalUrl);
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
