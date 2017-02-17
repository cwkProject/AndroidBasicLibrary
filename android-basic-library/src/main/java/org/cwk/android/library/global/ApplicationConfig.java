package org.cwk.android.library.global;

import android.content.Context;

import org.cwk.android.library.annotation.Encrypt;
import org.cwk.android.library.model.config.PersistenceConfigModel;

/**
 * 持久化的全局配置对象
 *
 * @author 超悟空
 * @version 1.0 2015/6/11
 * @since 1.0
 */
public class ApplicationConfig extends PersistenceConfigModel {

    /**
     * 用户名
     */
    private String userName = null;

    /**
     * 用户标识
     */
    private int userID = 0;

    /**
     * 手机号
     */
    private String mobile = null;

    /**
     * 密码
     */
    @Encrypt
    private String password = null;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public ApplicationConfig(Context context) {
        // 父类构造函数，传入全局内容提供者
        super(context);
        // 加载数据
        Refresh();
    }

    @Override
    protected void onDefault() {
        super.onDefault();
        userName = null;
        password = null;
        userID = 0;
        mobile = null;
    }

    @Override
    protected boolean onIsEncrypt() {
        return true;
    }

    /**
     * 得到用户名
     *
     * @return 用户名字符串
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名字符串
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 得到密码
     *
     * @return 密码字符串
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码字符串
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取手机号
     *
     * @return 手机号
     */
    public String getMobile() {
        return this.mobile;
    }

    /**
     * 设置手机号
     *
     * @param mobile 手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取用户标识
     *
     * @return 用户标识串
     */
    public int getUserID() {
        return this.userID;
    }

    /**
     * 设置用户标识
     *
     * @param userID 用户标识串
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }
}
