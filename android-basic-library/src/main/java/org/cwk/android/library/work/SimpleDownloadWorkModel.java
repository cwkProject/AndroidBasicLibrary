package org.cwk.android.library.work;

import android.support.annotation.NonNull;

import org.cwk.android.library.annotation.Download;
import org.cwk.android.library.data.SimpleDownloadDataModel;

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
        StandardWorkModel<Parameters, SimpleDownloadDataModel<Parameters, Result>> {
    @NonNull
    @Override
    protected SimpleDownloadDataModel<Parameters, Result> onCreateDataModel() {
        return new SimpleDownloadDataModel<Parameters, Result>(TAG) {

            @Override
            protected Result onRequestSuccess(@NonNull InputStream handleResult) throws Exception {
                return onSuccessExtract(handleResult);
            }

            @SafeVarargs
            @Override
            protected final void onFillRequestParameters(@NonNull Map<String, String> dataMap ,
                                                         @NonNull Parameters... parameters) {
                onFill(dataMap , parameters);
            }

            @Override
            protected String onRequestSuccessMessage(InputStream handleResult) throws Exception {
                return onDownloadSuccessMessage();
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
     * 处理下载数据<br>
     * 仅网络请求成功建立连接时该方法才会被调用，
     * 在这里应该将{@code handleResult}写入到文件或内存中，
     * 只有这里读取{@code handleResult}后网络连接才会真正开始向服务器拉取数据，
     * 所以该方法并不是下载完成后被调用，
     * 而是成功建立连接并开始下载数据时就被调用。<br>
     * 由于该方法的父框架需要同步返回执行结果，
     * 所以该方法不必新开线程进行IO操作，
     * 如果该任务通过异步启动，
     * 则该方法本身就会运行在新线程中，
     * 如果任务通过同步启动，
     * 则该方法会在用户建立的任务执行线程中运行。
     *
     * @param inputStream 响应的下载数据流
     *
     * @throws Exception 数据处理过程中的异常
     */
    protected abstract Result onSuccessExtract(@NonNull InputStream inputStream) throws Exception;

    @Override
    @Download
    protected final String onTaskUri() {
        return onTaskUri(mParameters);
    }

    /**
     * 设置文件下载地址
     *
     * @param parameters 任务传入参数，即{@link #onCheckParameters(Object[])}检测通过后的参数列表
     *
     * @return 下载地址
     */
    @SuppressWarnings("unchecked")
    protected abstract String onTaskUri(@NonNull Parameters... parameters);

    /**
     * 提取或设置下载成功的结果消息<br>
     * 在{@link #onSuccessExtract(InputStream)}}之后被调<br>
     *
     * @return 消息字符串
     *
     * @throws Exception 处理过程中可能出现的异常
     */
    protected String onDownloadSuccessMessage() throws Exception {
        return null;
    }
}
