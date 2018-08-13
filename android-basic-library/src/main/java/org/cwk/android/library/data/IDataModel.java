package org.cwk.android.library.data;

import android.support.annotation.Nullable;

/**
 * 基础数据模型接口，用于网络传输协议和结构协议的解析处理
 *
 * @param <Parameters> 任务传入参数类型
 * @param <Result>     任务返回结果类型
 *
 * @author 超悟空
 * @version 1.0 2015/1/6
 * @since 1.0
 */
public interface IDataModel<Parameters, Result> {

    /**
     * 判断本次服务请求是否成功(用户接口协议约定的请求结果，并非http的请求结果，但是http请求失败时该值总是返回false)
     *
     * @return true表示成功，false表示失败
     */
    boolean isSuccess();

    /**
     * 获取本次请求返回的结果消息(用户接口协议中约定的消息，并非http响应消息）
     *
     * @return 消息字符串
     */
    @Nullable
    String getMessage();

    /**
     * 获取本次http请求返回的响应码
     *
     * @return http响应码，0表示网络连接建立失败
     */
    int getCode();

    /**
     * 获取任务传入的参数列表
     *
     * @return 参数数据
     */
    Parameters[] getParams();

    /**
     * 获取处理完成的最终结果数据(用户接口协议中定义的有效数据转化成的java类型)
     *
     * @return 请求返回的主要数据
     */
    Result getResult();
}
