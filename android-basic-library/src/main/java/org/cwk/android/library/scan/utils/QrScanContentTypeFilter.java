package org.cwk.android.library.scan.utils;

import java.io.Serializable;

/**
 * 二维码扫描类型过滤器
 *
 * @author 超悟空
 * @version 1.0 2017/3/3
 * @since 1.0 2017/3/3
 **/

public interface QrScanContentTypeFilter extends Serializable {

    /**
     * 扫描到结果
     *
     * @param result 二维码信息的字符串
     *
     * @return 是否处理，true表示需要处理
     */
    boolean onScanFinishListener(String result);
}
