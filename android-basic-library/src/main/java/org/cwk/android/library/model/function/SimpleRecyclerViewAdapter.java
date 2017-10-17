package org.cwk.android.library.model.function;

import android.support.v7.widget.RecyclerView;

import org.cwk.android.library.model.operate.BasicRecyclerViewAdapterFunction;
import org.cwk.android.library.model.operate.OnRecyclerViewItemListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 增加基础功能实现的{@link android.support.v7.widget.RecyclerView.Adapter}实现
 *
 * @param <SourceType>     数据源类型
 * @param <ViewHolderType> Item控件管理器类型
 *
 * @author 超悟空
 * @version 1.0 2017/2/10
 * @since 1.0 2017/2/10
 **/
public abstract class SimpleRecyclerViewAdapter<SourceType, ViewHolderType extends RecyclerView
        .ViewHolder> extends RecyclerView.Adapter<ViewHolderType> implements
        BasicRecyclerViewAdapterFunction<SourceType> {

    /**
     * 数据源
     */
    protected List<SourceType> dataList = new ArrayList<>();

    /**
     * Item点击事件监听器，需要子类在{@link #onCreateViewHolder}中手动绑定
     */
    protected OnRecyclerViewItemListener<ViewHolderType, SourceType> onItemClickListener = null;

    /**
     * Item长按事件监听器，需要子类在{@link #onCreateViewHolder}中手动绑定
     */
    protected OnRecyclerViewItemListener<ViewHolderType, SourceType> onItemLongClickListener = null;

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener Item点击事件监听器
     */
    public void setOnItemClickListener(OnRecyclerViewItemListener<ViewHolderType, SourceType>
                                               onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置Item长按事件
     *
     * @param onItemLongClickListener Item长按事件监听器
     */
    public void setOnItemLongClickListener(OnRecyclerViewItemListener<ViewHolderType, SourceType>
                                                   onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public void add(SourceType data) {
        if (data != null) {
            dataList.add(data);
            notifyItemInserted(dataList.size() - 1);
        }
    }

    @Override
    public void add(int position, SourceType data) {
        if (position >= 0 && position <= dataList.size() && data != null) {
            dataList.add(position, data);
            notifyItemInserted(position);
        }
    }

    @Override
    public void add(List<SourceType> data) {
        if (data != null && !data.isEmpty()) {
            dataList.addAll(data);
            notifyItemRangeInserted(dataList.size() - 1, data.size());
        }
    }

    @Override
    public void add(int position, List<SourceType> data) {
        if (position >= 0 && position <= dataList.size() && data != null && !data.isEmpty()) {
            dataList.addAll(position, data);
            notifyItemRangeInserted(position, data.size());
        }
    }

    @Override
    public SourceType remove(int position) {
        SourceType data = null;
        if (position >= 0 && position < dataList.size()) {
            data = dataList.remove(position);
            notifyItemRemoved(position);
        }
        return data;
    }

    @Override
    public List<SourceType> remove(int start, int count) {
        List<SourceType> list = null;

        if (start >= 0 && count > 0 && start + count <= dataList.size()) {
            list = new ArrayList<>();

            for (int i = start; i < start + count; i++) {
                list.add(dataList.remove(start));
            }

            notifyItemRangeRemoved(start, count);
        }

        return list;
    }

    @Override
    public SourceType change(int position, SourceType data) {
        SourceType oldData = null;

        if (position >= 0 && position <= dataList.size() && data != null) {
            oldData = dataList.set(position, data);

            notifyItemChanged(position);
        }

        return oldData;
    }

    @Override
    public List<SourceType> change(int position, List<SourceType> data) {

        List<SourceType> list = null;

        if (data != null && !data.isEmpty() && position >= 0 && position + data.size() <=
                dataList.size()) {
            list = new ArrayList<>();

            for (int i = position; i < position + data.size(); i++) {
                list.add(dataList.set(i, data.get(i - position)));
            }

            notifyItemRangeChanged(position, data.size());
        }

        return list;

    }

    @Override
    public void swap(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && toPosition >= 0 && fromPosition < dataList.size() && toPosition
                < dataList.size()) {
            Collections.swap(dataList, fromPosition, toPosition);
            notifyItemChanged(fromPosition);
            notifyItemChanged(toPosition);
        }
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && toPosition >= 0 && fromPosition < dataList.size() && toPosition
                < dataList.size()) {
            dataList.add(toPosition, dataList.remove(fromPosition));
            notifyItemMoved(fromPosition, toPosition);
        }
    }

    @Override
    public void clear() {
        int count = dataList.size();
        dataList.clear();
        notifyItemRangeRemoved(0, count);
    }

    @Override
    public void clear(int position) {
        if (position >= 0 && position < dataList.size()) {
            int count = dataList.size();

            for (int i = 0; i < count - position; i++) {
                dataList.remove(position);
            }

            notifyItemRangeRemoved(position, count - position);
        }
    }

    @Override
    public List<SourceType> getDataList() {
        return dataList;
    }

    @Override
    public SourceType getData(int position) {
        if (position >= 0 && position < dataList.size()) {
            return dataList.get(position);
        } else {
            return null;
        }
    }
}
