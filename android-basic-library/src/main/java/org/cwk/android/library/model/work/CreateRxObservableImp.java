package org.cwk.android.library.model.work;

import android.support.annotation.NonNull;

import org.cwk.android.library.model.data.IDefaultDataModel;
import org.cwk.android.library.model.data.WorkResult;
import org.cwk.android.library.model.operate.CreateRxObservable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
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
                work.setOnWorkFinishListener(new OnWorkFinishListener<Result>() {
                    @Override
                    public void onFinish(boolean state, Result data, String message) {
                        e.onNext(new WorkResult<>(state, message, data));
                    }
                }, false).beginExecute(parameters);
            }
        });
    }
}