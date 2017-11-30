package org.cwk.android.library.model.operate;

import org.cwk.android.library.model.function.MultipleRecyclerViewAdapter;

/**
 * {@link MultipleRecyclerViewAdapter}的数据装配事务操作
 *
 * @author 超悟空
 * @version 1.0 2017/11/30
 * @since 1.0 2017/11/30
 **/
public interface OnRecyclerAdapterTransaction {

    /**
     * 当前是否是事务操作
     *
     * @return true表示正在执行事务
     */
    boolean isTransaction();

    /**
     * 开始数据变更事务，结束后需要调用{@link #commit()}提交界面刷新，使用事务刷新数据会丢失界面刷新动画
     */
    void beginTransaction();

    /**
     * 结束数据变更事务，通知界面刷新，与{@link #beginTransaction()}配合使用
     */
    void commit();
}
