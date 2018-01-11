package org.cwk.android.library.model.function;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.cwk.android.library.model.operate.BasicRecyclerViewAdapterFunction;
import org.cwk.android.library.model.operate.OnGroupPositionToAdapterPosition;
import org.cwk.android.library.model.operate.OnRecyclerAdapterTransaction;
import org.cwk.android.library.model.operate.OnRecyclerViewGroupItemListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@link android.support.v7.widget.RecyclerView.ViewHolder}和数据集管理器
 *
 * @author 超悟空
 * @version 1.0 2017/11/12
 * @since 1.0
 */
public abstract class RecyclerViewHolderManager<SourceType, ViewHolderType extends RecyclerView
        .ViewHolder> implements BasicRecyclerViewAdapterFunction<SourceType> {

    /**
     * 当前数据集在依赖适配器中的分组索引
     */
    private int groupIndex = 0;

    /**
     * 数据源
     */
    protected final List<SourceType> dataList = new ArrayList<>();

    /**
     * 依赖的适配器
     */
    protected final RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;

    /**
     * 索引转换器
     */
    private final OnGroupPositionToAdapterPosition convertUnit;

    /**
     * 事务控制器
     */
    private final OnRecyclerAdapterTransaction transaction;

    /**
     * Item点击事件监听器
     */
    private OnRecyclerViewGroupItemListener<ViewHolderType, SourceType> onItemClickListener = null;

    /**
     * Item长按事件监听器
     */
    private OnRecyclerViewGroupItemListener<ViewHolderType, SourceType> onItemLongClickListener =
            null;

    /**
     * 构造函数
     *
     * @param adapter 依赖的适配器
     */
    public RecyclerViewHolderManager(RecyclerView.Adapter<RecyclerView.ViewHolder> adapter) {
        this.adapter = adapter;
        if (adapter instanceof OnGroupPositionToAdapterPosition) {
            this.convertUnit = (OnGroupPositionToAdapterPosition) adapter;
        } else {
            this.convertUnit = null;
        }

        if (adapter instanceof OnRecyclerAdapterTransaction) {
            this.transaction = (OnRecyclerAdapterTransaction) adapter;
        } else {
            this.transaction = null;
        }
    }

    /**
     * 设置Item点击事件
     *
     * @param onItemClickListener Item点击事件监听器
     */
    public void setOnItemClickListener(OnRecyclerViewGroupItemListener<ViewHolderType,
            SourceType> onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 设置Item长按事件
     *
     * @param onItemLongClickListener Item长按事件监听器
     */
    public void setOnItemLongClickListener(OnRecyclerViewGroupItemListener<ViewHolderType,
            SourceType> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    /**
     * 设置分组索引
     *
     * @param index 索引编号
     */
    final void setGroupIndex(int index) {
        this.groupIndex = index;
    }

    /**
     * 绑定数据到控件
     *
     * @param holder   控件管理器
     * @param position 当前数据组中的位置
     */
    final void bindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //noinspection unchecked
        onBindViewHolder((ViewHolderType) holder, position);
    }

    /**
     * 创建控件管理器
     *
     * @param parent 列表布局
     *
     * @return 控件管理器
     */
    final ViewHolderType createViewHolder(ViewGroup parent) {

        final ViewHolderType holder = onCreateViewHolder(parent);

        onBindListener(holder);

        return holder;
    }

    /**
     * 绑定事件监听器
     *
     * @param holder 控件管理器
     */
    protected void onBindListener(ViewHolderType holder) {
        if (onItemClickListener != null && convertUnit != null) {
            holder.itemView.setOnClickListener(v -> {
                int position = holder.getAdapterPosition();

                if (position > -1) {
                    position = convertUnit.convertToGroupPosition(groupIndex, position);

                    onItemClickListener.onInvoke(holder, dataList.get(position), position);
                }
            });
        }

        if (onItemLongClickListener != null && convertUnit != null) {
            holder.itemView.setOnLongClickListener(v -> {
                int position = holder.getAdapterPosition();

                if (position > -1) {
                    position = convertUnit.convertToGroupPosition(groupIndex, position);

                    onItemLongClickListener.onInvoke(holder, dataList.get(position), position);
                }

                return true;
            });
        }
    }

    /**
     * 获取当前数据集总数
     *
     * @return 数据总数
     */
    public final int getCount() {
        return dataList.size();
    }

    /**
     * 创建控件管理器
     *
     * @param parent 列表布局
     *
     * @return 控件管理器
     */
    public abstract ViewHolderType onCreateViewHolder(@NonNull ViewGroup parent);

    /**
     * 绑定数据到控件
     *
     * @param holder   控件管理器
     * @param position 当前数据组中的相对索引，与{@link RecyclerView.ViewHolder#getAdapterPosition()}不同
     */
    protected abstract void onBindViewHolder(@NonNull ViewHolderType holder, int position);

    /**
     * 转换本组位置到适配器位置
     *
     * @param position 当前组中的位置
     *
     * @return 适配器中的位置
     */
    protected int convert(int position) {
        //noinspection ConstantConditions
        return convertUnit.convertToAdapterPosition(groupIndex, position);
    }

    /**
     * 是否需要通知界面刷新
     *
     * @return true表示需要通知刷新，false表示不需要通知刷新
     */
    protected boolean isNeedNotify() {
        return (transaction == null || !transaction.isTransaction()) && convertUnit != null;
    }

    /**
     * 通知指定位置的Item刷新
     *
     * @param position 索引
     */
    public void notifyItemChanged(int position) {
        if (position >= 0 && position <= dataList.size() && convertUnit != null) {
            adapter.notifyItemInserted(convert(position));
        }
    }

    @Override
    public void add(SourceType data) {
        if (data != null) {
            dataList.add(data);
            if (isNeedNotify()) {
                adapter.notifyItemInserted(convert(dataList.size()) - 1);
            }
        }
    }

    @Override
    public void add(int position, SourceType data) {
        if (position >= 0 && position <= dataList.size() && data != null) {
            dataList.add(position, data);
            if (isNeedNotify()) {
                adapter.notifyItemInserted(convert(position));
            }
        }
    }

    @Override
    public void add(List<SourceType> data) {
        if (data != null && !data.isEmpty()) {
            dataList.addAll(data);
            if (isNeedNotify()) {
                adapter.notifyItemRangeInserted(convert(dataList.size()) - 1, data.size());
            }
        }
    }

    @Override
    public void add(int position, List<SourceType> data) {
        if (position >= 0 && position <= dataList.size() && data != null && !data.isEmpty()) {
            dataList.addAll(position, data);
            if (isNeedNotify()) {
                adapter.notifyItemRangeInserted(convert(position), data.size());
            }
        }
    }

    @Override
    public SourceType remove(int position) {
        SourceType data = null;
        if (position >= 0 && position < dataList.size()) {
            data = dataList.remove(position);
            if (isNeedNotify()) {
                adapter.notifyItemRemoved(convert(position));
            }
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

            if (isNeedNotify()) {
                adapter.notifyItemRangeRemoved(convert(start), count);
            }
        }

        return list;
    }

    @Override
    public SourceType change(int position, SourceType data) {
        SourceType oldData = null;

        if (position >= 0 && position <= dataList.size() && data != null) {
            oldData = dataList.set(position, data);

            if (isNeedNotify()) {
                adapter.notifyItemChanged(convert(position));
            }
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

            if (isNeedNotify()) {
                adapter.notifyItemRangeChanged(convert(position), data.size());
            }
        }

        return list;

    }

    @Override
    public void swap(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && toPosition >= 0 && fromPosition < dataList.size() && toPosition
                < dataList.size()) {
            Collections.swap(dataList, fromPosition, toPosition);
            if (isNeedNotify()) {
                adapter.notifyItemChanged(convert(fromPosition));
                adapter.notifyItemChanged(convert(toPosition));
            }
        }
    }

    @Override
    public void move(int fromPosition, int toPosition) {
        if (fromPosition >= 0 && toPosition >= 0 && fromPosition < dataList.size() && toPosition
                < dataList.size()) {
            dataList.add(toPosition, dataList.remove(fromPosition));

            if (isNeedNotify()) {
                adapter.notifyItemMoved(convert(fromPosition), convert(toPosition));
            }
        }
    }

    @Override
    public void clear() {
        int count = dataList.size();
        dataList.clear();
        if (isNeedNotify()) {
            adapter.notifyItemRangeRemoved(convert(0), count);
        }
    }

    @Override
    public void clear(int position) {
        if (position >= 0 && position < dataList.size()) {
            int count = dataList.size();

            for (int i = 0; i < count - position; i++) {
                dataList.remove(position);
            }

            if (isNeedNotify()) {
                adapter.notifyItemRangeRemoved(convert(position), count - position);
            }
        }
    }

    @Override
    public List<SourceType> getList() {
        return dataList;
    }

    @Override
    public SourceType get(int position) {
        if (position >= 0 && position < dataList.size()) {
            return dataList.get(position);
        } else {
            return null;
        }
    }
}
