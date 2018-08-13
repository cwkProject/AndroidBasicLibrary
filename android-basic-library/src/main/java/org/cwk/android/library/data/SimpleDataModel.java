package org.cwk.android.library.data;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import org.json.JSONObject;

/**
 * 根据现有网络协议简化的任务数据模型基类
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public abstract class SimpleDataModel<Parameters, Result> extends JsonDataModel<Parameters,
        Result> {
    /**
     * 服务响应的业务数据的参数默认取值标签
     */
    protected static final String RESULT = "result";

    /**
     * 服务响应的errorCode，0为默认值，大于0为错误代码
     */
    private int errorCode = 0;

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public SimpleDataModel(String tag) {
        super(tag);
    }

    @Override
    protected boolean onRequestResult(@NonNull JSONObject handleResult) throws Exception {
        // 得到执行结果
        return handleResult.getBoolean("state");
    }

    @Override
    protected final Result onRequestSuccess(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.isNull(RESULT) ? onDefaultData() : onExtractData(handleResult);
    }

    @Override
    @CallSuper
    protected Result onRequestFailed(@NonNull JSONObject handleResult) throws Exception {
        if (!handleResult.isNull("errorCode")) {
            errorCode = handleResult.getInt("errorCode");
        }

        return super.onRequestFailed(handleResult);
    }

    /**
     * 当请求成功且返回结果中存在{@link #RESULT}标签的数据时被调用，
     * 即{@link #RESULT}不为null时此方法用于提取装配结果数据
     *
     * @param handleResult 响应的完整数据结果(包含{@link #RESULT})
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    @SuppressWarnings("NullableProblems")
    protected abstract Result onExtractData(@NonNull JSONObject handleResult) throws Exception;

    /**
     * 当请求成功且返回结果不存在{@link #RESULT}标签的数据时被调用，
     * 即{@link #RESULT}为null时此方法用于装配默认结果数据，默认实现为null
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    protected Result onDefaultData() throws Exception {
        return null;
    }

    @Override
    protected String onRequestFailedMessage(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.optString("message");
    }

    @Override
    protected String onRequestSuccessMessage(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.optString("message");
    }

    /**
     * 获取服务响应的errorCode，0为默认值，大于0为错误代码
     *
     * @return 成功时为0，错误时大于0
     */
    public int getErrorCode() {
        return errorCode;
    }
}
