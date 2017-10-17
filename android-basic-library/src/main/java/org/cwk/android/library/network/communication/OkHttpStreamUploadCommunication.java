package org.cwk.android.library.network.communication;

import android.util.Log;

import org.cwk.android.library.network.util.NetworkCallback;
import org.cwk.android.library.network.util.NetworkRefreshProgressHandler;
import org.cwk.android.library.network.util.OnNetworkProgressListener;
import org.cwk.android.library.network.util.ProgressRequestBody;
import org.cwk.android.library.network.util.RequestBodyBuilder;

import java.io.IOException;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * 以流的形式上传文件
 *
 * @author 超悟空
 * @version 1.0 2017/8/25
 * @since 1.0 2017/8/25
 **/
public class OkHttpStreamUploadCommunication extends Communication<Map<String, String>, String>
        implements NetworkRefreshProgressHandler {

    /**
     * 日志标签前缀
     */
    private static final String TAG = "OkHttpUploadCommunication";

    /**
     * 文件标签
     */
    private static final String FILE_TAG = "file";

    /**
     * 上传进度监听器
     */
    private OnNetworkProgressListener onNetworkProgressListener = null;

    @Override
    protected Request onCreateRequest(Map<String, String> sendData) {
        String data = null;

        if (!sendData.isEmpty()) {
            data = sendData.remove(FILE_TAG);
        }

        // 拼接参数
        RequestBody body = RequestBodyBuilder.onBuildUploadStream(data);

        // 拼接参数
        String params;
        if (encoded != null) {
            params = RequestBodyBuilder.onBuildParameter(sendData, encoded);
        } else {
            params = RequestBodyBuilder.onBuildParameter(sendData);
        }

        // 最终请求地址
        String finalUrl = params.length() == 0 ? url : url + "?" + params;

        // 考虑是否包装上传进度
        if (onNetworkProgressListener != null) {
            body = new ProgressRequestBody(body, onNetworkProgressListener);
        }

        return new Request.Builder().url(finalUrl).post(body).build();
    }

    @Override
    protected void onAsyncSuccess(ResponseBody body, NetworkCallback<String> callback) throws
            IOException {
        String responseString = body.string();
        Log.v(TAG, "response is " + responseString);
        callback.onFinish(true, responseString);
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

    @Override
    public void setNetworkProgressListener(OnNetworkProgressListener onNetworkProgressListener) {
        this.onNetworkProgressListener = onNetworkProgressListener;
    }
}
