package org.cwk.android.library.global;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.cwk.android.library.BuildConfig;
import org.cwk.android.library.R;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * 全局对象，用于在任意位置使用应用程序资源
 *
 * @author 超悟空
 * @version 4.0 2017/11/13
 * @since 1.0
 */
public class Global {

    /**
     * 日志标签前缀
     */
    private static final String TAG = "Global";

    /**
     * 用户代理
     */
    private static final String USER_AGENT_HEADER_NAME = "User-Agent";

    /**
     * 自身静态全局实例
     */
    private static Global global = null;

    /**
     * Application实例
     */
    private Application application = null;

    /**
     * 全局网络请求工具
     */
    private OkHttpClient okHttpClient = null;

    /**
     * 全局UI线程Handler
     */
    private Handler handler = null;

    /**
     * 获取应用的Application对象
     *
     * @return 返回Application对象
     */
    public static Context getApplication() {
        return global.application;
    }

    /**
     * 获取全局网络连接对象
     *
     * @return 带默认设置的OkHttpClient对象
     */
    public static OkHttpClient getOkHttpClient() {
        return global.okHttpClient;
    }

    /**
     * 获取全局UI线程Handler
     *
     * @return UI线程Handler
     */
    public static Handler getUiHandler() {
        return global.handler;
    }

    /**
     * 设置网络工具
     *
     * @param okHttpClient 带默认设置的OkHttpClient对象
     */
    public static void setOkHttpClient(OkHttpClient okHttpClient) {
        global.okHttpClient = okHttpClient;
    }

    /**
     * 初始化
     *
     * @param application 应用Application
     */
    public static void init(Application application) {
        global = new Global(application);
    }

    private Global(Application application) {
        this.application = application;
        handler = new Handler(Looper.getMainLooper());

        initOkHttpClient();
    }

    /**
     * 初始化网络工具
     */
    private void initOkHttpClient() {
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
        userAgentBuilder.append("/");

        try {
            // 包信息
            PackageInfo info = application.getPackageManager().getPackageInfo(application
                    .getPackageName(), 0);
            // 应用id
            userAgentBuilder.append(application.getPackageName());
            userAgentBuilder.append("/");

            // 应用版本
            userAgentBuilder.append(info.versionCode);
            userAgentBuilder.append("(");
            userAgentBuilder.append(info.versionName);
            userAgentBuilder.append(")");
            userAgentBuilder.append(".");

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "initOkHttpClient PackageManager error", e);
        }

        okHttpClient = new OkHttpClient.Builder()
                // 设置默认连接超时时间
                .connectTimeout(application.getResources().getInteger(R.integer
                        .http_default_connect_timeout), TimeUnit.MILLISECONDS)
                // 设置默认读取超时时间
                .readTimeout(application.getResources().getInteger(R.integer
                        .http_default_read_timeout), TimeUnit.MILLISECONDS)
                // 设置默认写入超时时间
                .writeTimeout(application.getResources().getInteger(R.integer
                        .http_default_write_timeout), TimeUnit.MILLISECONDS)
                // 设置用户代理信息拦截器
                .addNetworkInterceptor(chain -> {
                    final Request originalRequest = chain.request();
                    final Request requestWithUserAgent = originalRequest.newBuilder()
                            .removeHeader(USER_AGENT_HEADER_NAME).addHeader
                                    (USER_AGENT_HEADER_NAME, userAgentBuilder.toString()).build();
                    return chain.proceed(requestWithUserAgent);
                }).build();
    }
}
