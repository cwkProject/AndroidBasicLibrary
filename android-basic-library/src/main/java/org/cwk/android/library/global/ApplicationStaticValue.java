package org.cwk.android.library.global;

/**
 * 存放类库使用的全局静态常量
 *
 * @author 超悟空
 * @version 2.0 2016/3/19
 * @since 1.0
 */
public interface ApplicationStaticValue {

    /**
     * 应用程序配置
     */
    interface AppConfig {

        /**
         * 设备类型
         */
        String DEVICE_TYPE = "Android";

        /**
         * 应用程序默认使用的配置文件名
         */
        String APPLICATION_CONFIG_FILE_NAME = "app_config";
    }
}
