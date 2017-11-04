package org.cwk.android.library.model.work;

import android.support.annotation.Nullable;

/**
 * 任务同步执行接口
 *
 * @param <Parameters> 任务传入参数类型
 * @param <DataModel>  协议数据类型
 *
 * @author 超悟空
 * @version 1.0 2015/10/29
 * @since 1.0
 */
public interface SyncExecute<Parameters, DataModel> {

    /**
     * 执行任务操作，运行于当前线程
     *
     * @param parameters 任务所需参数
     *
     * @return 协议数据处理器，包含执行的结果
     */
    @SuppressWarnings("unchecked")
    @Nullable
    DataModel execute(@Nullable Parameters... parameters);
}
