package org.cwk.android.library.architecture.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * {@link MultipleRecyclerViewAdapter}的分组item事件
 *
 * @param <ViewHolderType> 绑定的自定义布局管理器类型
 * @param <DataSourceType> 绑定的数据源类型
 *
 * @author 超悟空
 * @version 1.0 2017/11/14
 * @since 1.0 2017/11/14
 **/
public interface OnRecyclerViewGroupItemListener<ViewHolderType extends RecyclerView.ViewHolder,
        DataSourceType> {

    /**
     * 事件响应
     *
     * @param holder     点击位置的布局管理工具
     * @param dataSource 当前适配器绑定的数据集
     * @param position   当前分组中的索引
     */
    void onInvoke(@NonNull ViewHolderType holder , DataSourceType dataSource , int position);
}
