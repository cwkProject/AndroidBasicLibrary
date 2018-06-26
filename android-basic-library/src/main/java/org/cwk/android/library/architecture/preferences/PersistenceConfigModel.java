package org.cwk.android.library.architecture.preferences;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.util.Log;

import org.cwk.android.library.global.ApplicationAttribute;
import org.cwk.android.library.global.ApplicationStaticValue;

/**
 * 需持久化系统参数配置抽象模型
 *
 * @author 超悟空
 * @version 1.0 2015/1/7
 * @since 1.0
 */
public abstract class PersistenceConfigModel {

    /**
     * 日志标签前缀
     */
    private String TAG = this.getClass().getSimpleName();

    /**
     * 持久化对象
     */
    private PreferencesUtil preferencesUtil = null;

    /**
     * 传入上下文的构造函数，以"config"作为配置文件名
     *
     * @param context 上下文对象
     */
    public PersistenceConfigModel(Context context) {
        this(context , ApplicationStaticValue.AppConfig.APPLICATION_CONFIG_FILE_NAME);
    }

    /**
     * 传入上下文和配置文件名的构造函数
     *
     * @param context  上下文对象
     * @param fileName 配置文件名
     */
    public PersistenceConfigModel(Context context , String fileName) {
        // 新建持久化对象
        this.preferencesUtil = new PreferencesUtil(context , fileName);
        Log.v(TAG , "config name is " + fileName);

        this.preferencesUtil.setDataCipher(onCreateDataCipher());
    }

    /**
     * 保存设置
     */
    @CallSuper
    public void save() {
        Log.v(TAG , "save invoked");
        this.preferencesUtil.save(this);
    }

    /**
     * 刷新配置参数，从配置文件中重新读取参数
     */
    @CallSuper
    public void refresh() {
        Log.v(TAG , "refresh invoked");
        this.preferencesUtil.read(this);
    }

    /**
     * 清空配置文件，重置当前参数
     */
    @CallSuper
    public void clear() {
        Log.v(TAG , "clear invoked");
        this.preferencesUtil.clear(this);
        onDefault();
    }

    /**
     * 设置参数默认值，
     * 用于在{@link #clear()}时调用以清空全局变量
     */
    protected void onDefault() {
    }

    /**
     * 创建一个加解密执行器，默认为DES加密器
     *
     * @return 加密器
     */
    protected PreferencesUtil.DataCipher onCreateDataCipher() {
        // 用于存放加密密钥的键
        final String keyTag = "PersistenceConfigModel.encryption";

        if (ApplicationAttribute.getDesKey() == null) {
            return null;
        }

        // 尝试读取保存的key
        String key = preferencesUtil.getSharedPreferences().getString(keyTag , null);

        DesDataCipher cipher = new DesDataCipher(ApplicationAttribute.getDesKey() , key);

        if (key == null) {
            key = cipher.createNewKey();
            preferencesUtil.getEditor().putString(keyTag , key).apply();
        }

        return cipher;
    }

    /**
     * 获取当前对象的持久化工具对象
     *
     * @return 持久化对象
     */
    protected PreferencesUtil getPreferencesUtil() {
        return preferencesUtil;
    }
}
