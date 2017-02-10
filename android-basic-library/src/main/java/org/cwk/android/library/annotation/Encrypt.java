package org.cwk.android.library.annotation;

import org.cwk.android.library.util.PreferencesUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记需要进行加密的成员属性，需要设置加解密器{@link PreferencesUtil#setDataCipher(PreferencesUtil.DataCipher)}
 *
 * @author 超悟空
 * @version 1.0 2017/2/10
 * @since 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Encrypt {
}
