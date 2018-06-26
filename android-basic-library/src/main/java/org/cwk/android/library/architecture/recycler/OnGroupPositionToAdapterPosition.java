package org.cwk.android.library.architecture.recycler;

/**
 * 将分组位置与适配器位置转换
 *
 * @author 超悟空
 * @version 1.0 2017/11/12
 * @since 1.0
 */
public interface OnGroupPositionToAdapterPosition {

    /**
     * 从分组中位置转换到适配器位置
     *
     * @param groupIndex    组索引
     * @param groupPosition 组中的位置
     *
     * @return 适配器中的位置序号
     */
    int convertToAdapterPosition(int groupIndex, int groupPosition);

    /**
     * 从适配器位置转换到分组中位置
     *
     * @param groupIndex      组索引
     * @param adapterPosition 适配器位置
     *
     * @return 组中的位置序号
     */
    int convertToGroupPosition(int groupIndex, int adapterPosition);
}
