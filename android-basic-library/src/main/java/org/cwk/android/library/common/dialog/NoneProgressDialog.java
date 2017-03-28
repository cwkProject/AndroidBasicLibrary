package org.cwk.android.library.common.dialog;

import org.cwk.android.library.model.dialog.IProgressDialog;

/**
 * 不加载进度条的实现
 *
 * @author 超悟空
 * @version 1.0 2015/1/22
 * @since 1.0
 */
public class NoneProgressDialog implements IProgressDialog<NoneProgressDialog> {
    @Override
    public void setProgress(int progress) {
        // 不执行任何操作，即不显示进度条
    }

    @Override
    public NoneProgressDialog setTimeout(int timeout) {
        return this;
    }
}
