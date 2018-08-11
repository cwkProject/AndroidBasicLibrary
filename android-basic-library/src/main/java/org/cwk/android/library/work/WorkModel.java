package org.cwk.android.library.work;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.util.Log;

import org.cwk.android.library.data.DataModelHandle;
import org.cwk.android.library.data.WorkDataModel;
import org.cwk.android.library.network.communication.ICommunication;
import org.cwk.android.library.network.factory.CommunicationBuilder;
import org.cwk.android.library.network.factory.NetworkType;

/**
 * 任务流程的基本模型
 *
 * @param <Parameters> 任务所需参数类型
 * @param <DataModel>  协议数据类型
 *
 * @author 超悟空
 * @version 1.0 2015/10/29
 * @since 1.0
 */
public abstract class WorkModel<Parameters, DataModel extends WorkDataModel> implements
        SyncExecute<Parameters, DataModel>, AsyncExecute<Parameters>, Cancelable {

    /**
     * 日志标签前缀
     */
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 网络请求工具
     */
    private ICommunication communication = null;

    /**
     * 协议数据处理器
     */
    protected DataModel mData = null;

    /**
     * 参数
     */
    protected Parameters[] mParameters = null;

    /**
     * 任务取消状态标签
     */
    protected volatile boolean cancelMark = false;

    /**
     * 标识是否异步启动任务
     */
    protected boolean isAsync = true;

    @SafeVarargs
    @Override
    public final void beginExecute(Parameters... parameters) {
        Log.v(TAG , "work beginExecute start");

        cancelMark = false;
        isAsync = true;

        // 是否继续执行
        boolean next = true;

        if (!cancelMark) {
            // 执行前导任务
            next = onStartWork(parameters);
        }

        if (!cancelMark && next) {
            // 执行核心任务
            if (!onDoWork()) {
                // 任务执行失败
                // 执行后继任务
                onStopWork();

                if (!cancelMark) {
                    // 最后执行
                    Log.v(TAG , "onFinish invoke");
                    onFinish();
                }

                Log.v(TAG , "work end");
            }
        }
    }

    @SafeVarargs
    @Override
    public final DataModel execute(Parameters... parameters) {
        Log.v(TAG , "work execute start");
        cancelMark = false;
        isAsync = false;

        // 是否继续执行
        boolean next = true;

        if (!cancelMark) {
            // 执行前导任务
            next = onStartWork(parameters);
        }

        if (!cancelMark && next) {
            // 执行核心任务
            onDoWork();
        }

        if (!cancelMark && next) {
            // 执行后继任务
            onStopWork();
        }

        if (!cancelMark && next) {
            // 最后执行
            Log.v(TAG , "onFinish invoke");
            onFinish();
        }

        Log.v(TAG , "work end");

        return mData;
    }

    @CallSuper
    @Override
    public void cancel() {
        Log.v(TAG , "cancel");
        this.cancelMark = true;
        if (communication != null) {
            communication.cancel();
        }

        Log.v(TAG , "onCanceled invoked");
        onCanceled();
    }

    @Override
    public boolean isCanceled() {
        return this.cancelMark;
    }

    /**
     * 任务启动前置方法<br>
     * 在{@link #onDoWork()}之前被调用，
     * 运行于当前线程
     *
     * @param parameters 任务传入参数
     *
     * @return 是否继续执行任务，true表示继续
     */
    @SuppressWarnings("unchecked")
    @CallSuper
    protected boolean onStartWork(Parameters... parameters) {
        // 校验参数
        if (!onCheckParameters(parameters)) {
            // 数据异常
            Log.d(TAG , "onStartWork parameters is error");
            // 执行异常回调
            onParameterError(parameters);

            return false;
        }

        mParameters = parameters;

        // 创建数据模型
        mData = onCreateDataModel();

        DataModelHandle.setParams(mData , mParameters);

        // 创建网络请求工具
        if (communication == null) {
            communication = onInterceptCreateCommunication();

            if (communication == null) {

                CommunicationBuilder builder = new CommunicationBuilder(TAG , this.getClass());

                onCreateCommunication(builder);

                communication = builder.build();
            }
        }

        return true;
    }

    /**
     * 任务逻辑核心方法<br>
     * 任务主要逻辑应该在该方法中被实现，
     * 并且方法返回任务执行结果。
     *
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    private boolean onDoWork() {
        if (!cancelMark) {
            Log.v(TAG , "onDoWork invoked");

            // 设置请求地址
            communication.setTaskName(onTaskUri());

            // 进入同步异步请求分支
            if (isAsync) {
                // 异步分支

                // 发送请求
                //noinspection unchecked
                communication.request(DataModelHandle.serialization(mData) , (result , code ,
                                                                              response) -> {
                    if (!cancelMark) {
                        // 解析响应数据
                        onParseResult(result , code , response);

                        // 执行后继任务
                        onStopWork();
                    }
                });

                // 表示成功发送请求，任务被受理
                return true;
            } else {
                // 同步分支

                // 发送请求
                //noinspection unchecked
                communication.request(DataModelHandle.serialization(mData));

                // 解析响应数据
                boolean success = onParseResult(communication.isSuccessful() , communication.code
                        () , communication.response());

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
     * @param code     http响应码
     * @param response 响应数据
     *
     * @return 任务执行结果，true表示成功
     */
    private boolean onParseResult(boolean result , int code , Object response) {
        Log.v(TAG , "onParseResult result parse start");
        // 解析数据
        //noinspection unchecked
        if (result && DataModelHandle.parse(mData , code , response)) {
            // 解析成功
            Log.v(TAG , "onParseResult result parse success");
            Log.v(TAG , "onParseSuccess invoked");
            // 解析成功回调
            onParseSuccess();
            if (mData.isSuccess()) {
                // 设置请求成功后返回的数据
                Log.v(TAG , "work success");
                return true;
            } else {
                // 设置请求失败后返回的数据
                Log.v(TAG , "work failed");
                return false;
            }
        } else {
            // 解析失败
            Log.v(TAG , "onParseResult result parse failed");
            Log.v(TAG , "onParseFailed invoked");
            // 解析失败回调
            onParseFailed();
            return false;
        }
    }

    /**
     * 任务完成后置方法<br>
     * 在{@link #onDoWork()}之后被调用
     */
    @CallSuper
    protected void onStopWork() {
        Log.v(TAG , "onStopWork invoked");
        if (!cancelMark) {
            // 不同结果的后继执行
            if (mData.isSuccess()) {
                Log.v(TAG , "onSuccessResult invoke");
                onSuccess();
            } else {
                Log.v(TAG , "onFailed invoke");
                onFailed();
            }
        }
    }

    /**
     * 任务被取消，在执行取消操作的线程中执行
     */
    protected void onCanceled() {
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
     * {@code -keepclassmembers class * extends org.cwk.android.library.work.WorkModel {
     * protected ** onTaskUri();
     * }
     * }
     *
     * @return 地址字符串
     */
    protected abstract String onTaskUri();

    /**
     * 创建数据模型对象并填充参数
     *
     * @return 参数设置完毕后的数据模型对象
     */
    protected abstract DataModel onCreateDataModel();

    /**
     * 参数合法性检测<br>
     * 用于检测传入参数是否合法，
     * 需要子类重写检测规则<br>
     * 检测成功任务才会被正常执行，
     * 如果检测失败则{@link #onParameterError(Object[])}会被调用，
     * 后续其他生命周期将不会再执行，任务会直接被取消，不会有任何返回值
     *
     * @param parameters 任务传入参数
     *
     * @return 检测结果，合法返回true，非法返回false，默认为true
     */
    @SuppressWarnings("unchecked")
    protected boolean onCheckParameters(Parameters... parameters) {
        return true;
    }

    /**
     * 参数检测不合法时调用，
     * 即{@link #onCheckParameters(Object[])}返回false时被调用，
     * 且后续任务不再执行
     *
     * @param parameters 任务传入参数
     */
    @SuppressWarnings("unchecked")
    protected void onParameterError(Parameters... parameters) {
    }

    /**
     * 拦截创建网络请求工具<br>
     * 用于创建完全自定义实现的网络请求工具。
     * 此方法如果返回非空实例则默认的创建方法{@link #onCreateCommunication(CommunicationBuilder)}不再执行，
     * {@link #onTaskUri()}设置的请求类型注解也不再生效
     *
     * @return 网络请求工具实例
     */
    protected ICommunication onInterceptCreateCommunication() {
        return null;
    }

    /**
     * 创建网络请求工具<br>
     * 用于发送网络请求，
     * 使用{@link CommunicationBuilder}工具进行创建，
     * 如果需要配置网络请求参数请重写此方法
     *
     * @param builder 网络访问工具构建器，用于继续设置请求属性
     */
    protected void onCreateCommunication(@NonNull CommunicationBuilder builder) {
    }

    /**
     * 服务器响应数据解析成功后调用，
     * 即在{@link WorkDataModel#parse(int , Object)}返回true时调用
     */
    protected void onParseSuccess() {
    }

    /**
     * 服务器响应数据解析失败后调用，
     * 即在{@link WorkDataModel#parse(int , Object)}返回false时调用
     */
    protected void onParseFailed() {
    }
}
