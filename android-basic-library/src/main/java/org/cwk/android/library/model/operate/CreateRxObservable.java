package org.cwk.android.library.model.operate;

import org.cwk.android.library.model.data.WorkResult;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

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

    /**
     * 创建一个任务的{@link Maybe}对象，执行成功会发送onSuccess，失败发送onComplete，不会发送onError事件
     *
     * @param parameters 任务所需参数
     *
     * @return {@link Maybe}对象
     */
    Maybe<Result> maybe(Parameters... parameters);

    /**
     * 创建一个任务的{@link Single}对象，执行成功会发送onSuccess，失败发送onError
     *
     * @param parameters 任务所需参数
     *
     * @return {@link Single}对象
     */
    Single<Result> single(Parameters... parameters);

    /**
     * 创建一个任务的{@link Completable}对象，执行成功会发送onComplete，失败发送onError
     *
     * @param parameters 任务所需参数
     *
     * @return {@link Completable}对象
     */
    Completable completable(Parameters... parameters);
}
