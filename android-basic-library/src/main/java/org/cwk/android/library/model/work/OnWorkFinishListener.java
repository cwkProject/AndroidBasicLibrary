package org.cwk.android.library.model.work;

import org.cwk.android.library.model.data.IDataModel;

/**
 * 任务结束回调接口
 *
 * @param <DataModel> 协议数据类型
 *
 * @author 超悟空
 * @version 1.0 2015/10/16
 * @since 1.0
 */
public interface OnWorkFinishListener<DataModel extends IDataModel> {

    /**
     * 任务结束回调方法，
     * 在任务函数执行结束后被调用
     *
     * @param data 协议数据处理器，包含执行结果
     */
    void onFinish(DataModel data);
}
