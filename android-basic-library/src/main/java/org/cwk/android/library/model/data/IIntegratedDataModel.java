package org.cwk.android.library.model.data;

import java.util.Map;

/**
 * 进一步简化的数据模型接口，集成化数据模型
 *
 * @param <Parameters> 任务传入参数类型
 * @param <Result>     任务返回结果类型
 * @param <Response>   要解析的结果数据类型
 * @param <Request>    要序列化的目标类型
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public interface IIntegratedDataModel<Parameters, Result, Response, Request extends Map<String,
        ?>> extends IDefaultDataModel<Response, Request> {

    /**
     * 设置任务传入参数
     *
     * @param parameters 参数
     */
    @SuppressWarnings("unchecked")
    void setParameters(Parameters... parameters);

    /**
     * 获取处理完成的响应结果
     *
     * @return 响应数据
     */
    Result getResult();
}
