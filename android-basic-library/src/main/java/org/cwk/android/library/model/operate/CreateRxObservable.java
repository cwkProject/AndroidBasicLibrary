package org.cwk.android.library.model.operate;

import org.cwk.android.library.model.data.WorkResult;

import io.reactivex.Observable;

/**
 * 任务创建RxJava被订阅者对象的接口
 *
 * @author 超悟空
 * @version 1.0 2017/4/3
 * @since 1.0
 */
public interface CreateRxObservable<Parameters, Result> {

    /**
     * 创建一个任务的{@link Observable}对象
     *
     * @param parameters 任务所需参数
     *
     * @return {@link Observable}对象
     */
    @SuppressWarnings("unchecked")
    Observable<WorkResult<Result>> observable(Parameters... parameters);
}
