package org.cwk.android.library.model.operate;

/**
 * 取消接口
 *
 * @author 超悟空
 * @version 1.0 2015/10/29
 * @since 1.0
 */
public interface Cancelable {

    /**
     * 取消正在执行的任务
     */
    void cancel();

    /**
     * 判断任务是否被取消
     *
     * @return true表示执行了{@link #cancel()}，false表示未执行{@link #cancel()}
     */
    boolean isCanceled();
}
