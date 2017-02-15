package org.cwk.android.library.model.data.base;

import org.json.JSONObject;

/**
 * 基于集成化任务架构，
 * 用于上传任务的数据模型基类<br>
 * 请求参数Map集合的值包含文件类型，
 * 支持的文件类型为{@link java.io.File}，{@link org.cwk.android.library.struct.FileInfo}两种，
 * {@link org.cwk.android.library.struct.FileInfo}为文件的包装类型，
 * 用于设定上传时使用的文件名和MIME类型，
 * 同时也可以发送普通的文本类型参数
 *
 * @author 超悟空
 * @version 1.0 2017/2/15
 * @since 1.0 2017/2/15
 **/
public abstract class SimpleUploadDataModel<Parameters, Result> extends
        IntegratedDataModel<Parameters, Result, JSONObject, String, Object> {
    @Override
    protected final boolean onCheckResponse(String response) {
        return response != null;
    }

    @Override
    protected final JSONObject onCreateHandle(String response) throws Exception {
        return new JSONObject(response);
    }

    /**
     * 服务响应的业务数据的参数默认取值标签
     */
    protected static final String RESULT_TAG = "result";

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
    protected Result onSuccess(JSONObject handleResult) throws Exception {
        if (!handleResult.isNull(RESULT_TAG)) {
            return onExtractData(handleResult);
        } else {
            return null;
        }
    }

    /**
     * 当请求成功且返回结果中存在{@link #RESULT_TAG}标签的数据时被调用，
     * 即{@link #RESULT_TAG}不为null时此方法用于提取装配结果数据
     *
     * @param jsonData 响应的完整数据结果(包含{@link #RESULT_TAG})
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    protected abstract Result onExtractData(JSONObject jsonData) throws Exception;
}
