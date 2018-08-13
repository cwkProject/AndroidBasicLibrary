package org.cwk.android.library.global;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

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
    }
}
