package org.cwk.android.library.network.util;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 网络请求重试拦截器
 *
 * @author 超悟空
 * @version 1.0 2018/1/11
 * @since 1.0 2018/1/11
 **/
public class RetryInterceptor implements Interceptor {

    /**
     * 跟踪日志
     */
    private final String logTag;

    /**
     * 最大重试次数
     */
    private int maxRetryTimes = 0;

    /**
     * 构造函数
     *
     * @param tag   标签，用于跟踪日志
     * @param times 重试次数
     */
    public RetryInterceptor(String tag, int times) {
        this.logTag = tag;
        this.maxRetryTimes = times;
    }

    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        int tryCount = 0;
        boolean isSuccessful = false;
        Response response = null;

        do {
            try {
                tryCount++;
                Log.v(logTag, "request try:" + tryCount);

                response = chain.proceed(request);
                isSuccessful = response.isSuccessful();
            } catch (IOException e) {
                Log.e(logTag, "request try:" + tryCount + "," + e.toString());

                if (tryCount > maxRetryTimes) {
                    throw e;
                }
            }
        } while (!isSuccessful && tryCount <= maxRetryTimes);

        return response;
    }
}
