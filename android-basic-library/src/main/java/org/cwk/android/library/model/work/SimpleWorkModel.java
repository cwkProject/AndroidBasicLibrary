package org.cwk.android.library.model.work;
/**
 * Created by 超悟空 on 2016/7/23.
 */


import org.json.JSONObject;
import org.cwk.android.library.model.data.IIntegratedDataModel;
import org.cwk.android.library.model.data.base.SimpleDataModel;

import java.util.Map;

/**
 * 极简的一体化集成式网络任务模型基类，
 * 内置{@link SimpleDataModel}的默认实现
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public abstract class SimpleWorkModel<Parameters, Result> extends IntegratedWorkModel<Parameters,
        Result> {

    /**
     * 服务响应的业务数据的参数默认取值标签
     */
    protected static final String RESULT = "result";

    @Override
    protected IIntegratedDataModel<Parameters, Result, ?, ?> onCreateDataModel() {
        return new SimpleDataModel<Parameters, Result>() {
            @Override
            protected Result onExtractData(JSONObject jsonResult) throws Exception {
                return onSuccessExtract(jsonResult);
            }

            @SafeVarargs
            @Override
            protected final void onFillRequestParameters(Map<String, String> dataMap,
                                                         Parameters... parameters) {
                onFill(dataMap, parameters);
            }
        };
    }

    /**
     * 填充服务请求所需的参数
     *
     * @param dataMap    将要填充的参数数据集<参数名,参数值>
     * @param parameters 任务传入的参数
     */
    @SuppressWarnings("unchecked")
    protected abstract void onFill(Map<String, String> dataMap, Parameters... parameters);

    /**
     * 当请求成功且返回结果中存在{@link #RESULT}标签的数据时被调用，
     * 即{@link #RESULT}不为null时此方法用于提取装配结果数据
     *
     * @param jsonResult 响应的完整json对象(包含{@link #RESULT})
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    protected abstract Result onSuccessExtract(JSONObject jsonResult) throws Exception;
}
