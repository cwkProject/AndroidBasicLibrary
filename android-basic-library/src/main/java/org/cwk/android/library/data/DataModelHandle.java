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
     * @param response   网络请求响应数据
     * @param <Response> 网络请求响应数据类型
     *
     * @return true表示解析成功，false表示解析失败
     */
    public static <Response> boolean parse(WorkDataModel<?, Response, ?, ?, ?> dataModel ,
                                           Response response) {
        return dataModel.parse(response);
    }

    /**
     * 设置结果消息，通常用于设置网络请求失败时的消息和覆盖自解析生成的消息
     *
     * @param dataModel 任务数据类
     * @param message   消息内容
     */
    public static void setMessage(WorkDataModel<?, ?, ?, ?, ?> dataModel , String message) {
        dataModel.setMessage(message);
    }

    /**
     * 设置http响应码
     *
     * @param dataModel 任务数据类
     * @param code      http响应码
     */
    public static void setCode(WorkDataModel<?, ?, ?, ?, ?> dataModel , int code) {
        dataModel.setResponseCode(code);
    }
}
