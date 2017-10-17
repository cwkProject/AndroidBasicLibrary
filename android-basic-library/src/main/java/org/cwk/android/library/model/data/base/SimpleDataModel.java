package org.cwk.android.library.model.data.base;

import android.support.annotation.CallSuper;

import org.json.JSONObject;

/**
 * 基于集成化任务架构，
 * 根据现有网络协议简化的数据模型基类
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public abstract class SimpleDataModel<Parameters, Result> extends
        IntegratedJsonDataModel<Parameters, Result> {
    /**
     * 服务响应的业务数据的参数默认取值标签
     */
    protected static final String RESULT = "result";

    /**
     * 服务响应的errorCode，0为默认值，大于0为错误代码
     */
    private int code = 0;

    @Override
    protected boolean onRequestResult(JSONObject handleResult) throws Exception {
        // 得到执行结果
        return handleResult.getBoolean("state");
    }

    @Override
    protected String onRequestMessage(boolean result, JSONObject handleResult) throws Exception {
        return handleResult.getString("message");
    }

    @Override
    protected Result onSuccessResult(JSONObject handleResult) throws Exception {
        if (!handleResult.isNull(RESULT)) {
            return onExtractData(handleResult);
        } else {
            return null;
        }
    }

    @Override
    @CallSuper
    protected void onRequestFailed(JSONObject handleResult) throws Exception {
        if (handleResult != null && !handleResult.isNull("errorCode")) {
            code = handleResult.getInt("errorCode");
        }
    }

    /**
     * 当请求成功且返回结果中存在{@link #RESULT}标签的数据时被调用，
     * 即{@link #RESULT}不为null时此方法用于提取装配结果数据
     *
     * @param jsonData 响应的完整数据结果(包含{@link #RESULT})
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    protected abstract Result onExtractData(JSONObject jsonData) throws Exception;

    /**
     * 获取服务响应的errorCode，0为默认值，大于0为错误代码
     *
     * @return 成功时为0，错误时大于0
     */
    public int getCode() {
        return code;
    }
}
