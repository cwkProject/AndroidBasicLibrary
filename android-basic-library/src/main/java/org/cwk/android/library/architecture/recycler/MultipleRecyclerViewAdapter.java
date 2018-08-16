package org.cwk.android.library.architecture.recycler;

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * 带有多组数据集和相应的显示控件的{@link android.support.v7.widget.RecyclerView.Adapter}实现，
 * 每组数据集之间相互分割，没有穿插交替显示
 * <pre> {@code
 * public class SampleAdapter extends MultipleRecyclerViewAdapter {
 *  public RecyclerViewHolderManager<String, SampleViewHolder> mainGroup = new
 *      RecyclerViewHolderManager<String, SampleViewHolder>(this) {
 *          @Override
 *              public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
 *                  return new SampleViewHolder(LayoutInflater.from(parent.getApplication()).inflate
 *                  (R.layout
 *                      .sample_layout, parent, false));
 *              }
 *          @Override
 *              protected void onBindViewHolder(SampleViewHolder holder, int position, int
 *              viewType) {
 *                  String data = dataList.get(position);
 *                  // holder set data
 *              }
 *      };
 *  public RecyclerViewHolderManager<String, SampleViewHolder> headGroup = new
 *      RecyclerViewHolderManager<String, SampleViewHolder>(this) {
 *          @Override
 *          public SampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
 *              return new SampleViewHolder(LayoutInflater.from(parent.getApplication()).inflate(R
 *              .layout
 *                  .sample_layout, parent, false));
 *          }
 *          @Override
 *          protected void onBindViewHolder(SampleViewHolder holder, int position, int viewType) {
 *              String data = dataList.get(position);
 *              // holder set data
 *          }
 *      };
 *  @Override
 *  protected void onBindManagers(List<RecyclerViewHolderManager<?, ? extends RecyclerView
 *  .ViewHolder>> managerList) {
 *      managerList.add(headGroup);
 *      managerList.add(mainGroup);
 *  }
 * }
 * }</pre>
 *
 * @author 超悟空
 * @version 1.0 2017/11/12
 * @since 1.0
 */
public abstract class MultipleRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView
        .ViewHolder> implements OnGroupPositionToAdapterPosition, OnRecyclerAdapterTransaction {

    /**
     * getItemViewType值加权
     */
    private static final int VIEW_TYPE_WEIGHT = 1000;

    /**
     * 存放数据集管理器的集合
     */
    private final List<RecyclerViewHolderManager<?, ? extends RecyclerView.ViewHolder>>
            managerList = new ArrayList<>();

    /**
     * 是否第一次执行
     */
    private boolean isFirst = true;

    /**
     * 是否开始了事务操作
     */
    private boolean isTransaction = false;

    /**
     * 装配数据集管理器，在这里将{@link RecyclerViewHolderManager}加入到{@link #managerList}中,
     * 数据集分组的显示顺序就是加入管理器的顺序
     *
     * @param managerList 数据集管理器
     */
    protected abstract void onBindManagers(@NonNull List<RecyclerViewHolderManager<?, ? extends
            RecyclerView.ViewHolder>> managerList);

    @CallSuper
    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        if (isFirst) {
            isFirst = false;
            onBindManagers(managerList);

            for (int i = 0; i < managerList.size(); i++) {
                managerList.get(i).setGroupIndex(i);
            }
        }
    }

    @Override
    public final int getItemViewType(int position) {
        for (int i = 0, count = 0; i < managerList.size(); i++) {
            count += managerList.get(i).getCount();
            if (position < count) {
                return i * VIEW_TYPE_WEIGHT + managerList.get(i).getItemViewType
                        (convertToGroupPosition(i , position));
            }
        }

        return 0;
    }

    @NonNull
    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent , int
            viewType) {
        return managerList.get(viewType / VIEW_TYPE_WEIGHT).createViewHolder(parent , viewType %
                VIEW_TYPE_WEIGHT);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder , int position) {
        int groupIndex = holder.getItemViewType() / VIEW_TYPE_WEIGHT;
        managerList.get(groupIndex).bindViewHolder(holder , convertToGroupPosition(groupIndex ,
                position) , holder.getItemViewType() % VIEW_TYPE_WEIGHT);
    }

    @Override
    public final int getItemCount() {
        int count = 0;

        for (int i = 0; i < managerList.size(); i++) {
            count += managerList.get(i).getCount();
        }

        return count;
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        int groupIndex = holder.getItemViewType() / VIEW_TYPE_WEIGHT;
        managerList.get(groupIndex).viewRecycled(holder);
    }

    /**
     * 获取指定序号的数据集管理器
     *
     * @param groupIndex 管理器索引
     * @param <T>        管理器类泛型
     *
     * @return 管理器对象
     */
    @SuppressWarnings("unchecked")
    @NonNull
    public <T extends RecyclerViewHolderManager> T getHolderManager(int groupIndex) {
        return (T) managerList.get(groupIndex);
    }

    @Override
    public int convertToAdapterPosition(int groupIndex , int groupPosition) {

        int position = 0;

        for (int i = 0; i < groupIndex; i++) {
            position += managerList.get(i).getCount();
        }

        return position + groupPosition;
    }

    @Override
    public int convertToGroupPosition(int groupIndex , int adapterPosition) {
        int groupPosition = adapterPosition;

        for (int i = 0; i < groupIndex; i++) {
            groupPosition -= managerList.get(i).getCount();
        }

        return groupPosition;
    }

    @Override
    public void beginTransaction() {
        isTransaction = true;
    }

    @Override
    public void commit() {
        isTransaction = false;
        notifyDataSetChanged();
    }

    @Override
    public boolean isTransaction() {
        return isTransaction;
    }
}
