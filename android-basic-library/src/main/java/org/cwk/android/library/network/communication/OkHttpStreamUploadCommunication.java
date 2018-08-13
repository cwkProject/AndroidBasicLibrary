package org.cwk.android.library.network.communication;

import android.util.Log;

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
public class OkHttpStreamUploadCommunication extends OkHttpCommunication<Map<String, String>,
        String> implements NetworkRefreshProgressHandler {

    /**
     * 文件标签
     */
    private static final String FILE_TAG = "file";

    /**
     * 上传进度监听器
     */
    private OnNetworkProgressListener onNetworkProgressListener = null;

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public OkHttpStreamUploadCommunication(String tag) {
        super(tag);
    }

    @Override
    protected void onCreateRequest(Request.Builder builder , Map<String, String> sendData) {
        String data = null;

        if (!sendData.isEmpty()) {
            data = sendData.remove(FILE_TAG);
        }

        // 拼接参数
        RequestBody body = RequestBodyBuilder.onBuildUploadStream(logTag , data);

        // 拼接参数
        String params = RequestBodyBuilder.onBuildParameter(logTag , sendData , encoded);

        // 最终请求地址
        String finalUrl = params.length() == 0 ? url : url + "?" + params;

        // 考虑是否包装上传进度
        if (onNetworkProgressListener != null) {
            body = new ProgressRequestBody(body , onNetworkProgressListener);
        }

        builder.url(finalUrl).post(body);
    }

    @Override
    protected String onAsyncSuccess(ResponseBody body) throws IOException {
        return body.string();
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

    @Override
    public void setNetworkProgressListener(OnNetworkProgressListener onNetworkProgressListener) {
        this.onNetworkProgressListener = onNetworkProgressListener;
    }
}
