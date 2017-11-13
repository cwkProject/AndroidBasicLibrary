package org.cwk.android.library.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.cwk.android.library.global.Global;

import java.util.UUID;

/**
 * 设备ID工具
 *
 * @author 超悟空
 * @version 1.0 2016/3/19
 * @since 1.0
 */
public class DeviceID {

    /**
     * 设备ID键名
     */
    private static final String DEVICE_ID = "device_id";

    /**
     * 设备id
     */
    private static String deviceId = null;

    /**
     * 获取设备唯一标识
     *
     * @return 设备唯一标识
     */
    public static synchronized String getDeviceId() {

        if (deviceId != null) {
            return deviceId;
        }

        // 从配置文件读取
        readConfig();

        if (deviceId == null) {
            // 通过uuid生成
            fromUUID();
            writeConfig();
        }

        return deviceId;
    }

    /**
     * 从配置文件读取
     */
    private static void readConfig() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences
                (Global.getApplication());

        deviceId = sharedPreferences.getString(DEVICE_ID, null);
    }

    /**
     * 通过uuid生成
     */
    private static void fromUUID() {
        deviceId = UUID.randomUUID().toString();
    }

    /**
     * 写入配置文件
     */
    private static void writeConfig() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(Global
                .getApplication()).edit();
        editor.putString(DEVICE_ID, deviceId);
        editor.apply();
    }
}
