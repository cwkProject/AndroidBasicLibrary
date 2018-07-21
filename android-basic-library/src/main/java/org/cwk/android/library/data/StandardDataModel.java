package org.cwk.android.library.data;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * 规范的数据模型结构
 *
 * @param <Value>    要序列化的Map值类型
 * @param <Handle>   二次处理的结果数据类型
 * @param <Response> 要解析的结果数据类型
 *
 * @author 超悟空
 * @version 2.0 2016/3/19
 * @since 1.0
 */
public abstract class StandardDataModel<Value, Response, Handle, Parameters, Result> extends
        WorkDataModel<Map<String, Value>, Response, Handle, Parameters, Result> {

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public StandardDataModel(String tag) {
        super(tag);
    }

    @Override
    protected Map<String, Value> onSerialization(Parameters[] parameters) {
        // 序列化后的参数集
        Map<String, Value> dataMap = new HashMap<>();
        Log.v(logTag , "onSerialization onFillRequestParameters invoked");
        // 调用填充方法
        onFillRequestParameters(dataMap , parameters);

        // 对参数进行签名
        Log.v(logTag , "onSerialization onRequestParametersSign invoked");
        onRequestParametersSign(dataMap);
        return dataMap;
    }

    /**
     * 填充服务请求所需的参数，
     * 即设置{@link #onSerialization(Object[])}返回值
     *
     * @param dataMap 参数数据集<参数名,参数值>
     */
    @SuppressWarnings("unchecked")
    protected abstract void onFillRequestParameters(Map<String, Value> dataMap , Parameters...
            parameters);

    /**
     * 对参数进行签名
     *
     * @param dataMap 要发送的数据
     */
    protected void onRequestParametersSign(Map<String, Value> dataMap) {

    }
}
