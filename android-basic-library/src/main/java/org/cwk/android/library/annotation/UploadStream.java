package org.cwk.android.library.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注网络请求为http上传{@link org.cwk.android.library.network.factory.NetworkType#UPLOAD_STREAM},
 * 用于标记{@link org.cwk.android.library.work.WorkModel#onTaskUri()},
 * 用要上传的文件用"file"作为key，文件路径作为"value"
 *
 * @author 超悟空
 * @version 1.0 2017/8/25
 * @since 1.0 2017/8/25
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface UploadStream {
}
