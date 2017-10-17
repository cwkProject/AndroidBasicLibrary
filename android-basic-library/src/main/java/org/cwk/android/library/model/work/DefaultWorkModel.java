package org.cwk.android.library.model.work;

import android.support.annotation.NonNull;
import android.util.Log;

import org.cwk.android.library.annotation.Delete;
import org.cwk.android.library.annotation.Download;
import org.cwk.android.library.annotation.Get;
import org.cwk.android.library.annotation.Post;
import org.cwk.android.library.annotation.Put;
import org.cwk.android.library.annotation.Upload;
import org.cwk.android.library.annotation.UploadStream;
import org.cwk.android.library.global.Global;
import org.cwk.android.library.model.data.IDataModel;
import org.cwk.android.library.model.data.IDefaultDataModel;
import org.cwk.android.library.model.operate.AsyncExecute;
import org.cwk.android.library.model.operate.Cancelable;
import org.cwk.android.library.model.operate.CreateRxObservable;
import org.cwk.android.library.model.operate.SyncExecute;
import org.cwk.android.library.network.communication.Communication;
import org.cwk.android.library.network.factory.CommunicationBuilder;
import org.cwk.android.library.network.factory.NetworkType;
import org.cwk.android.library.network.util.AsyncCommunication;
import org.cwk.android.library.network.util.NetworkCallback;
import org.cwk.android.library.network.util.OnNetworkProgressListener;
import org.cwk.android.library.network.util.SyncCommunication;

import java.lang.reflect.Method;

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
 * 默认实现的网络任务模型基类<br>
 * 内部使用{@link IDefaultDataModel}作为默认的数据模型类，
 * 使用{@link SyncCommunication}作为同步网络请求工具，
 * 使用{@link AsyncCommunication}作为异步网络请求工具，
 * 集成RxJava构建器
 *
 * @param <Parameters>    功能所需参数类型
 * @param <Result>        结果数据类型
 * @param <DataModelType> 任务请求使用的数据模型类型
 *
 * @author 超悟空
 * @version 4.0 2017/10/17
 * @since 1.0 2014/11/2
 */
