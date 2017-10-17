package org.cwk.android.library.model.data;

import java.util.Map;

/**
 * 常用的默认数据模型接口
 *
 * @param <Response> 要解析的结果数据类型
 * @param <Request>  要序列化的目标类型
 *
 * @author 超悟空
 * @version 2.0 2015/11/3
 * @since 1.0
 */
public interface IDefaultDataModel<Response, Request extends Map<String, ?>> extends
        IDataModel<Response, Request> {
}
