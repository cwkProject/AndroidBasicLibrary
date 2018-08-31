package org.cwk.android.library.work;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cwk.android.library.data.WorkDataModel;

/**
 * 任务{@link android.arch.lifecycle.LiveData}支持接口
 *
 * @param <Parameters> 任务传入参数类型
 * @param <DataModel>  协议数据类型
 *
 * @author 超悟空
 * @version 1.0 2018/8/31
 * @since 1.0
 */
public interface LiveDataExecute<Parameters, DataModel extends WorkDataModel> {

    /**
     * 获取LiveData，无论哪种方式启动任务，任务执行结束后都会对LiveData赋值
     *
     * @return LiveData结果数据
     */
    @NonNull
    LiveData<DataModel> getLiveData();

    /**
     * 执行任务操作，运行于新线程，同步返回LiveData对象，任务执行结束后会对LiveData赋值
     *
     * @param parameters 任务所需参数
     *
     * @return LiveData结果数据
     */
    @SuppressWarnings("unchecked")
    @NonNull
    LiveData<DataModel> executeLiveData(@Nullable Parameters... parameters);
}
