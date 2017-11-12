package org.cwk.android.library.model.function;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.cwk.android.library.model.operate.OnGroupPositionToAdapterPosition;

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
 *              public SampleViewHolder onCreateViewHolder(ViewGroup parent) {
 *                  return new SampleViewHolder(LayoutInflater.from(parent.getContext()).inflate
 *                  (R.layout
 *                      .sample_layout, parent, false));
 *              }
 *          @Override
 *              protected void onBindViewHolder(SampleViewHolder holder, int position) {
 *                  String data = dataList.get(position);
 *                  // holder set data
 *              }
 *      };
 *  public RecyclerViewHolderManager<String, SampleViewHolder> headGroup = new
 *      RecyclerViewHolderManager<String, SampleViewHolder>(this) {
 *          @Override
 *          public SampleViewHolder onCreateViewHolder(ViewGroup parent) {
 *              return new SampleViewHolder(LayoutInflater.from(parent.getContext()).inflate(R
 *              .layout
 *                  .sample_layout, parent, false));
 *          }
 *          @Override
 *          protected void onBindViewHolder(SampleViewHolder holder, int position) {
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
        .ViewHolder> implements OnGroupPositionToAdapterPosition {

    /**
     * 存放数据集管理器的集合
     */
    private final List<RecyclerViewHolderManager<?, ? extends RecyclerView.ViewHolder>>
            managerList = new ArrayList<>();

    /**
     * 构造方法
     */
    public MultipleRecyclerViewAdapter() {
        onBindManagers(managerList);

        for (int i = 0; i < managerList.size(); i++) {
            managerList.get(i).setGroupIndex(i);
        }
    }

    /**
     * 装配数据集管理器，在这里将{@link RecyclerViewHolderManager}加入到{@link #managerList}中,
     * 数据集分组的显示顺序就是加入管理器的顺序
     *
     * @param managerList 数据集管理器
     */
    protected abstract void onBindManagers(List<RecyclerViewHolderManager<?, ? extends
            RecyclerView.ViewHolder>> managerList);

    @Override
    public final int getItemViewType(int position) {
        for (int i = 0, count = 0; i < managerList.size(); i++) {
            count += managerList.get(i).getCount();
            if (position < count) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public final RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return managerList.get(viewType).onCreateViewHolder(parent);
    }

    @Override
    public final void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int groupPosition = position;

        for (int i = 0; i < holder.getItemViewType(); i++) {
            groupPosition -= managerList.get(i).getCount();
        }

        managerList.get(holder.getItemViewType()).bindViewHolder(holder, groupPosition);
    }

    @Override
    public final int getItemCount() {
        int count = 0;

        for (int i = 0; i < managerList.size(); i++) {
            count += managerList.get(i).getCount();
        }

        return count;
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
    public <T extends RecyclerViewHolderManager> T getHolderManager(int groupIndex) {
        return (T) managerList.get(groupIndex);
    }

    @Override
    public int convert(int groupIndex, int groupPosition) {

        int position = 0;

        for (int i = 0; i < groupIndex; i++) {
            position += managerList.get(i).getCount();
        }

        return position + groupPosition;
    }
}
