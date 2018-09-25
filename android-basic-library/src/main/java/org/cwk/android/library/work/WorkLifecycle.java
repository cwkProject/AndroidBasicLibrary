package org.cwk.android.library.work;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;

import java.lang.ref.WeakReference;

/**
 * 用于管理{@link WorkModel}在UI控制器生命周期的处理，目前仅用于UI控制器销毁时安全取消
 *
 * @author 超悟空
 * @version 1.0 2018/8/31
 * @since 1.0
 */
class WorkLifecycle implements LifecycleObserver {

    /**
     * 是否执行一次任务后自动解除生命周期绑定，默认为true
     */
    boolean isOnce = true;

    /**
     * 生命周期对象
     */
    private final WeakReference<Lifecycle> lifecycle;

    /**
     * 可取消任务
     */
    private final WeakReference<Cancelable> cancelable;

    /**
     * 是否已被销毁
     */
    private boolean isDestroy = false;

    /**
     * 注册生命周期
     *
     * @param lifecycle  生命周期对象
     * @param cancelable 可取消任务
     */
    WorkLifecycle(Lifecycle lifecycle , Cancelable cancelable) {
        this.lifecycle = new WeakReference<>(lifecycle);
        this.cancelable = new WeakReference<>(cancelable);

        lifecycle.addObserver(this);
    }

    /**
     * 注销生命周期
     */
    void unregister() {
        if (!isDestroy) {
            WorkModel.MAIN_HANDLER.post(() -> {
                Lifecycle lifecycle = this.lifecycle.get();

                if (lifecycle != null) {
                    lifecycle.removeObserver(this);
                }

                this.lifecycle.clear();
                this.cancelable.clear();
            });
        }
    }

    /**
     * UI控制器销毁
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private void onDestroy() {
        isDestroy = true;
        Cancelable cancelable = this.cancelable.get();

        if (cancelable != null) {
            cancelable.cancel();
        }

        this.lifecycle.clear();
        this.cancelable.clear();
    }
}
