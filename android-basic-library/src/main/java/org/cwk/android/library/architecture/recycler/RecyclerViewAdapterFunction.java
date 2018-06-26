package org.cwk.android.library.architecture.recycler;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * {@link android.support.v7.widget.RecyclerView.Adapter}因该具备的基础功能
 *
 * @param <SourceType> 数据源类型
 *
 * @author 超悟空
 * @version 1.0 2017/2/10
 * @since 1.0 2017/2/10
 **/
public interface RecyclerViewAdapterFunction<SourceType> {

    /**
     * 在列表末尾追加一条数据
     *
     * @param data 数据
     */
    void add(SourceType data);

    /**
     * 在列表指定位置插入一条数据
     *
     * @param position 索引
     * @param data     数据
     */
    void add(int position, SourceType data);

    /**
     * 在列表末尾追加一组数据
     *
     * @param data 数据集
     */
    void add(Collection<SourceType> data);

    /**
     * 在列表指定位置插入一组数据
     *
     * @param position 索引
     * @param data     数据集
     */
    void add(int position, Collection<SourceType> data);

    /**
     * 移除一条数据
     *
     * @param position 索引
     *
     * @return 被移除的数据，失败返回null
     */
    SourceType remove(int position);

    /**
     * 移除一组数据
     *
     * @param start 起点索引
     * @param count 移除数量
     *
     * @return 被移除的一组数据，失败返回null
     */
    List<SourceType> remove(int start, int count);

    /**
     * 变更一条数据
     *
     * @param position 索引
     * @param data     新数据
     *
     * @return 原数据，失败返回null
     */
    SourceType change(int position, SourceType data);

    /**
     * 变更一组数据
     *
     * @param position 索引
     * @param data     新数据集
     *
     * @return 原数据集，失败返回null
     */
    List<SourceType> change(int position, Collection<SourceType> data);

    /**
     * 交换两条数据
     *
     * @param fromPosition 索引1
     * @param toPosition   索引2
     */
    void swap(int fromPosition, int toPosition);

    /**
     * 移动数据
     *
     * @param fromPosition 起始位置
     * @param toPosition   目标位置
     */
    void move(int fromPosition, int toPosition);

    /**
     * 清空数据
     */
    void clear();

    /**
     * 清空数据
     *
     * @param position 指定的开始位置
     */
    void clear(int position);

    /**
     * 获取数据源
     *
     * @return 数据集
     */
    @NonNull
    List<SourceType> getList();

    /**
     * 获取指定位置的数据
     *
     * @param position 索引
     *
     * @return 数据，失败返回null
     */
    SourceType get(int position);
}
