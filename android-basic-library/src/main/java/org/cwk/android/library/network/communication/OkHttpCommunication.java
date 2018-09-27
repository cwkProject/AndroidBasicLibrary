package org.cwk.android.library.network.communication;

import android.support.annotation.NonNull;
import android.util.Log;

import org.cwk.android.library.network.util.GlobalOkHttpClient;
import org.cwk.android.library.network.util.NetworkCallback;
import org.cwk.android.library.network.util.NetworkTimeout;
import org.cwk.android.library.network.util.RetryInterceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 基于OkHttp的网络请求基类
 *
 * @author 超悟空
 * @version 2.0 2017/11/13
 * @since 1.0
 */
public abstract class OkHttpCommunication<RequestType, ResponseType> implements
        ICommunication<RequestType, ResponseType> {

    /**
     * 跟踪日志
     */
    protected final String logTag;

    /**
     * 超时时间配置
     */
    protected NetworkTimeout networkTimeout = null;

    /**
     * 请求地址的完整路径
     */
    protected String url = null;

    /**
     * 请求数据编码，默认使用UTF-8
     */
    protected String encoded = null;

    /**
     * 请求头信息
     */
    private Headers headers = null;

    /**
     * 一个请求对象
     */
    protected Call call = null;

    /**
     * 要返回的响应体
     */
    protected ResponseBody response = null;

    /**
     * 标识请求是否成功
     */
    protected boolean success = false;

    /**
     * 响应码
     */
    protected int code = 0;

    /**
     * 请求重试次数
     */
    protected int retryTimes = 0;

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    protected OkHttpCommunication(String tag) {
        this.logTag = tag;
    }

    @Override
    public void setNetworkTimeout(NetworkTimeout networkTimeout) {
        this.networkTimeout = networkTimeout;
    }

    /**
     * 设置编码格式
     *
     * @param encoded 编码字符串，默认为UTF-8
     */
    public void setEncoded(String encoded) {
        Log.v(logTag , "encoded:" + encoded);
        this.encoded = encoded;
    }

    /**
     * 设置请求头信息
     *
     * @param headers 头信息
     */
    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    /**
     * 设置请求地址
     *
     * @param url 完整地址
     */
    @Override
    public void setTaskName(String url) {
        this.url = url;
        Log.v(logTag , "url:" + url);
    }

    @Override
    public void setRetryTimes(int times) {
        this.retryTimes = times;
        Log.v(logTag , "retryTimes:" + times);
    }

    @Override
    public void request(RequestType sendData) {
        Log.v(logTag , "request start");

        this.success = false;
        response = null;

        if (url == null || (!url.trim().toLowerCase().startsWith("http://") && !url.trim()
                .toLowerCase().startsWith("https://"))) {
            // 地址不合法
            Log.d(logTag , "url is error");
            return;
        }

        // 进阶配置请求工具
        OkHttpClient okHttpClient = onConfigOkHttpClient();

        // 创建请求
        Request.Builder builder = new Request.Builder();
        if (headers != null) {
            builder.headers(headers);
        }
        onCreateRequest(builder , sendData);
        Request request = builder.build();

        try {
            // 发起同步请求
            call = okHttpClient.newCall(request);
            Response response = call.execute();

            this.success = response.isSuccessful();
            this.code = response.code();
            String message = response.message();

            Log.v(logTag , "response code:" + code + "  message:" + message);

            if (response.isSuccessful()) {
                Log.v(logTag , "request success");
                this.response = response.body();
            } else {
                Log.v(logTag , "request failed");
                this.response = null;
            }
        } catch (IOException e) {
            Log.e(logTag , "call error" , e);

            this.success = false;
            response = null;
        }
    }

    /**
     * 进阶配置请求工具
     *
     * @return 请求工具
     */
    private OkHttpClient onConfigOkHttpClient() {
        // 得到okHttpClient对象
        OkHttpClient okHttpClient = GlobalOkHttpClient.getOkHttpClient();

        OkHttpClient.Builder builder = onRebuildClient(okHttpClient);

        // 判断是否需要设置超时
        if (networkTimeout != null) {

            if (builder == null) {
                builder = okHttpClient.newBuilder();
            }

            if (networkTimeout.getConnectTimeout() > -1) {
                builder.connectTimeout(networkTimeout.getConnectTimeout() , TimeUnit.MILLISECONDS);
            }

            if (networkTimeout.getReadTimeout() > -1) {
                builder.readTimeout(networkTimeout.getReadTimeout() , TimeUnit.MILLISECONDS);
            }

            if (networkTimeout.getWriteTimeout() > -1) {
                builder.writeTimeout(networkTimeout.getWriteTimeout() , TimeUnit.MILLISECONDS);
            }
        }

        // 判断是否需要设置重试
        if (retryTimes > 0) {
            if (builder == null) {
                builder = okHttpClient.newBuilder();
            }

            builder.addInterceptor(new RetryInterceptor(logTag , retryTimes));
        }

        // 尝试构建新配置的请求工具
        if (builder != null) {
            okHttpClient = builder.build();
        }
        return okHttpClient;
    }

    /**
     * 重新配置一个请求工具
     *
     * @param okHttpClient 原请求工具
     *
     * @return 新构建器，由父类完成构建
     */
    protected OkHttpClient.Builder onRebuildClient(OkHttpClient okHttpClient) {
        return null;
    }

    /**
     * 创建请求内容
     *
     * @param sendData 要发送的数据
     * @param builder  请求构造器，用于装填请求内容，无需调用build方法
     */
    protected abstract void onCreateRequest(Request.Builder builder , RequestType sendData);

    @Override
    public boolean isSuccessful() {
        return success;
    }

    @Override
    public int code() {
        return code;
    }

    @Override
    public void close() {
        if (response == null) {
            return;
        }

        response.close();
    }

    @Override
    public void request(RequestType sendData , final NetworkCallback<ResponseType> callback) {
        Log.v(logTag , "request start");

        if (url == null || (!url.trim().toLowerCase().startsWith("http://") && !url.trim()
                .toLowerCase().startsWith("https://"))) {
            // 地址不合法
            Log.d(logTag , "url is error");

            if (callback != null) {
                callback.onFinish(false , 0 , null);
            }

            return;
        }

        // 进阶配置请求工具
        OkHttpClient okHttpClient = onConfigOkHttpClient();

        // 创建请求
        Request.Builder builder = new Request.Builder();
        if (headers != null) {
            builder.headers(headers);
        }
        onCreateRequest(builder , sendData);
        Request request = builder.build();

        // 发送异步请求
        call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call , @NonNull IOException e) {
                Log.e(logTag , "call error" , e);

                if (callback != null) {
                    callback.onFinish(false , 0 , null);
                }
            }

            @Override
            public void onResponse(@NonNull Call call , @NonNull Response response) throws
                    IOException {
                code = response.code();
                success = response.isSuccessful();
                String message = response.message();

                Log.v(logTag , "response code:" + code + " message:" + message);

                if (callback != null) {
                    if (response.isSuccessful()) {
                        Log.v(logTag , "request success");

                        ResponseBody body = response.body();

                        // 处理结果
                        callback.onFinish(true , code , onAsyncSuccess(body));

                        // 关闭流
                        if (body != null) {
                            body.close();
                        }
                    } else {
                        Log.v(logTag , "request failed");
                        callback.onFinish(false , code , null);
                    }
                }
            }
        });
    }

    /**
     * 异步请求成功后的执行，用于结果处理
     *
     * @param body 响应体
     *
     * @return 正真需要的响应数据
     */
    protected abstract ResponseType onAsyncSuccess(ResponseBody body) throws IOException;

    @Override
    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public boolean isCanceled() {
        return call.isCanceled();
    }
}
