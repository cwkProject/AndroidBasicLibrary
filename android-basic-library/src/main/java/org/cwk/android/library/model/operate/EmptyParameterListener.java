package org.cwk.android.library.model.operate;

/**
 * 无参数的监听器，
 * 用于执行无传入参数的特定回调任务的接口
 *
 * @author 超悟空
 * @version 1.0 2015/4/20
 * @since 1.0
 */
public abstract class EmptyParameterListener implements DataChangeListener<Void> {

    @Override
    public final void onDataChange(Void data) {
        onInvoke();
    }

    /**
     * 执行回调
     */
    public final void invoke() {
        onDataChange(null);
    }

    /**
     * 执行回调操作
     */
    public abstract void onInvoke();
}
