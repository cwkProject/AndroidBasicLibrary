package org.cwk.android.library.global;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.cwk.android.library.BuildConfig;
import org.cwk.android.library.network.util.GlobalOkHttpClient;

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
     * 自身静态全局实例
     */
    private static Global global = null;

    /**
     * Application实例
     */
    private Application application = null;

    /**
     * 全局UI线程Handler
     */
    private Handler handler = null;

    /**
     * 获取应用的Application对象
     *
     * @return 返回Application对象
     */
    public static Application getApplication() {
        return global.application;
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
     * 初始化
     *
     * @param application 应用Application
     */
    public synchronized static void init(Application application) {
        if (global == null) {
            global = new Global(application);
        }
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
                    .getPackageName() , 0);
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
            Log.e(TAG , "initOkHttpClient PackageManager error" , e);
        }

        OkHttpClient.Builder builder = GlobalOkHttpClient.getOkHttpClient().newBuilder();

        builder.networkInterceptors().clear();

        // 设置用户代理信息拦截器
        builder.addNetworkInterceptor(chain -> {
            final Request originalRequest = chain.request();
            final Request requestWithUserAgent = originalRequest.newBuilder().removeHeader
                    (GlobalOkHttpClient.USER_AGENT_HEADER_NAME).addHeader(GlobalOkHttpClient
                    .USER_AGENT_HEADER_NAME , userAgentBuilder.toString()).build();
            return chain.proceed(requestWithUserAgent);
        });

        GlobalOkHttpClient.setOkHttpClient(builder.build());
    }
}
