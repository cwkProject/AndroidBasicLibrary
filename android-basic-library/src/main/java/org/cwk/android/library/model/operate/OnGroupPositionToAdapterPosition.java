package org.cwk.android.library.model.operate;

/**
 * 将分组位置转换到适配器位置
 *
 * @author 超悟空
 * @version 1.0 2017/11/12
 * @since 1.0
 */
public interface OnGroupPositionToAdapterPosition {

    /**
     * 转换
     *
     * @param groupIndex    组索引
     * @param groupPosition 组中的位置
     *
     * @return 适配器中的位置序号
     */
    int convert(int groupIndex, int groupPosition);
}
