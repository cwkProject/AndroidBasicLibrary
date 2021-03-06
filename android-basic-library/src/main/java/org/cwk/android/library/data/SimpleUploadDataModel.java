package org.cwk.android.library.data;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONObject;

/**
 * 用于上传任务的简单任务数据模型基类<br>
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
public abstract class SimpleUploadDataModel<Parameters, Result> extends StandardDataModel<Object,
        String, JSONObject, Parameters, Result> {

    /**
     * 服务响应的业务数据的参数默认取值标签
     */
    protected static final String RESULT = "result";

    /**
     * 构造函数
     *
     * @param tag 标签，用于跟踪日志
     */
    public SimpleUploadDataModel(String tag) {
        super(tag);
    }

    @Override
    protected final boolean onCheckResponse(String response) {
        return !TextUtils.isEmpty(response);
    }

    @Override
    protected final JSONObject onCreateHandle(String response) throws Exception {
        return new JSONObject(response);
    }

    /**
     * 服务响应的errorCode，0为默认值，大于0为错误代码
     */
    private int errorCode = 0;

    @Override
    protected boolean onRequestResult(@NonNull JSONObject handleResult) throws Exception {
        // 得到执行结果
        return handleResult.getBoolean("state");
    }

    @Override
    protected final Result onRequestSuccess(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.isNull(RESULT) ? onDefaultData() : onExtractData(handleResult);
    }

    @Override
    @CallSuper
    protected Result onRequestFailed(@NonNull JSONObject handleResult) throws Exception {
        if (!handleResult.isNull("errorCode")) {
            errorCode = handleResult.getInt("errorCode");
        }

        return super.onRequestFailed(handleResult);
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
    @SuppressWarnings("NullableProblems")
    protected abstract Result onExtractData(@NonNull JSONObject jsonData) throws Exception;

    /**
     * 当请求成功且返回结果不存在{@link #RESULT}标签的数据时被调用，
     * 即{@link #RESULT}为null时此方法用于装配默认结果数据，默认实现为null
     *
     * @return 处理后的任务传出结果
     *
     * @throws Exception 处理过程抛出的异常
     */
    protected Result onDefaultData() throws Exception {
        return null;
    }

    @Override
    protected String onRequestFailedMessage(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.optString("message");
    }

    @Override
    protected String onRequestSuccessMessage(@NonNull JSONObject handleResult) throws Exception {
        return handleResult.optString("message");
    }

    /**
     * 获取服务响应的errorCode，0为默认值，大于0为错误代码
     *
     * @return 成功时为0，错误时大于0
     */
    public int getErrorCode() {
        return errorCode;
    }
}
