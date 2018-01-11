package org.cwk.android.library.model.work;

import android.support.annotation.NonNull;

import org.cwk.android.library.R;
import org.cwk.android.library.annotation.Upload;
import org.cwk.android.library.global.Global;
import org.cwk.android.library.model.data.base.SimpleUploadDataModel;
import org.cwk.android.library.network.communication.ICommunication;
import org.cwk.android.library.network.factory.CommunicationBuilder;
import org.json.JSONObject;

import java.util.Map;

/**
 * 极简的一体化集成式网络下载任务模型基类，
 * 内置{@link org.cwk.android.library.model.data.base.SimpleUploadDataModel}的默认实现
 *
 * @author 超悟空
 * @version 1.0 2017/2/15
 * @since 1.0 2017/2/15
 **/
public abstract class SimpleUploadWorkModel<Parameters, Result> extends
        IntegratedWorkModel<Parameters, Result, SimpleUploadDataModel<Parameters, Result>> {

    /**
     * 服务响应的业务数据的参数默认取值标签
     */
    protected static final String RESULT = "result";

    @Override
    protected SimpleUploadDataModel<Parameters, Result> onCreateDataModel() {
        return new SimpleUploadDataModel<Parameters, Result>(TAG) {
            @Override
            protected Result onExtractData(@NonNull JSONObject jsonResult) throws Exception {
                return onSuccessExtract(jsonResult);
            }

            @SafeVarargs
            @Override
            protected final void onFillRequestParameters(@NonNull Map<String, Object> dataMap,
                                                         @NonNull Parameters... parameters) {
                onFill(dataMap, parameters);
            }

            @Override
            protected Result onDefaultData() throws Exception {
                return onSuccessDefault();
            }
        };
    }

    @Override
    @Upload
    protected final String onTaskUri() {
        return onTaskUri(mParameters);
    }

    @Override
    protected ICommunication onCreateCommunication(CommunicationBuilder builder) {
        builder.writeTimeout(Global.getApplication().getResources().getInteger(R.integer
                .http_upload_write_timeout));
        return super.onCreateCommunication(builder);
    }

    /**
     * 设置文件上传地址
     *
     * @param parameters 任务传入参数，即{@link #onCheckParameters(Object[])}检测通过后的参数列表
     *
     * @return 上传地址
     */
    @SuppressWarnings("unchecked")
    protected abstract String onTaskUri(@NonNull Parameters... parameters);

    /**
     * 填充服务请求所需的参数
     *
     * @param dataMap    将要填充的参数数据集<参数名,参数值>
     * @param parameters 任务传入的参数
     */
    @SuppressWarnings("unchecked")
    protected abstract void onFill(@NonNull Map<String, Object> dataMap, @NonNull Parameters...
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
}
