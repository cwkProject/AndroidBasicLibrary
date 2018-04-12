package org.cwk.android.library.data.base;

import android.support.annotation.NonNull;
import android.util.Log;

import org.cwk.android.library.data.IDefaultDataModel;

import java.util.HashMap;
import java.util.Map;

/**
 * 规范的数据模型结构
 *
 * @param <Handle>   二次处理的结果数据类型
 * @param <Response> 要解析的结果数据类型
 * @param <Value>    要序列化的Map值类型
 *
 * @author 超悟空
 * @version 2.0 2016/3/19
 * @since 1.0
 */
public abstract class StandardDataModel<Handle, Response, Value> implements
        IDefaultDataModel<Response, Map<String, Value>> {

    /**
     * 日志前缀
     */
    private static final String TAG = "StandardDataModel#";

    /**
     * 跟踪日志
     */
    protected final String logTag;

    /**
     * 标识本次服务请求是否成功
     */
    private boolean success = false;

    /**
     * 本次服务请求返回的结果字符串
     */
    private String message = null;

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public StandardDataModel(String tag) {
        this.logTag = tag;
    }

    /**
     * 判断本次服务请求是否成功
     *
     * @return true表示成功，false表示失败
     */
    @Override
    public final boolean isSuccess() {
        return success;
    }

    @Override
    public final String getMessage() {
        return message;
    }

    /**
     * 设置服务请求结果消息
     *
     * @param message 消息字符串
     */
    protected final void setMessage(String message) {
        this.message = message;
    }

    @Override
    public final Map<String, Value> serialization() {
        Log.v(logTag, TAG + "serialization start");
        // 序列化后的参数集
        Map<String, Value> dataMap = new HashMap<>();
        Log.v(logTag, TAG + "onFillRequestParameters invoked");
        // 调用填充方法
        onFillRequestParameters(dataMap);

        // 对参数进行签名
        Log.v(logTag, TAG + "onRequestParametersSign invoked");
        onRequestParametersSign(dataMap);
        Log.v(logTag, TAG + "serialization end");
        return dataMap;
    }

    /**
     * 填充服务请求所需的参数，
     * 即设置{@link #serialization()}返回值
     *
     * @param dataMap 参数数据集<参数名,参数值>
     */
    protected abstract void onFillRequestParameters(Map<String, Value> dataMap);

    @Override
    public final boolean parse(Response response) {
        Log.v(logTag, TAG + "parse start");
        Log.v(logTag, TAG + "response " + response);
        if (!onCheckResponse(response)) {
            // 通信异常
            Log.d(logTag, TAG + "response error");
            Log.v(logTag, TAG + "onParseFailed invoked");
            onParseFailed();
            return false;
        }

        try {
            // 将结果转换为Handle对象
            Log.v(logTag, TAG + "onCreateHandle invoked");
            Handle handle = onCreateHandle(response);

            Log.v(logTag, TAG + "onRequestResult invoked");
            // 提取服务执行结果
            this.success = onRequestResult(handle);
            Log.v(logTag, TAG + "request result is " + this.success);

            Log.v(logTag, TAG + "onRequestMessage invoked");
            // 提取服务返回的消息
            this.message = onRequestMessage(this.success, handle);
            Log.v(logTag, TAG + "request message is " + this.message);

            if (this.success) {
                // 服务请求成功回调
                Log.v(logTag, TAG + "onRequestSuccess invoked");
                onRequestSuccess(handle);
            } else {
                // 服务请求失败回调
                Log.v(logTag, TAG + "onRequestFailed invoked");
                onRequestFailed(handle);
            }

            return true;
        } catch (Exception e) {
            Log.e(logTag, TAG + "parse error", e);
            Log.v(logTag, TAG + "onParseFailed invoked");
            onParseFailed();
            return false;
        } finally {
            Log.v(logTag, TAG + "parse end");
        }
    }

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
     * 提取服务返回的结果消息<br>
     * 在{@link #onRequestResult(Object)}之后被调用
     *
     * @param result       服务请求执行结果，
     *                     即{@link #onRequestResult(Object)}返回值
     * @param handleResult 二次处理结果集
     *
     * @return 消息字符串
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected abstract String onRequestMessage(boolean result, Handle handleResult) throws
            Exception;

    /**
     * 提取服务反馈的结果数据<br>
     * 在服务请求成功后调用，
     * 即{@link #onRequestResult(Object)}返回值为true时，
     * 在{@link #onRequestMessage(boolean , Object)}之后被调用，
     *
     * @param handleResult 二次处理结果集
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected abstract void onRequestSuccess(Handle handleResult) throws Exception;

    /**
     * 提取服务反馈的结果数据<br>
     * 在服务请求失败后调用，
     * 即{@link #onRequestResult(Object)}返回值为false时，
     * 在{@link #onRequestMessage(boolean , Object)}之后被调用，
     *
     * @param handleResult 二次处理结果集
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected void onRequestFailed(Handle handleResult) throws Exception {

    }

    /**
     * 对参数进行签名
     *
     * @param dataMap 要发送的数据
     */
    protected void onRequestParametersSign(Map<String, Value> dataMap) {

    }
}
