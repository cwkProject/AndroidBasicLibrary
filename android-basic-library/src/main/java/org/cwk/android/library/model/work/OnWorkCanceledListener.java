package org.cwk.android.library.model.work;

/**
 * 任务被取消的回调接口
 *
 * @param <Parameters> 任务传入参数类型
 *
 * @author 超悟空
 * @version 1.0 2017/4/14
 * @since 1.0 2017/4/14
 **/
public interface OnWorkCanceledListener<Parameters> {
    /**
     * 任务结束回调方法，
     * 在任务函数执行结束后被调用
     *
     * @param parameters 任务传入参数
     */
    @SuppressWarnings("unchecked")
    void onCanceled(Parameters... parameters);
}
