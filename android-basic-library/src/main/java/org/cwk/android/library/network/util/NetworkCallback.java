package org.cwk.android.library.network.util;

/**
 * 网络请求结果回调接口
 *
 * @author 超悟空
 * @version 1.0 2015/10/30
 * @since 1.0
 */
public interface NetworkCallback<ResponseType> {

    /**
     * 网络请求结束回调
     *
     * @param result   请求结果
     * @param code     http响应码，0表示网络未建立连接
     * @param response 响应数据
     */
    void onFinish(boolean result , int code , ResponseType response);
}
