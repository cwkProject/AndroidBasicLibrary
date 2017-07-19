package org.cwk.android.library.model.work;

import android.support.annotation.NonNull;

import org.cwk.android.library.model.data.IDefaultDataModel;
import org.cwk.android.library.model.data.WorkResult;
import org.cwk.android.library.model.operate.CreateRxObservable;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Cancellable;

/**
 * 任务创建RxJava被订阅者对象的实现类
 *
 * @author 超悟空
 * @version 1.0 2017/4/4
 * @since 1.0
 */
class CreateRxObservableImp<T extends DefaultWorkModel<Parameters, Result, ? extends
        IDefaultDataModel>, Parameters, Result> implements CreateRxObservable<Parameters, Result> {

    /**
     * 任务类类型
     */
    private Class<T> workClass = null;

    /**
     * 构造函数
     *
     * @param workClass 任务类类型
     */
    CreateRxObservableImp(Class<T> workClass) {
        this.workClass = workClass;
    }

    @SafeVarargs
    @Override
    public final Observable<WorkResult<Result>> observable(final Parameters... parameters) {
        return Observable.create(new ObservableOnSubscribe<WorkResult<Result>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<WorkResult<Result>> e) throws
                    Exception {
                // 创建任务实例
                final T work = workClass.newInstance();

                // 设置需要任务监听
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        if (work != null && !work.isCanceled()) {
                            work.cancel();
                        }
                    }
                });
                work.setOnWorkFinishListener(false, new OnWorkFinishListener<Result>() {
                    @Override
                    public void onFinish(boolean state, Result data, String message) {
                        e.onNext(new WorkResult<>(state, message, data));
                    }
                }).beginExecute(parameters);
            }
        });
    }

    @SafeVarargs
    @Override
    public final Maybe<Result> maybe(final Parameters... parameters) {
        return Maybe.create(new MaybeOnSubscribe<Result>() {
            @Override
            public void subscribe(final MaybeEmitter<Result> maybeEmitter) throws Exception {
                // 创建任务实例
                final T work = workClass.newInstance();

                // 设置需要任务监听
                maybeEmitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        if (work != null && !work.isCanceled()) {
                            work.cancel();
                        }
                    }
                });
                work.setOnWorkFinishListener(false, new OnWorkFinishListener<Result>() {
                    @Override
                    public void onFinish(boolean state, Result data, String message) {
                        if (state) {
                            maybeEmitter.onSuccess(data);
                        } else {
                            maybeEmitter.onComplete();
                        }
                    }
                }).beginExecute(parameters);
            }
        });
    }

    @SafeVarargs
    @Override
    public final Single<Result> single(final Parameters... parameters) {
        return Single.create(new SingleOnSubscribe<Result>() {
            @Override
            public void subscribe(final SingleEmitter<Result> singleEmitter) throws Exception {
                // 创建任务实例
                final T work = workClass.newInstance();

                // 设置需要任务监听
                singleEmitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        if (work != null && !work.isCanceled()) {
                            work.cancel();
                        }
                    }
                });
                work.setOnWorkFinishListener(false, new OnWorkFinishListener<Result>() {
                    @Override
                    public void onFinish(boolean state, Result data, String message) {
                        if (state) {
                            singleEmitter.onSuccess(data);
                        } else {
                            singleEmitter.onError(new Throwable(message));
                        }
                    }
                }).beginExecute(parameters);
            }
        });
    }

    @SafeVarargs
    @Override
    public final Completable completable(final Parameters... parameters) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter completableEmitter) throws Exception {
                // 创建任务实例
                final T work = workClass.newInstance();

                // 设置需要任务监听
                completableEmitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        if (work != null && !work.isCanceled()) {
                            work.cancel();
                        }
                    }
                });
                work.setOnWorkFinishListener(false, new OnWorkFinishListener<Result>() {
                    @Override
                    public void onFinish(boolean state, Result data, String message) {
                        if (state) {
                            completableEmitter.onComplete();
                        } else {
                            completableEmitter.onError(new Throwable(message));
                        }
                    }
                }).beginExecute(parameters);
            }
        });
    }
}