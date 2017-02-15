package org.cwk.android.library.model.work;

import org.cwk.android.library.annotation.Download;
import org.cwk.android.library.model.data.IIntegratedDataModel;
import org.cwk.android.library.model.data.base.SimpleDownloadDataModel;

import java.io.InputStream;
import java.util.Map;

/**
 * 极简的一体化集成式网络下载任务模型基类，
 * 内置{@link SimpleDownloadWorkModel}的默认实现
 *
 * @author 超悟空
 * @version 1.0 2017/2/15
 * @since 1.0 2017/2/15
 **/
public abstract class SimpleDownloadWorkModel<Parameters, Result> extends
        IntegratedWorkModel<Parameters, Result> {
    @Override
    protected IIntegratedDataModel<Parameters, Result, ?, ?> onCreateDataModel() {
        return new SimpleDownloadDataModel<Parameters, Result>() {
            @Override
            protected Result onSuccess(InputStream handleResult) throws Exception {
                return onSuccessExtract(handleResult);
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
     * 处理下载数据<br>
     * 仅网络请求成功建立连接时该方法才会被调用，
     * 在这里应该将{@code handleResult}写入到文件或内存中，
     * 只有这里读取{@code handleResult}后网络连接才会真正开始向服务器拉取数据，
     * 所以该方法并不是下载完成后被调用，
     * 而是成功建立连接并开始下载数据时才被调用。<br>
     * 由于该方法的父框架需要同步返回执行结果，
     * 所以该方法不必新开线程进行IO操作，
     * 如果该任务通过异步启动，
     * 则该方法本身就会运行在新线程中，
     * 如果任务通过同步启动，
     * 则该方法会在用户建立的任务执行线程中运行。
     *
     * @param inputStream 响应的下载数据流
     *
     * @throws Exception
     */
    protected abstract Result onSuccessExtract(InputStream inputStream) throws Exception;

    /**
     * 提取服务反馈的结果数据<br>
     * 在服务请求失败或数据流写入失败后调用，
     * 即网络访问失败或{@link #onSuccessExtract}处理数据出错时被调用
     *
     * @return 处理后的任务传出结果
     */
    protected Result onFailedExtract() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected boolean onCheckParameters(Parameters... parameters) {
        return true;
    }

    @Override
    @Download
    protected final String onTaskUri() {
        return onTaskUri(getParameters());
    }

    /**
     * 设置文件下载地址
     *
     * @param parameters 任务传入参数，即{@link #onCheckParameters(Object[])}检测通过后的参数列表
     *
     * @return 下载地址
     */
    @SuppressWarnings("unchecked")
    protected abstract String onTaskUri(Parameters... parameters);

    @Override
    protected final String onParseSuccessSetMessage(boolean state,
                                                    IIntegratedDataModel<Parameters, Result, ?,
                                                            ?> data) {
        return super.onParseSuccessSetMessage(state, data);
    }

    @Override
    protected final Result onParseFailedSetResult(IIntegratedDataModel<Parameters, Result, ?, ?>
                                                              data) {
        return onFailedExtract();
    }

    @Override
    protected final String onParseFailedSetMessage(IIntegratedDataModel<Parameters, Result, ?, ?>
                                                               data) {
        return super.onParseFailedSetMessage(data);
    }

    @Override
    protected final void onParseSuccess(IIntegratedDataModel<Parameters, Result, ?, ?> data) {
        super.onParseSuccess(data);
    }

    @Override
    protected final void onParseFailed(IIntegratedDataModel<Parameters, Result, ?, ?> data) {
        super.onParseFailed(data);
    }
}
