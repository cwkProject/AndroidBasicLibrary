package org.cwk.android.library.work;

import android.arch.lifecycle.LifecycleOwner;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;

/**
 * 可绑定UI控制器生命周期的任务，目前主要用于UI控制器销毁时的自动取消任务，以便尽可能安全结束任务
 *
 * @author 超悟空
 * @version 1.0 2018/8/31
 * @since 1.0
 */
public interface LifeCycleBindable {

    /**
     * 设置生命周期
     *
     * @param lifecycleOwner 生命周期拥有者
     */
    @MainThread
    LifeCycleBindable setLifecycleOwner(@NonNull LifecycleOwner lifecycleOwner);

    /**
     * 设置生命周期
     *
     * @param lifecycleOwner 生命周期拥有者
     * @param isOnce         是否执行一次任务后自动解除生命周期绑定，默认为true。
     *                       如果是一次性任务对象，建议设为true以便JVM快速回收任务对象内存。
     */
    @MainThread
    LifeCycleBindable setLifecycleOwner(@NonNull LifecycleOwner lifecycleOwner , boolean isOnce);
}
