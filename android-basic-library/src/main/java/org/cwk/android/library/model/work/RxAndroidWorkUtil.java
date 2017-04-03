package org.cwk.android.library.model.work;

import org.cwk.android.library.model.data.IDefaultDataModel;
import org.cwk.android.library.model.data.WorkResult;
import org.cwk.android.library.model.operate.CreateObservable;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Cancellable;

/**
 * 用于创建Work的RxAndroid被订阅者任务对象的工具类
 *
 * @author 超悟空
 * @version 1.0 2017/4/3
 * @since 1.0
 */
public class RxAndroidWorkUtil {

    /**
     * 获得一个{@link CreateObservable}接口实例用于创建任务的RxAndroid
     * 被订阅者，之后调用{@link CreateObservable#createObservable(Object[])}获取{@link Observable}实例
     *
     * @param workClass    任务类
     * @param <T>          任务类类型
     * @param <Parameters> 任务类使用的参数类型
     * @param <Result>     任务类响应的结果类型
     *
     * @return 被订阅者
     */
    public static <T extends DefaultWorkModel<Parameters, Result, ? extends IDefaultDataModel>,
            Parameters, Result> CreateObservable<Parameters, Result> from(final Class<T> workClass) {
        return new CreateObservable<Parameters, Result>() {
            @SafeVarargs
            @Override
            public final Observable<WorkResult<Result>> createObservable(final Parameters...
                                                                                 parameters) {
                return Observable.create(new ObservableOnSubscribe<WorkResult<Result>>() {
                    @Override
                    public void subscribe(@NonNull final ObservableEmitter<WorkResult<Result>> e)
                            throws Exception {
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
        };
    }

}
