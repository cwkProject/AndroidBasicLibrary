package org.cwk.android.library.architecture.broadcast;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

/**
 * 带有生命周期感知的广播接收器
 *
 * @author 超悟空
 * @version 1.0 2018/6/26
 * @since 1.0
 */
public abstract class LifecycleBroadcastReceiver extends BaseBroadcastReceiver implements
        LifecycleObserver {

    /**
     * 是否同时在全局范围注册接收，即接收系统事件和其他进程事件，默认为false
     */
    private boolean globalRegister = false;

    /**
     * Android 上下文
     */
    private Context context = null;

    @Override
    public abstract void onReceive(@NonNull Context context , @NonNull Intent intent);

    /**
     * 设置为同时在全局范围注册接收，即接收系统事件和其他进程事件
     *
     * @return 本接收者实例
     */
    public LifecycleBroadcastReceiver globalRegister() {
        this.globalRegister = true;
        return this;
    }

    /**
     * 注册接收者
     *
     * @param context        Android 上下文
     * @param lifecycle      生命周期
     * @param globalRegister 设置是否同时在全局范围注册接收，即接收系统事件和其他进程事件，默认为false
     */
    public void register(Context context , Lifecycle lifecycle , boolean globalRegister) {
        this.globalRegister = globalRegister;
        this.context = context;

        registerLocalReceiver(context);

        if (globalRegister) {
            context.registerReceiver(this , getRegisterIntentFilter());
        }

        lifecycle.addObserver(this);
    }

    /**
     * 注册接收者
     *
     * @param context   Android 上下文
     * @param lifecycle 生命周期
     */
    public void register(Context context , Lifecycle lifecycle) {
        register(context , lifecycle , false);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        unregisterLocalReceiver(context);
        if (globalRegister) {
            context.unregisterReceiver(this);
        }
    }
}
