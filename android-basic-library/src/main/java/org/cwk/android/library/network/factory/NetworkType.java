package org.cwk.android.library.network.factory;

/**
 * 网络工具类型枚举
 *
 * @author 超悟空
 * @version 2.0 2016/8/4
 * @since 1.0
 */
public interface NetworkType {

    /**
     * http get类型请求
     */
    int GET = 0;

    /**
     * http post类型的请求
     */
    int POST = 1;

    /**
     * 下载
     */
    int DOWNLOAD = 2;

    /**
     * 上传
     */
    int UPLOAD = 3;

    /**
     * http delete类型的请求
     */
    int DELETE = 4;

    /**
     * http put类型的请求
     */
    int PUT = 5;
}
