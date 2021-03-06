package org.cwk.android.library.work;

import android.support.annotation.NonNull;

import org.cwk.android.library.data.SimpleDataModel;
import org.json.JSONObject;

import java.util.Map;

/**
 * 极简的一体化集成式网络任务模型基类，
 * 内置{@link SimpleDataModel}的默认实现
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public abstract class SimpleWorkModel<Parameters, Result> extends StandardWorkModel<Parameters,
        SimpleDataModel<Parameters, Result>> {

    /**
     * 服务响应的业务数据的参数默认取值标签
     */
    protected static final String RESULT = "result";

    /**
     * 服务响应的业务消息的参数默认取值标签
     */
    protected static final String MESSAGE = "message";

    @NonNull
    @Override
    protected SimpleDataModel<Parameters, Result> onCreateDataModel() {
        return new SimpleDataModel<Parameters, Result>(TAG) {
            @Override
            protected Result onExtractData(@NonNull JSONObject jsonResult) throws Exception {
                return onSuccessExtract(jsonResult);
            }

            @Override
            protected Result onRequestFailed(@NonNull JSONObject handleResult) throws Exception {
                super.onRequestFailed(handleResult);
                return onFailedExtract(handleResult);
            }

            @SafeVarargs
            @Override
            protected final void onFillRequestParameters(@NonNull Map<String, String> dataMap ,
                                                         @NonNull Parameters... parameters) {
                onFill(dataMap , parameters);
            }

            @Override
            protected Result onDefaultData() throws Exception {
                return onSuccessDefault();
            }

            @Override
            protected String onRequestFailedMessage(@NonNull JSONObject handleResult) throws
                    Exception {
                return SimpleWorkModel.this.onRequestFailedMessage(handleResult);
            }

            @Override
            protected String onRequestSuccessMessage(@NonNull JSONObject handleResult) throws
                    Exception {
                return SimpleWorkModel.this.onRequestSuccessMessage(handleResult);
            }
        };
    }

    /**
     * 填充服务请求所需的参数
     *
     * @param dataMap    将要填充的参数数据集(参数名,参数值)
     * @param parameters 任务传入的参数
     */
    @SuppressWarnings("unchecked")
    protected abstract void onFill(@NonNull Map<String, String> dataMap , @NonNull Parameters...
            parameters);

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
    protected abstract Result onSuccessExtract(@NonNull JSONObject jsonResult) throws Exception;

    /**
     * 当请求失败时设置一个特殊的返回值，
     * 即{@link SimpleDataModel#onRequestResult(JSONObject)}返回false时被调用，
     * 此时{@link SimpleDataModel#getErrorCode()}已经有值
     *
     * @param jsonResult 响应的完整json对象
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    protected Result onFailedExtract(@NonNull JSONObject jsonResult) throws Exception {
        return null;
    }

    /**
     * 当请求成功且返回结果不存在{@link #RESULT}标签的数据时被调用，
     * 即{@link #RESULT}为null时此方法用于装配默认结果数据，默认实现为null
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    protected Result onSuccessDefault() throws Exception {
        return null;
    }

    /**
     * 提取或设置服务返回的失败结果消息<br>
     * 在{@link SimpleDataModel#onRequestFailed(JSONObject)}之后被调<br>
     * 且服务器返回的执行结果为失败{@link SimpleDataModel#isSuccess()}为false
     *
     * @param handleResult 二次处理结果集
     *
     * @return 消息字符串
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected String onRequestFailedMessage(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.optString(MESSAGE);
    }

    /**
     * 提取或设置服务返回的成功结果消息<br>
     * 在{@link SimpleDataModel#onRequestSuccess(JSONObject)}}之后被调<br>
     * 且服务器返回的执行结果为成功{@link SimpleDataModel#isSuccess()}为true
     *
     * @param handleResult 二次处理结果集
     *
     * @return 消息字符串
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected String onRequestSuccessMessage(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.optString(MESSAGE);
    }
}
