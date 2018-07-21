package org.cwk.android.library.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注网络请求为http上传{@link org.cwk.android.library.network.factory.NetworkType#UPLOAD
 * }，用于标记{@link org.cwk.android.library.work.WorkModel#onTaskUri()}
 *
 * @author 超悟空
 * @version 1.0 2017/2/10
 * @since 1.0 2017/2/10
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Upload {
}
