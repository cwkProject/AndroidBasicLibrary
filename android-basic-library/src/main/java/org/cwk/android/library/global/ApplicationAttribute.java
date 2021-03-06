package org.cwk.android.library.global;

/**
 * 应用相关参数
 *
 * @author 超悟空
 * @version 3.0 2016/3/19
 * @since 1.0
 */
public class ApplicationAttribute {

    /**
     * 自身静态实例
     */
    private static ApplicationAttribute applicationAttribute = new ApplicationAttribute();

    /**
     * 应用标识
     */
    private String appCode = null;

    /**
     * 应用令牌
     */
    private String appToken = null;

    /**
     * 标识是否在全局范围内对应用的网络请求进行签名
     */
    private boolean requestSign = false;

    /**
     * 默认des加密key
     */
    private String desKey = null;

    /**
     * 获取应用标识
     *
     * @return 应用标识码
     */
    public static String getAppCode() {
        return applicationAttribute.appCode;
    }

    /**
     * 获取应用令牌
     *
     * @return 应用令牌
     */
    public static String getAppToken() {
        return applicationAttribute.appToken;
    }

    /**
     * 是否对网络请求进行签名
     *
     * @return true表示需要签名
     */
    public static boolean isRequestSign() {
        return applicationAttribute.requestSign;
    }

    /**
     * 获取默认使用的des加密key
     *
     * @return base64编码的key
     */
    public static String getDesKey() {
        return applicationAttribute.desKey;
    }

    /**
     * 构造函数
     */
    private ApplicationAttribute() {
    }

    /**
     * 创建全局变量参数
     *
     * @return 全局变量对象
     */
    public static ApplicationAttribute create() {
        applicationAttribute.appCode = null;
        applicationAttribute.appToken = null;
        applicationAttribute.requestSign = false;
        applicationAttribute.desKey = null;
        return applicationAttribute;
    }

    /**
     * 设置是否全局签名
     *
     * @param flag true表示需要签名
     *
     * @return 全局变量对象
     */
    public ApplicationAttribute requestSign(boolean flag) {
        this.requestSign = flag;
        return this;
    }

    /**
     * 设置应用编号
     *
     * @param appCode 应用编号
     *
     * @return 全局变量对象
     */
    public ApplicationAttribute appCode(String appCode) {
        this.appCode = appCode;
        return this;
    }

    /**
     * 设置应用令牌
     *
     * @param appToken 应用令牌
     *
     * @return 全局变量对象
     */
    public ApplicationAttribute appToken(String appToken) {
        this.appToken = appToken;
        return this;
    }

    /**
     * 设置默认的des加密key
     *
     * @param desKey base64编码的key
     *
     * @return 全局变量对象
     */
    public ApplicationAttribute desKey(String desKey) {
        this.desKey = desKey;
        return this;
    }
}
