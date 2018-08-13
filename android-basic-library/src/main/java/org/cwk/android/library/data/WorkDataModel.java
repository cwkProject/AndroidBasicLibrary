package org.cwk.android.library.data;

import android.support.annotation.NonNull;
import android.util.Log;

import org.cwk.android.library.util.LogUtil;
import org.cwk.android.library.work.WorkModel;

/**
 * 任务{@link WorkModel}使用的数据模型，所有任务使用的定制数据类必须从此类继承
 *
 * @param <Request>    要序列化的目标类型
 * @param <Response>   要解析的结果数据类型
 * @param <Handle>     二次处理的结果数据类型，如jsonObject类型，方便解析数据
 * @param <Parameters> 任务传入参数类型
 * @param <Result>     任务返回结果类型
 *
 * @author 超悟空
 * @version 1.0 2018/7/21
 * @since 1.0
 */
public abstract class WorkDataModel<Request, Response, Handle, Parameters, Result> implements
        IDataModel<Parameters, Result> {

    /**
     * 跟踪日志
     */
    protected final String logTag;

    /**
     * 任务传入的参数
     */
    private Parameters[] parameters = null;

    /**
     * 响应码
     */
    private int responseCode = 0;

    /**
     * 标识本次服务请求是否成功
     */
    private boolean success = false;

    /**
     * 本次服务请求返回的结果字符串
     */
    private String message = null;

    /**
     * 任务传出的结果
     */
    private Result result = null;

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public WorkDataModel(String tag) {
        this.logTag = tag;
    }

    @Override
    public final Result getResult() {
        return result;
    }

    @Override
    public final boolean isSuccess() {
        return success;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    @Override
    public final int getCode() {
        return responseCode;
    }

    @Override
    public final Parameters[] getParams() {
        return parameters;
    }

    /**
     * 设置任务参数
     *
     * @param parameters 任务参数
     */
    final void setParams(Parameters[] parameters) {
        this.parameters = parameters;
    }

    /**
     * 序列化要提交的数据,用于与服务器交互
     *
     * @return 序列化的数据用于网络框架提交
     */
    final Request serialization() {
        Log.v(logTag , "serialization start");
        Request request = onSerialization(parameters);
        Log.v(logTag , "serialization end");
        return request;
    }

    /**
     * 解析传回的数据，只有当网络请求成功后才会执行
     *
     * @param response 要解析的数据
     *
     * @return 解析执行结果
     */
    final boolean parse(Response response) {
        Log.v(logTag , "parse start");
        LogUtil.v(logTag , "parse response:" + response);
        if (!onCheckResponse(response)) {
            // 通信异常
            Log.d(logTag , "parse response error onParseFailed invoked");
            onParseFailed();
            return false;
        }

        try {
            // 将结果转换为Handle对象
            Log.v(logTag , "parse onCreateHandle invoked");
            Handle handle = onCreateHandle(response);

            // 提取服务执行结果
            this.success = onRequestResult(handle);
            Log.v(logTag , "parse request result:" + this.success);

            if (this.success) {
                // 服务请求成功回调
                Log.v(logTag , "parse onRequestSuccess invoked");
                this.result = onRequestSuccess(handle);
                // 提取服务返回的消息
                this.message = onRequestSuccessMessage(handle);
            } else {
                // 服务请求失败回调
                Log.v(logTag , "parse onRequestFailed invoked");
                this.result = onRequestFailed(handle);
                // 提取服务返回的消息
                this.message = onRequestFailedMessage(handle);
            }
            Log.v(logTag , "parse request message:" + this.message);

            return true;
        } catch (Exception e) {
            Log.e(logTag , "parse error onParseFailed invoked" , e);
            onParseFailed();
            return false;
        } finally {
            Log.v(logTag , "parse end");
        }
    }

    /**
     * 从外部设置结果消息，通常用于设置网络请求失败时的消息和覆盖自解析生成的消息
     *
     * @param message 消息内容
     */
    final void setMessage(String message) {
        this.message = message;
        Log.v(logTag , "setMessage message:" + this.message);
    }

    /**
     * 从外部设置http响应码
     *
     * @param code http响应码
     */
    final void setResponseCode(int code) {
        this.responseCode = code;
    }

    /**
     * 序列化要提交的数据,用于与服务器交互
     *
     * @param parameters 任务传入的参数集合
     *
     * @return 序列化的数据用于网络框架提交
     */
    protected abstract Request onSerialization(Parameters[] parameters);

    /**
     * 检测响应结果是否符合预期，也可以做验签
     *
     * @param response 响应数据
     *
     * @return 检测结果
     */
    protected abstract boolean onCheckResponse(Response response);

    /**
     * 解析失败时调用，即{@link #parse}出现异常时调用
     */
    protected void onParseFailed() {
    }

    /**
     * 对响应结果数据进行二次处理
     *
     * @param response 响应结果数据
     *
     * @return 处理后的可操作对象
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    @SuppressWarnings("NullableProblems")
    @NonNull
    protected abstract Handle onCreateHandle(Response response) throws Exception;

    /**
     * 提取服务执行结果
     *
     * @param handleResult 二次处理结果集
     *
     * @return 服务请求结果，true表示请求成功，false表示请求失败
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected abstract boolean onRequestResult(Handle handleResult) throws Exception;

    /**
     * 提取或设置服务返回的失败结果消息<br>
     * 在{@link #onRequestFailed(Object)}之后被调<br>
     * 且服务器返回的执行结果为失败{@link #isSuccess()}为false
     *
     * @param handleResult 二次处理结果集
     *
     * @return 消息字符串
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected abstract String onRequestFailedMessage(Handle handleResult) throws Exception;

    /**
     * 提取或设置服务返回的成功结果消息<br>
     * 在{@link #onRequestSuccess(Object)}之后被调<br>
     * 且服务器返回的执行结果为成功{@link #isSuccess()}为true
     *
     * @param handleResult 二次处理结果集
     *
     * @return 消息字符串，默认为null
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected String onRequestSuccessMessage(Handle handleResult) throws Exception {
        return null;
    }

    /**
     * 提取服务反馈的结果数据<br>
     * 在服务请求成功后调用，
     * 用于生成请求成功后的任务返回结果数据
     * 即{@link #onRequestResult(Object)}返回值为true时被调用
     *
     * @param handleResult 二次处理结果集
     *
     * @return 任务返回结果数据
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected abstract Result onRequestSuccess(Handle handleResult) throws Exception;

    /**
     * 提取服务反馈的结果数据<br>
     * 在服务请求失败后调用，
     * 用于处理错误并返回失败时的默认结果数据
     * 即{@link #onRequestResult(Object)}返回值为false时被调用
     *
     * @param handleResult 二次处理结果集
     *
     * @return 失败时的任务返回结果数据，默认为null
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected Result onRequestFailed(Handle handleResult) throws Exception {
        return null;
    }
}
