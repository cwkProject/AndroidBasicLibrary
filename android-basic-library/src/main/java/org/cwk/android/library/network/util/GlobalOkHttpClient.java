package org.cwk.android.library.network.util;

import android.os.Build;
import android.support.annotation.NonNull;

import org.cwk.android.library.BuildConfig;
import org.cwk.android.library.global.ApplicationStaticValue;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 任务模型网络请求实现类使用的全局OkHttpClient对象管理器，
 * 在这里可以获取和自定义任务模型使用的OkHttpClient对象
 *
 * @author 超悟空
 * @version 1.0 2018/7/21
 * @since 1.0
 */
public class GlobalOkHttpClient {

    /**
     * http头用户代理字段，有默认的定义
     */
    public static final String USER_AGENT_HEADER_NAME = "User-Agent";

    /**
     * 全局网络请求工具
     */
    private static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient.Builder()
                // 设置默认读取超时时间
                .readTimeout(30 , TimeUnit.SECONDS)
                // 设置默认写入超时时间
                .writeTimeout(30 , TimeUnit.SECONDS).build();

    }

    /**
     * 获取默认的用户代理内容
     *
     * @return 默认的用户代理内容
     */
    public static String getUserAgent() {
        // 网络请求用户代理字符串
        final StringBuilder userAgentBuilder = new StringBuilder();

        userAgentBuilder.append(ApplicationStaticValue.AppConfig.DEVICE_TYPE);
        userAgentBuilder.append("/");
        // 制造商
        userAgentBuilder.append(Build.BRAND);
        userAgentBuilder.append("/");
        // 设备型号
        userAgentBuilder.append(Build.MODEL);
        userAgentBuilder.append("(");
        userAgentBuilder.append(Build.ID);
        userAgentBuilder.append(")");
        userAgentBuilder.append("/");

        // 系统版本
        userAgentBuilder.append(Build.VERSION.SDK_INT);
        userAgentBuilder.append("(");
        userAgentBuilder.append(Build.VERSION.RELEASE);
        userAgentBuilder.append(")");
        userAgentBuilder.append("/");

        // 框架版本
        userAgentBuilder.append(BuildConfig.VERSION_CODE);
        userAgentBuilder.append("(");
        userAgentBuilder.append(BuildConfig.VERSION_NAME);
        userAgentBuilder.append(")");
        userAgentBuilder.append(".");

        return userAgentBuilder.toString();
    }

    /**
     * 获取全局网络连接对象
     *
     * @return 带默认设置的OkHttpClient对象
     */
    @NonNull
    public static OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    /**
     * 设置网络工具
     *
     * @param okHttpClient 带默认设置的OkHttpClient对象
     */
    public static void setOkHttpClient(@NonNull OkHttpClient okHttpClient) {
        GlobalOkHttpClient.okHttpClient = okHttpClient;
    }
}
