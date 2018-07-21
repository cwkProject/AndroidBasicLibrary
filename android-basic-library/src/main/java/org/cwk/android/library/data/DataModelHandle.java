package org.cwk.android.library.data;

/**
 * 任务数据类框架助手，用于执行{@link WorkDataModel}类的框架方法
 *
 * @author 超悟空
 * @version 1.0 2018/7/20
 * @since 1.0
 */
public class DataModelHandle {

    /**
     * 执行任务数据模型参数设置
     *
     * @param parameters 任务参数
     */
    public static <Parameters> void setParams(WorkDataModel<?, ?, ?, Parameters, ?> dataModel ,
                                              Parameters[] parameters) {
        dataModel.setParams(parameters);
    }

    /**
     * 执行任务数据模型序列化
     *
     * @param dataModel    任务数据类
     * @param <Request>    序列化后的网络请求数据类型
     * @param <Parameters> 任务参数类型
     *
     * @return 序列化后的网络请求数据
     */
    public static <Request, Parameters> Request serialization(WorkDataModel<Request, ?, ?,
            Parameters, ?> dataModel) {
        return dataModel.serialization();
    }

    /**
     * 执行任务数据模型解析
     *
     * @param dataModel  任务数据类
     * @param code       网络请求响应码
     * @param response   网络请求响应数据
     * @param <Response> 网络请求响应数据类型
     *
     * @return true表示解析成功，false表示解析失败
     */
    public static <Response> boolean parse(WorkDataModel<?, Response, ?, ?, ?> dataModel , int
            code , Response response) {
        return dataModel.parse(code , response);
    }
}
