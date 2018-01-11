package org.cwk.android.library.model.work;

import android.support.annotation.CallSuper;
import android.util.Log;

import org.cwk.android.library.model.data.IDataModel;

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
public abstract class WorkProcessModel<Parameters, DataModel extends IDataModel> {

    /**
     * 日志标签前缀
     */
    protected final String TAG = this.getClass().getSimpleName();

    /**
     * 协议数据处理器
     */
    protected DataModel mData = null;

    /**
     * 参数
     */
    protected Parameters[] mParameters = null;

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
            Log.d(TAG, "onStartWork parameters is error");
            // 执行异常回调
            onParameterError(parameters);

            return false;
        }

        mParameters = parameters;

        // 创建数据模型
        mData = onCreateDataModel(parameters);

        return true;
    }

    /**
     * 创建数据模型对象并填充参数
     *
     * @param parameters 传入参数
     *
     * @return 参数设置完毕后的数据模型对象
     */
    @SuppressWarnings("unchecked")
    protected abstract DataModel onCreateDataModel(Parameters... parameters);

    /**
     * 参数合法性检测<br>
     * 用于检测传入参数是否合法，
     * 需要子类重写检测规则<br>
     * 检测成功后续任务才会被正常执行，
     * 如果检测失败则{@link #onParameterError(Object[])}会被调用
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
     * 任务逻辑核心方法<br>
     * 任务主要逻辑应该在该方法中被实现，
     * 并且方法返回任务执行结果。
     *
     * @return 执行结果
     */
    @SuppressWarnings("unchecked")
    protected abstract boolean onDoWork();

    /**
     * 任务完成后置方法<br>
     * 在{@link #onDoWork()}之后被调用
     */
    protected void onStopWork() {
    }

    /**
     * 任务被取消，在执行取消操作的线程中执行
     */
    protected void onCanceled() {

    }
}
