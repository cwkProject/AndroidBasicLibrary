package org.cwk.android.library.global;

/**
 * 登录状态数据类
 *
 * @author 超悟空
 * @version 4.0 2016/8/15
 * @since 1.0
 */
public class LoginStatus {

    private static LoginStatus loginStatus = new LoginStatus();

    /**
     * 标记是否已登录
     */
    private boolean login = false;

    /**
     * 用户标识
     */
    private int userID = 0;

    /**
     * 公司码
     */
    private String companyCode = null;

    /**
     * 公司名称
     */
    private String companyName = null;

    /**
     * 用户名
     */
    private String userName = null;

    /**
     * 用户真实姓名
     */
    private String realName = null;

    /**
     * 用户昵称
     */
    private String nickname = null;

    /**
     * 手机号
     */
    private String mobile = null;

    /**
     * 上次登录时间
     */
    private String lastTime = null;

    /**
     * 构造函数
     */
    private LoginStatus() {
        onCreate();
    }

    /**
     * 获取用户数据对象
     *
     * @return 用户数据对象
     */
    public static LoginStatus getLoginStatus() {
        return loginStatus;
    }

    /**
     * 重置数据
     */
    public static void Reset() {
        loginStatus.onCreate();
    }

    /**
     * 初始化参数
     */
    private void onCreate() {
        // 初始化用户参数
        setLogin(false);
        setUserID(0);
        setCompanyCode(null);
        setCompanyName(null);
        setUserName(null);
        setMobile(null);
        setLastTime(null);
        setRealName(null);
        setNickname(null);
    }

    /**
     * 判断是否登录
     *
     * @return 返回状态
     */
    public static boolean isLogin() {
        return loginStatus.login;
    }

    /**
     * 设置登录状态
     *
     * @param flag 状态标识
     */
    public synchronized void setLogin(boolean flag) {
        this.login = flag;
    }

    /**
     * 获取用户标识
     *
     * @return 用户标识串
     */
    public static int getUserID() {
        return loginStatus.userID;
    }

    /**
     * 设置用户标识
     *
     * @param userID 用户标识串
     */
    public synchronized void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * 获取公司代码
     *
     * @return 公司代码
     */
    public static String getCompanyCode() {
        return loginStatus.companyCode;
    }

    /**
     * 设置公司代码
     *
     * @param companyCode 公司代码
     */
    public synchronized void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    /**
     * 获取公司名称
     *
     * @return 公司名称
     */
    public static String getCompanyName() {
        return loginStatus.companyName;
    }

    /**
     * 设置公司名称
     *
     * @param companyName 公司名称
     */
    public synchronized void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public static String getUserName() {
        return loginStatus.userName;
    }

    /**
     * 设置用户名
     *
     * @param userName 用户名
     */
    public synchronized void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取手机号
     *
     * @return 手机号
     */
    public static String getMobile() {
        return loginStatus.mobile;
    }

    /**
     * 设置手机号
     *
     * @param mobile 手机号
     */
    public synchronized void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取上次登录时间
     *
     * @return 时间
     */
    public static String getLastTime() {
        return loginStatus.lastTime;
    }

    /**
     * 设置上次登录时间
     *
     * @param lastTime 时间
     */
    public synchronized void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    /**
     * 获取用户真实姓名
     *
     * @return 真实姓名
     */
    public static String getRealName() {
        return loginStatus.realName;
    }

    /**
     * 设置用户真实姓名
     *
     * @param realName 真实姓名
     */
    public synchronized void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 获取用户昵称
     *
     * @return 昵称
     */
    public static String getNickname() {
        return loginStatus.nickname;
    }

    /**
     * 设置用户昵称
     *
     * @param nickname 昵称
     */
    public synchronized void setNickname(String nickname) {
        this.nickname = nickname;
    }
}