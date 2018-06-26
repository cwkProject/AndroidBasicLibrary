package org.cwk.android.library.data;

import android.support.annotation.NonNull;

import java.util.Map;

/**
 * 集成化数据模型基类
 *
 * @param <Parameters> 任务传入参数类型
 * @param <Result>     任务返回结果类型
 * @param <Handle>     二次处理的结果数据类型
 * @param <Response>   要解析的结果数据类型
 * @param <Value>      要序列化的Map值类型
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public abstract class IntegratedDataModel<Parameters, Result, Handle, Response, Value> extends
        StandardDataModel<Handle, Response, Value> implements IIntegratedDataModel<Parameters,
        Result, Response, Map<String, Value>> {

    /**
     * 任务传入的参数
     */
    private Parameters[] parameters = null;

    /**
     * 任务传出的结果
     */
    private Result result = null;

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public IntegratedDataModel(String tag) {
        super(tag);
    }

    @SafeVarargs
    @Override
    public final void setParameters(Parameters... parameters) {
        this.parameters = parameters;
    }

    @Override
    public Result getResult() {
        return result;
    }

    @Override
    protected final void onFillRequestParameters(@NonNull Map<String, Value> dataMap) {
        onFillRequestParameters(dataMap, parameters);
    }

    /**
     * 填充服务请求所需的参数
     *
     * @param dataMap    将要填充的参数数据集<参数名,参数值>
     * @param parameters 任务传入的参数
     */
    @SuppressWarnings({"unchecked" , "NullableProblems"})
    protected abstract void onFillRequestParameters(@NonNull Map<String, Value> dataMap, @NonNull
            Parameters... parameters);

    @Override
    protected final void onRequestSuccess(Handle handleResult) throws Exception {
        result = onSuccessResult(handleResult);
    }

    /**
     * 提取服务反馈的结果数据<br>
     * 在服务请求成功后调用，
     * 即{@link #onRequestResult(Object)}返回值为true时，
     * 在{@link #onRequestMessage(boolean , Object)}之后被调用，
     *
     * @param handleResult 二次处理结果集
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    @SuppressWarnings("NullableProblems")
    protected abstract Result onSuccessResult(@NonNull Handle handleResult) throws Exception;
}
