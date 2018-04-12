package org.cwk.android.library.data;

import android.support.annotation.Nullable;

/**
 * 基础数据模型接口，用于网络传输协议和结构协议的解析处理
 *
 * @param <Response> 要解析的结果数据类型
 * @param <Request>  要序列化的目标类型
 *
 * @author 超悟空
 * @version 1.0 2015/1/6
 * @since 1.0
 */
public interface IDataModel<Response, Request> {
    /**
     * 序列化要提交的数据,用于与服务器交互
     *
     * @return 返回Map集合
     */
    Request serialization();

    /**
     * 解析传回的数据
     *
     * @param response 要解析的数据
     *
     * @return 解析执行结果
     */
    boolean parse(Response response);

    /**
     * 判断本次服务请求是否成功
     *
     * @return true表示成功，false表示失败
     */
    boolean isSuccess();

    /**
     * 获取本次请求返回的结果消息
     *
     * @return 消息字符串
     */
    @Nullable
    String getMessage();
}
