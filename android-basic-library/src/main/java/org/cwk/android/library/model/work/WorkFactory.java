package org.cwk.android.library.model.work;

import org.cwk.android.library.model.data.IDefaultDataModel;
import org.cwk.android.library.model.operate.CreateRxObservable;

import io.reactivex.Observable;

/**
 * 用于创建Work的RxAndroid被订阅者对象的工厂类
 *
 * @author 超悟空
 * @version 1.0 2017/4/3
 * @since 1.0
 */
public class WorkFactory {

    /**
     * 获得一个{@link CreateRxObservable}接口实例用于创建任务的RxAndroid
     * 被订阅者，之后调用{@link CreateRxObservable#observable(Object[])}获取{@link Observable}实例
     *
     * @param workClass    任务类
     * @param <T>          任务类类型
     * @param <Parameters> 任务类使用的参数类型
     * @param <Result>     任务类响应的结果类型
     *
     * @return 被订阅者
     */
    public static <T extends DefaultWorkModel<Parameters, Result, ? extends IDefaultDataModel>,
            Parameters, Result> CreateRxObservable<Parameters, Result> from(final Class<T> workClass) {
        return new CreateRxObservableImp<>(workClass);
    }
}
