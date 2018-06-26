package org.cwk.android.library.architecture.recycler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * RecyclerView的item标准事件
 *
 * @param <ViewHolderType> 绑定的自定义布局管理器类型
 * @param <DataSourceType> 绑定的数据源类型
 *
 * @author 超悟空
 * @version 1.0 2015/3/2
 * @since 1.0
 */
public interface OnRecyclerViewItemListener<ViewHolderType extends RecyclerView.ViewHolder,
        DataSourceType> {
    /**
     * 事件响应
     *
     * @param holder     点击位置的布局管理工具
     * @param dataSource 当前适配器绑定的数据集
     */
    void onInvoke(@NonNull ViewHolderType holder,DataSourceType dataSource);
}
