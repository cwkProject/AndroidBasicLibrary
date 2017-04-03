package org.cwk.android.library.model.data;

import org.cwk.android.library.model.operate.CreateObservable;

/**
 * 任务模型{@link CreateObservable#createObservable(Object[])}的通知数据，包含任务执行结果和响应数据
 *
 * @author 超悟空
 * @version 1.0 2017/4/3
 * @since 1.0
 */
public class WorkResult<Result> {

    /**
     * 任务执行是否成功
     */
    private boolean success = false;

    /**
     * 任务响应消息
     */
    private String message = null;

    /**
     * 任务响应数据
     */
    private Result result = null;

    /**
     * 构造函数
     *
     * @param success 执行结果
     * @param message 响应消息
     * @param result  响应数据
     */
    public WorkResult(boolean success, String message, Result result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    /**
     * 任务是否成功
     *
     * @return true表示成功
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * 任务响应消息
     *
     * @return 消息字符串，可为null
     */
    public String getMessage() {
        return message;
    }

    /**
     * 任务响应数据
     *
     * @return 数据对象
     */
    public Result getResult() {
        return result;
    }
}
