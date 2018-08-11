package org.cwk.android.library.work;

import org.cwk.android.library.data.IDataModel;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * 任务创建RxJava被订阅者对象的接口
 *
 * @param <DataModel> 协议数据类型
 *
 * @author 超悟空
 * @version 1.0 2017/4/3
 * @since 1.0
 */
public interface CreateRxObservable<Parameters, DataModel extends IDataModel> {

    /**
     * 创建一个任务的{@link io.reactivex.Observable}对象
     *
     * @param parameters 任务所需参数
     *
     * @return {@link io.reactivex.Observable}对象
     */
    @SuppressWarnings("unchecked")
    Observable<DataModel> observable(Parameters... parameters);

    /**
     * 创建一个任务的{@link io.reactivex.Maybe}对象，执行成功会发送onSuccess，失败发送onComplete，不会发送onError事件
     *
     * @param parameters 任务所需参数
     *
     * @return {@link io.reactivex.Maybe}对象
     */
    Maybe<DataModel> maybe(Parameters... parameters);

    /**
     * 创建一个任务的{@link io.reactivex.Single}对象，执行成功会发送onSuccess，失败发送onError
     *
     * @param parameters 任务所需参数
     *
     * @return {@link io.reactivex.Single}对象
     */
    Single<DataModel> single(Parameters... parameters);

    /**
     * 创建一个任务的{@link io.reactivex.Completable}对象，执行成功会发送onComplete，失败发送onError
     *
     * @param parameters 任务所需参数
     *
     * @return {@link io.reactivex.Completable}对象
     */
    Completable completable(Parameters... parameters);
}
