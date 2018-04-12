package org.cwk.android.library.data.base;

import org.json.JSONObject;
import org.cwk.android.library.global.ApplicationAttribute;
import org.cwk.android.library.util.RequestSign;

import java.util.Map;

/**
 * 集成化Json数据模型基类<br>
 * 解析响应结果为Json字符串的数据模型基类<br>
 * 请求参数为纯文本内容
 *
 * @param <Parameters> 任务传入参数类型
 * @param <Result>     任务返回结果类型
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public abstract class IntegratedJsonDataModel<Parameters, Result> extends
        IntegratedDataModel<Parameters, Result, JSONObject, String, String> {

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public IntegratedJsonDataModel(String tag) {
        super(tag);
    }

    @Override
    protected boolean onCheckResponse(String response) {
        return response != null;
    }

    @Override
    protected final JSONObject onCreateHandle(String response) throws Exception {
        return new JSONObject(response);
    }

    /**
     * 对参数进行签名，
     * 需要在应用启动时对环境变量赋值，
     * {@link ApplicationAttribute#create()}}
     *
     * @param dataMap 要发送的数据
     */
    @Override
    protected void onRequestParametersSign(Map<String, String> dataMap) {
        RequestSign.signForText(logTag, dataMap);
    }
}