public abstract class DefaultWorkModel<Parameters, Result, DataModelType extends IDataModel>
        extends WorkProcessModel<Parameters, DataModelType> implements SyncExecute<Parameters>,
        AsyncExecute<Parameters>, Cancelable, CreateRxObservable<Parameters, DataModelType> {

    /**
     * 日志标签前缀
     */
    private static final String TAG = "DefaultWorkModel";

    /**
     * 任务完成回调接口
     */
    private OnWorkFinishListener<DataModelType> onWorkFinishListener = null;

    /**
     * 网络请求进度监听器，可用于上传和下载进度监听
     */
    private OnNetworkProgressListener onNetworkProgressListener = null;

    /**
     * 任务被取消回调接口
     */
    private OnWorkCanceledListener<Parameters> onWorkCanceledListener = null;

    /**
     * 网络请求工具
     */
    private Communication communication = null;

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
     * 任务取消状态标签
     */
    private volatile boolean cancelMark = false;

    /**
     * 标识是否异步启动任务
     */
    private boolean isAsync = true;

    @Override
    protected final boolean onDoWork() {
        if (!cancelMark) {
            Log.v(TAG, "onDoWork invoked");
            Log.v(TAG, "onDoWork task request url is " + onTaskUri());

            // 设置请求地址
            communication.setTaskName(onTaskUri());

            // 进入同步异步请求分支
            if (isAsync) {
                // 异步分支

                // 发送请求
                //noinspection unchecked
                communication.Request(mData.serialization(), new NetworkCallback() {
                    @Override
                    public void onFinish(boolean result, Object response) {
                        if (!cancelMark) {
                            // 解析响应数据
                            onParseResult(result, response);

                            // 执行后继任务
                            onStopWork();
                        }
                    }
                });

                // 表示成功发送请求，任务被受理
                return true;
            } else {
                // 同步分支

                // 发送请求
                //noinspection unchecked
                communication.request(mData.serialization());

                // 解析响应数据
                boolean success = onParseResult(communication.isSuccessful(), communication
                        .response());

                // 关闭网络
                communication.close();
                return success;
            }
        } else {
            return false;
        }
    }

    /**
     * 解析响应数据
     *
     * @param result   请求结果
     * @param response 响应数据
     *
     * @return 任务执行结果，true表示成功
     */
    private boolean onParseResult(boolean result, Object response) {
        Log.v(TAG, "onParseResult result parse start");
        // 解析数据
        //noinspection unchecked
        if (result && mData.parse(response)) {
            // 解析成功
            Log.v(TAG, "onParseResult result parse success");
            Log.v(TAG, "onParseSuccess invoked");
            // 解析成功回调
            onParseSuccess();
            if (mData.isSuccess()) {
                // 设置请求成功后返回的数据
                Log.v(TAG, "work success");
                return true;
            } else {
                // 设置请求失败后返回的数据
                Log.v(TAG, "work failed");
                return false;
            }
        } else {
            // 解析失败
            Log.v(TAG, "onParseResult result parse failed");
            Log.v(TAG, "onParseFailed invoked");
            // 解析失败回调
            onParseFailed();
            return false;
        }
    }

    @SafeVarargs
    @Override
    public final void beginExecute(Parameters... parameters) {
        Log.v(TAG, "beginExecute start");

        cancelMark = false;
        isAsync = true;

        // 是否继续执行
        boolean next = true;

        if (!cancelMark) {
            // 执行前导任务
            next = onStartWork();
        }

        if (!cancelMark && next) {
            // 执行核心任务
            if (!onDoWork()) {
                // 任务执行失败
                // 执行后继任务
                onStopWork();
            }
        }
    }

    @SafeVarargs
    @Override
    public final boolean execute(Parameters... parameters) {
        Log.v(TAG, "execute start");
        cancelMark = false;
        isAsync = false;

        // 保留执行结果
        boolean state = false;

        // 是否继续执行
        boolean next = true;

        if (!cancelMark) {
            // 执行前导任务
            next = onStartWork();
        }

        if (!cancelMark && next) {
            // 执行核心任务
            state = onDoWork();
        }

        if (!cancelMark && next) {
            // 执行后继任务
            onStopWork();
        }

        return state;
    }

    @Override
    public final void cancel() {
        Log.v(TAG, "work cancel");
        this.cancelMark = true;
        if (communication != null) {
            communication.cancel();
        }

        Log.v(TAG, "onCanceled invoked");
        onCanceled();

        if (onWorkCanceledListener != null) {
            Log.v(TAG, "onWorkCanceledListener invoked");
            if (isCancelUiThread) {
                // 发送到UI线程
                Global.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        onWorkCanceledListener.onCanceled(mParameters);
                    }
                });
            } else {
                // 发送到当前线程
                this.onWorkCanceledListener.onCanceled(mParameters);
            }
        }
    }

    @Override
    public boolean isCanceled() {
        return this.cancelMark;
    }

    @SafeVarargs
    @Override
    protected final boolean onStartWork(Parameters... parameters) {
        Log.v(TAG, "onStartWork invoked");

        boolean next = super.onStartWork(parameters);

        // 创建网络请求工具
        if (next && communication == null) {
            communication = onCreateCommunication(new CommunicationBuilder(onNetworkType())
                    .networkRefreshProgressListener(onCreateProgressListener()));
        }

        return next;
    }

    /**
     * 创建网络请求进度监听器，根据情况可能进行了包装
     *
     * @return 网络请求进度监听器
     */
    protected OnNetworkProgressListener onCreateProgressListener() {
        if (onNetworkProgressListener != null) {
            // 开始绑定
            Log.v(TAG, "set ProgressListener");

            if (isProgressUiThread) {
                // 发送到UI线程
                return new OnNetworkProgressListener() {
                    @Override
                    public void onRefreshProgress(final long current, final long total, final
                    boolean done) {
                        Global.getUiHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                onNetworkProgressListener.onRefreshProgress(current, total, done);
                            }
                        });
                    }
                };
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
    protected final void onStopWork() {
        Log.v(TAG, "onStopWork invoked");
        if (!cancelMark) {
            // 不同结果的后继执行
            if (mData.isSuccess()) {
                Log.v(TAG, "onSuccessResult invoke");
                onSuccess();
            } else {
                Log.v(TAG, "onFailed invoke");
                onFailed();
            }
        }

        // 如果设置了回调接口则执行回调方法
        if (!cancelMark && isAsync && this.onWorkFinishListener != null) {
            Log.v(TAG, "onWorkFinishListener invoked");
            if (isEndUiThread) {
                // 发送到UI线程
                Global.getUiHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        onWorkFinishListener.onFinish(mData);
                    }
                });
            } else {
                // 发送到当前线程
                this.onWorkFinishListener.onFinish(mData);
            }
        }

        if (!cancelMark) {
            // 最后执行
            Log.v(TAG, "onFinish invoke");
            onFinish();
        }

        Log.v(TAG, "work end");
    }

    /**
     * 本次任务执行成功后执行，
     * 即设置请求结果和返回数据之后，并且在回调接口之前执行此函数，
     * 该方法在{@link #onFinish()}之前被调用
     */
    protected void onSuccess() {

    }

    /**
     * 本次任务执行失败后执行，
     * 即设置请求结果和返回数据之后，并且在回调接口之前执行此函数，
     * 该方法在{@link #onFinish()}之前被调用
     */
    protected void onFailed() {

    }

    /**
     * 最后执行的一个方法，
     * 即设置请求结果和返回数据之后，并且在回调任务发送后才执行此函数
     */
    protected void onFinish() {
    }

    /**
     * 设置任务请求地址，同时标记请求协议，默认使用http get发送请求<br>
     * 或使用{@link NetworkType}中支持的其他请求类型，使用时标记同名注解。<br>
     * 如果项目使用混淆，请加入<br>
     * {@code -keepclassmembers class * extends org.cwk.android.library.model.work
     * .DefaultWorkModel {
     * protected ** onTaskUri();
     * }
     * }
     *
     * @return 地址字符串
     */
    @Get
    protected abstract String onTaskUri();

    /**
     * 设置网络请求类型<br>
     * 用于{@link CommunicationBuilder#CommunicationBuilder(int)}生产网络请求实例，
     * 默认为{@link NetworkType#GET}
     *
     * @return 网络请求类型枚举
     */
    private int onNetworkType() {
        Class<?> thisClass = this.getClass();

        Method method = null;

        while (method == null) {
            for (Method name : thisClass.getDeclaredMethods()) {

                if (name.getName().equals("onTaskUri") && name.getParameterTypes().length == 0) {
                    method = name;
                    break;
                }
            }

            thisClass = thisClass.getSuperclass();
        }

        if (method.isAnnotationPresent(Get.class)) {
            return NetworkType.GET;
        }
        if (method.isAnnotationPresent(Post.class)) {
            return NetworkType.POST;
        }
        if (method.isAnnotationPresent(Download.class)) {
            return NetworkType.DOWNLOAD;
        }
        if (method.isAnnotationPresent(Upload.class)) {
            return NetworkType.UPLOAD;
        }
        if (method.isAnnotationPresent(Put.class)) {
            return NetworkType.PUT;
        }
        if (method.isAnnotationPresent(Delete.class)) {
            return NetworkType.DELETE;
        }
        if (method.isAnnotationPresent(UploadStream.class)) {
            return NetworkType.UPLOAD_STREAM;
        }

        return NetworkType.GET;
    }

    /**
     * 创建网络请求工具<br>
     * 用于发送网络请求，
     * 使用{@link CommunicationBuilder}工具进行创建，
     * 如果需要配置网络请求参数请重写此方法
     *
     * @param builder 网络访问工具构建器
     *
     * @return 网络请求工具实例，调用{@link CommunicationBuilder#build()}创建
     */
    protected Communication onCreateCommunication(CommunicationBuilder builder) {
        return builder.build();
    }

    /**
     * 服务器响应数据解析成功后调用，
     * 即在{@link IDataModel#parse(Object)}返回true时调用
     */
    protected void onParseSuccess() {
    }

    /**
     * 服务器响应数据解析失败后调用，
     * 即在{@link IDataModel#parse(Object)}返回false时调用
     */
    protected void onParseFailed() {
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
    public final DefaultWorkModel<Parameters, Result, DataModelType> setOnWorkFinishListener
    (OnWorkFinishListener<DataModelType> onWorkFinishListener) {
        return setOnWorkFinishListener(true, onWorkFinishListener);
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
    public final DefaultWorkModel<Parameters, Result, DataModelType> setOnNetworkProgressListener
    (OnNetworkProgressListener onNetworkProgressListener) {
        return setOnNetworkProgressListener(true, onNetworkProgressListener);
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
    public final DefaultWorkModel<Parameters, Result, DataModelType> setOnWorkFinishListener
    (boolean isUiThread, OnWorkFinishListener<DataModelType> onWorkFinishListener) {
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
    public final DefaultWorkModel<Parameters, Result, DataModelType> setOnNetworkProgressListener
    (boolean isUiThread, OnNetworkProgressListener onNetworkProgressListener) {
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
    public final DefaultWorkModel<Parameters, Result, DataModelType> setOnWorkCanceledListener
    (OnWorkCanceledListener<Parameters> onWorkCanceledListener) {
        return setOnWorkCanceledListener(true, onWorkCanceledListener);
    }

    /**
     * 设置任务的取消回调接口<br>
     * 在任务取消时被回调，
     * 并设置是否在当前线程执行
     *
     * @param isUiThread             指示是否在UI线程回调，
     *                               true表示在UI线程回调，
     *                               false表示在当前线程（执行{@link #cancel()}的线程）回调，
     *                               默认为true
     * @param onWorkCanceledListener 监听器对象
     *
     * @return 当前任务实例
     */
    public final DefaultWorkModel<Parameters, Result, DataModelType> setOnWorkCanceledListener
    (boolean isUiThread, OnWorkCanceledListener<Parameters> onWorkCanceledListener) {
        this.onWorkCanceledListener = onWorkCanceledListener;
        this.isCancelUiThread = isUiThread;
        return this;
    }

    @SafeVarargs
    @Override
    public final Observable<DataModelType> observable(final Parameters... parameters) {
        return Observable.create(new ObservableOnSubscribe<DataModelType>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<DataModelType> e) throws
                    Exception {

                // 设置需要任务监听
                e.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        DefaultWorkModel.this.cancel();
                    }
                });

                setOnWorkFinishListener(false, new OnWorkFinishListener<DataModelType>() {
                    @Override
                    public void onFinish(DataModelType data) {
                        e.onNext(data);
                    }
                }).beginExecute(parameters);
            }
        });
    }

    @SafeVarargs
    @Override
    public final Maybe<DataModelType> maybe(final Parameters... parameters) {
        return Maybe.create(new MaybeOnSubscribe<DataModelType>() {
            @Override
            public void subscribe(final MaybeEmitter<DataModelType> maybeEmitter) throws Exception {
                // 设置需要任务监听
                maybeEmitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        DefaultWorkModel.this.cancel();
                    }
                });

                setOnWorkFinishListener(false, new OnWorkFinishListener<DataModelType>() {
                    @Override
                    public void onFinish(DataModelType data) {
                        if (data.isSuccess()) {
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
    public final Single<DataModelType> single(final Parameters... parameters) {
        return Single.create(new SingleOnSubscribe<DataModelType>() {
            @Override
            public void subscribe(final SingleEmitter<DataModelType> singleEmitter) throws
                    Exception {

                // 设置需要任务监听
                singleEmitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        DefaultWorkModel.this.cancel();
                    }
                });

                setOnWorkFinishListener(false, new OnWorkFinishListener<DataModelType>() {
                    @Override
                    public void onFinish(DataModelType data) {
                        if (data.isSuccess()) {
                            singleEmitter.onSuccess(data);
                        } else {
                            singleEmitter.onError(new Throwable(data.getMessage()));
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

                // 设置需要任务监听
                completableEmitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        DefaultWorkModel.this.cancel();
                    }
                });

                setOnWorkFinishListener(false, new OnWorkFinishListener<DataModelType>() {
                    @Override
                    public void onFinish(DataModelType data) {
                        if (data.isSuccess()) {
                            completableEmitter.onComplete();
                        } else {
                            completableEmitter.onError(new Throwable(data.getMessage()));
                        }
                    }
                }).beginExecute(parameters);
            }
        });
    }
}
