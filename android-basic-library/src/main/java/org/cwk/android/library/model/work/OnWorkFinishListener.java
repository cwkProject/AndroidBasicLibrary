package org.cwk.android.library.model.work;
/**
 * Created by 超悟空 on 2015/10/16.
 */

/**
 * 任务结束回调接口
 *
 * @author 超悟空
 * @version 1.0 2015/10/16
 * @since 1.0
 */
public interface OnWorkFinishListener<Result> {

    /**
     * 任务结束回调方法，
     * 在任务函数执行结束后被调用
     *
     * @param state   任务执行结果
     * @param data    结果数据
     * @param message 结果消息
     */
    void onFinish(boolean state, Result data, String message);
}
