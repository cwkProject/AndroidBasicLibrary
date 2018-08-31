package org.cwk.android.library.work;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.CallSuper;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.cwk.android.library.data.WorkDataModel;
import org.cwk.android.library.network.factory.CommunicationBuilder;
import org.cwk.android.library.network.util.AsyncCommunication;
import org.cwk.android.library.network.util.OnNetworkProgressListener;
import org.cwk.android.library.network.util.SyncCommunication;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;

/**
 * 默认实现的网络任务模型基类<br>
 * 内部使用{@link WorkDataModel}作为默认的数据模型类，
 * 使用{@link SyncCommunication}作为同步网络请求工具，
 * 使用{@link AsyncCommunication}作为异步网络请求工具，
 * 集成RxJava构建器
 *
 * @param <Parameters> 功能所需参数类型
 * @param <DataModel>  任务请求使用的数据模型类型
 *
 * @author 超悟空
 * @version 4.0 2017/10/17
 * @since 1.0 2014/11/2
 */
public abstract class StandardWorkModel<Parameters, DataModel extends WorkDataModel> extends
        WorkModel<Parameters, DataModel> implements LiveDataExecute<Parameters, DataModel>,
        LifeCycleBindable, CreateRxObservable<Parameters, DataModel> {

    /**
     * 任务完成回调接口
     */
    private OnWorkFinishListener<DataModel> onWorkFinishListener = null;

    /**
     * 网络请求进度监听器，可用于上传和下载进度监听
     */
    private OnNetworkProgressListener onNetworkProgressListener = null;

    /**
     * 任务被取消回调接口
     */
    private OnWorkCanceledListener<Parameters> onWorkCanceledListener = null;

    /**
     * 存放任务结果的LiveData
     */
    private MutableLiveData<DataModel> liveData = null;

    /**
     * 任务UI生命周期同步
     */
    private WorkLifecycle workLifecycle = null;

    /**
     * 指示是否将取消回调接口在UI线程执行，默认为发送到UI线程
     */
    private boolean isEndUiThread = true;

    /**
     * 指示是否将进度回调接口在UI线程执行，默认为发送到UI线程
     */
    private boolean isProgressUiThread = true;

    /**
     * 指示是否将取消任务回调接口在UI线程执行，默认为发送到UI线程
     */
    private boolean isCancelUiThread = true;

    /**
     * 任务请求重试次数
     */
    private int retryTimes = 0;

    @NonNull
    @Override
    public LiveData<DataModel> getLiveData() {
        synchronized (this) {
            if (liveData == null) {
                liveData = new MutableLiveData<>();
            }
        }

        return liveData;
    }

    @SafeVarargs
    @NonNull
    @Override
    public final LiveData<DataModel> executeLiveData(@Nullable Parameters... parameters) {
        synchronized (this) {
            if (liveData == null) {
                liveData = new MutableLiveData<>();
            }
        }

        beginExecute(parameters);

        return liveData;
    }

    @MainThread
    @Override
    public StandardWorkModel<Parameters, DataModel> setLifecycleOwner(@NonNull LifecycleOwner
                                                                                  lifecycleOwner) {
        return setLifecycleOwner(lifecycleOwner , true);
    }

    @MainThread
    @Override
    public StandardWorkModel<Parameters, DataModel> setLifecycleOwner(@NonNull LifecycleOwner
                                                                                  lifecycleOwner
            , boolean isOnce) {
        if (workLifecycle != null) {
            workLifecycle.unregister();
        }

        workLifecycle = new WorkLifecycle(lifecycleOwner.getLifecycle() , this);
        workLifecycle.isOnce = isOnce;

        return this;
    }

    @CallSuper
    @Override
    protected void onCreateCommunication(@NonNull CommunicationBuilder builder) {
        builder.retryTimes(retryTimes).networkRefreshProgressListener(onCreateProgressListener());
    }

    /**
     * 创建网络请求进度监听器，根据情况可能进行了包装
     *
     * @return 网络请求进度监听器
     */
    protected OnNetworkProgressListener onCreateProgressListener() {
        if (onNetworkProgressListener != null) {
            // 开始绑定
            if (isProgressUiThread) {
                // 发送到UI线程
                return (current , total , done) -> MAIN_HANDLER.post(() ->
                        onNetworkProgressListener.onRefreshProgress(current , total , done));
            } else {
                // 在当前线程
                // 直接绑定
                return onNetworkProgressListener;
            }
        } else {
            return null;
        }
    }

    @Override
    protected void onStopWork() {
        super.onStopWork();

        // 如果设置了回调接口则执行回调方法
        if (!cancelMark && isAsync && this.onWorkFinishListener != null) {
            if (isEndUiThread) {
                // 发送到UI线程
                Log.v(TAG , "onWorkFinishListener invoked in main thread");
                MAIN_HANDLER.post(() -> onWorkFinishListener.onFinish(mData));
            } else {
                // 发送到当前线程
                Log.v(TAG , "onWorkFinishListener invoked in background thread");
                this.onWorkFinishListener.onFinish(mData);
            }
        }

        if (workLifecycle != null && workLifecycle.isOnce) {
            workLifecycle.unregister();
            workLifecycle = null;
        }

        if (liveData != null) {
            liveData.postValue(mData);
        }
    }

    @CallSuper
    @Override
    protected void onCanceled() {
        if (onWorkCanceledListener != null) {
            Log.v(TAG , "onWorkCanceledListener invoked");
            if (isCancelUiThread) {
                // 发送到UI线程
                MAIN_HANDLER.post(() -> onWorkCanceledListener.onCanceled(mParameters));
            } else {
                // 发送到当前线程
                this.onWorkCanceledListener.onCanceled(mParameters);
            }
        }

        if (workLifecycle != null && workLifecycle.isOnce) {
            workLifecycle.unregister();
            workLifecycle = null;
        }
    }

    /**
     * 设置任务完成时的回调接口<br>
     * 在任务执行完成后被回调，
     * 运行于UI线程
     *
     * @param onWorkFinishListener 监听器对象
     *
     * @return 当前任务实例
     */
    public final StandardWorkModel<Parameters, DataModel> setOnWorkFinishListener
    (OnWorkFinishListener<DataModel> onWorkFinishListener) {
        return setOnWorkFinishListener(true , onWorkFinishListener);
    }

    /**
     * 设置任务的进度更新回调接口<br>
     * 在异步任务中实时更新任务执行进度，
     * 运行于UI线程
     *
     * @param onNetworkProgressListener 监听器对象
     *
     * @return 当前任务实例
     */
    public final StandardWorkModel<Parameters, DataModel> setOnNetworkProgressListener
    (OnNetworkProgressListener onNetworkProgressListener) {
        return setOnNetworkProgressListener(true , onNetworkProgressListener);
    }

    /**
     * 设置任务完成时的回调接口<br>
     * 在任务执行完成后被回调，
     * 并设置是否在当前线程执行
     *
     * @param isUiThread           指示是否在UI线程回调，
     *                             true表示在UI线程回调，
     *                             false表示在当前线程（网络IO线程）回调，
     *                             默认为true
     * @param onWorkFinishListener 监听器对象
     *
     * @return 当前任务实例
     */
    public final StandardWorkModel<Parameters, DataModel> setOnWorkFinishListener(boolean isUiThread , OnWorkFinishListener<DataModel> onWorkFinishListener) {
        this.onWorkFinishListener = onWorkFinishListener;
        this.isEndUiThread = isUiThread;
        return this;
    }

    /**
     * 设置任务的进度更新回调接口<br>
     * 在异步任务中实时更新任务执行进度，
     * 并设置是否在当前线程执行
     *
     * @param isUiThread                指示是否在UI线程回调，
     *                                  true表示在UI线程回调，
     *                                  false表示在当前线程（通过{@link #execute(Object[])}执行则在相同线程，
     *                                  通过{@link #beginExecute(Object[])}执行则在网络IO线程）回调，
     *                                  默认为true
     * @param onNetworkProgressListener 监听器对象
     *
     * @return 当前任务实例
     */
    public final StandardWorkModel<Parameters, DataModel> setOnNetworkProgressListener(boolean isUiThread , OnNetworkProgressListener onNetworkProgressListener) {
        this.onNetworkProgressListener = onNetworkProgressListener;
        this.isProgressUiThread = isUiThread;

        return this;
    }

    /**
     * 设置任务的取消回调接口<br>
     * 在任务取消时被回调，
     * 运行于UI线程
     *
     * @param onWorkCanceledListener 监听器对象
     *
     * @return 当前任务实例
     */
    public final StandardWorkModel<Parameters, DataModel> setOnWorkCanceledListener
    (OnWorkCanceledListener<Parameters> onWorkCanceledListener) {
        return setOnWorkCanceledListener(true , onWorkCanceledListener);
    }

    /**
     * 设置任务的取消回调接口<br>
     * 在任务取消时被回调，
     * 并设置是否在当前线程执行
     *
     * @param isUiThread             指示是否在UI线程回调，
     *                               true表示在UI线程回调，
     *                               false表示在原线程回调
     *                               (如果是{@link #execute(Object[])}方式启动，则在执行任务的线程中执行，<br>
     *                               如果是{@link #beginExecute(Object[])}方式启动，则可能在网络请求线程被执行，<br>
     *                               也可能取消速度较快，网络请求还未启动，此时会在执行任务的线程中执行)
     *                               默认为true
     * @param onWorkCanceledListener 监听器对象
     *
     * @return 当前任务实例
     */
    public final StandardWorkModel<Parameters, DataModel> setOnWorkCanceledListener(boolean isUiThread , OnWorkCanceledListener<Parameters> onWorkCanceledListener) {
        this.onWorkCanceledListener = onWorkCanceledListener;
        this.isCancelUiThread = isUiThread;
        return this;
    }

    /**
     * 设置任务的请求重试次数<br>
     * 任务实际请求最少一次
     *
     * @param times 重试次数，默认为0，表示不重试即只执行一次请求。例如设为5则总共执行6次请求
     *
     * @return 当前任务实例
     */
    public final StandardWorkModel<Parameters, DataModel> setRetryTimes(int times) {
        this.retryTimes = times;
        return this;
    }

    @SafeVarargs
    @Override
    public final Observable<DataModel> observable(final Parameters... parameters) {
        return Observable.create(e -> {

            // 设置需要任务监听
            e.setCancellable(StandardWorkModel.this::cancel);

            setOnWorkFinishListener(false , e::onNext).beginExecute(parameters);
        });
    }

    @SafeVarargs
    @Override
    public final Maybe<DataModel> maybe(final Parameters... parameters) {
        return Maybe.create(maybeEmitter -> {
            // 设置需要任务监听
            maybeEmitter.setCancellable(StandardWorkModel.this::cancel);

            setOnWorkFinishListener(false , data -> {
                if (data.isSuccess()) {
                    maybeEmitter.onSuccess(data);
                } else {
                    maybeEmitter.onComplete();
                }
            }).beginExecute(parameters);
        });
    }

    @SafeVarargs
    @Override
    public final Single<DataModel> single(final Parameters... parameters) {
        return Single.create(singleEmitter -> {
            // 设置需要任务监听
            singleEmitter.setCancellable(StandardWorkModel.this::cancel);

            setOnWorkFinishListener(false , data -> {
                if (data.isSuccess()) {
                    singleEmitter.onSuccess(data);
                } else {
                    singleEmitter.onError(new Throwable(data.getMessage()));
                }
            }).beginExecute(parameters);
        });
    }

    @SafeVarargs
    @Override
    public final Completable completable(final Parameters... parameters) {
        return Completable.create(completableEmitter -> {
            // 设置需要任务监听
            completableEmitter.setCancellable(StandardWorkModel.this::cancel);

            setOnWorkFinishListener(false , data -> {
                if (data.isSuccess()) {
                    completableEmitter.onComplete();
                } else {
                    completableEmitter.onError(new Throwable(data.getMessage()));
                }
            }).beginExecute(parameters);
        });
    }
}
