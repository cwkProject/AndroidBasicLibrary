package org.cwk.android.library.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.cwk.android.library.global.Global;

/**
 * 检测网络连接状态
 *
 * @author 超悟空
 * @version 1.0 2015/6/11
 * @since 1.0
 */
public class CheckNetwork {

    /**
     * 无网络
     */
    public static final int NETWORK_NOT = -1;

    /**
     * 未知网络
     */
    public static final int NETWORK_UNKNOWN = 0;

    /**
     * WIFI网络
     */
    public static final int NETWORK_WIFI = 1;

    /**
     * 2G网络
     */
    public static final int NETWORK_2G = 2;

    /**
     * 3G网络
     */
    public static final int NETWORK_3G = 3;

    /**
     * 4G网络
     */
    public static final int NETWORK_4G = 4;

    /**
     * 对网络连接状态进行判断
     *
     * @return true可用，false不可用
     */
    public static boolean isOpenNetwork() {
        ConnectivityManager manager = (ConnectivityManager) Global.getApplication().getSystemService
                (Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable();
        }

        return true;
    }

    /**
     * 获取当前网络类型
     *
     * @return 网络类型
     */
    public static int getNetWorkType() {
        int netWorkType = NETWORK_UNKNOWN;

        ConnectivityManager connectivityManager = (ConnectivityManager) Global.getApplication()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager != null ? connectivityManager
                .getActiveNetworkInfo() : null;

        if (networkInfo != null && networkInfo.isConnected() && networkInfo.isAvailable()) {
            int type = networkInfo.getType();

            if (type == ConnectivityManager.TYPE_WIFI) {
                netWorkType = NETWORK_WIFI;
            } else if (type == ConnectivityManager.TYPE_MOBILE) {
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_GSM:
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        netWorkType = NETWORK_2G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                    case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                        netWorkType = NETWORK_3G;
                        break;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        netWorkType = NETWORK_4G;
                        break;
                    default:
                        netWorkType = NETWORK_UNKNOWN;
                        break;
                }
            }
        } else {
            netWorkType = NETWORK_NOT;
        }

        return netWorkType;
    }
}
