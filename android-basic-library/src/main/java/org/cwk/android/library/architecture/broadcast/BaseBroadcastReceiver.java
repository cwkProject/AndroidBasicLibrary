package org.cwk.android.library.architecture.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

/**
 * 广播接收者基类
 *
 * @author 超悟空
 * @version 1.0 2017/2/22
 * @since 1.0 2017/2/22
 **/
public abstract class BaseBroadcastReceiver extends BroadcastReceiver {

    /**
     * 注册本地广播接收者（使用{@link LocalBroadcastManager}注册）
     *
     * @param context 上下文
     */
    public final void registerLocalReceiver(Context context) {
        LocalBroadcastManager.getInstance(context);

        // 注册
        LocalBroadcastManager.getInstance(context).registerReceiver(this, getRegisterIntentFilter
                ());
    }

    /**
     * 注销本地广播接收者
     *
     * @param context 上下文
     */
    public final void unregisterLocalReceiver(Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this);
    }

    /**
     * 得到本接收者监听的动作集合
     *
     * @return 填充完毕的意图集合
     */
    public final IntentFilter getRegisterIntentFilter() {
        // 新建动作集合
        IntentFilter filter = new IntentFilter();
        onRegisterIntentFilter(filter);
        return filter;
    }

    /**
     * 注册本接受者监听的动作集合
     *
     * @param filter 要填充的意图集合
     */
    protected abstract void onRegisterIntentFilter(@NonNull IntentFilter filter);
}
