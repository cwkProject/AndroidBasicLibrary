package org.cwk.android.library.network.util;

/**
 * 可设置网络请求重试次数的工具接口
 *
 * @author 超悟空
 * @version 1.0 2018/1/11
 * @since 1.0 2018/1/11
 **/
public interface NetworkRetryHandler {

    /**
     * 设置重试次数
     *
     * @param times 新的重试次数，默认为0即不重试
     */
    void setRetryTimes(int times);
}
