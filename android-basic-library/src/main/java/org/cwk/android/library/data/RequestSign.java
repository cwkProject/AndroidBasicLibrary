package org.cwk.android.library.data;

import android.util.Log;

import org.cwk.android.library.global.ApplicationAttribute;
import org.cwk.android.library.struct.FileInfo;
import org.cwk.android.library.util.HashMD5;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 请求参数签名工具
 *
 * @author 超悟空
 * @version 1.0 2016/3/19
 * @since 1.0
 */
public class RequestSign {

    /**
     * 应用编号
     */
    private static final String APP_CODE = "AppName";

    /**
     * 应用令牌
     */
    private static final String SIGN = "Sign";

    /**
     * 参数签名
     *
     * @param logTag   标签，用于跟踪日志
     * @param sendData 要发送的参数
     */
    public static void sign(String logTag, Map<String, Object> sendData) {
        // 遍历sendData集合并加入请求参数对象
        if (ApplicationAttribute.isRequestSign() && sendData != null && ApplicationAttribute
                .getAppCode() != null && ApplicationAttribute.getAppToken() != null) {

            // key数组
            List<String> keyList = new ArrayList<>();

            // 遍历并追加参数
            for (Map.Entry<String, ?> dataEntry : sendData.entrySet()) {

                if (dataEntry.getValue() instanceof FileInfo || dataEntry.getValue() instanceof
                        File) {
                    // 排除文件类型
                    continue;
                }

                // 处理剩余情况，包括String，Integer，Boolean等类型
                if (dataEntry.getValue() != null) {
                    // 参数不为空，才参与签名
                    keyList.add(dataEntry.getKey());
                }
            }

            // 加入应用编号
            sendData.put(APP_CODE, ApplicationAttribute.getAppCode());
            keyList.add(APP_CODE);

            // 排序
            Collections.sort(keyList);

            StringBuilder builder = new StringBuilder();
            // 拼接
            for (String key : keyList) {
                builder.append(key).append(sendData.get(key));
            }

            // 拼接应用令牌
            builder.append(ApplicationAttribute.getAppToken());

            String sign = HashMD5.hash(builder.toString());

            // 加入签名串
            sendData.put(SIGN, sign);

            Log.v(logTag, "sign app code:" + ApplicationAttribute.getAppCode() + " sign:" + sign);
        } else {
            Log.v(logTag, "sign parameters has null");
        }
    }

    /**
     * 参数签名，针对纯文本参数优化
     *
     * @param logTag   标签，用于跟踪日志
     * @param sendData 要发送的参数
     */
    public static void signForText(String logTag, Map<String, String> sendData) {
        // 遍历sendData集合并加入请求参数对象
        if (ApplicationAttribute.isRequestSign() && sendData != null && ApplicationAttribute
                .getAppCode() != null && ApplicationAttribute.getAppToken() != null) {

            // key数组
            List<String> keyList = new ArrayList<>();

            // 遍历并追加参数
            for (Map.Entry<String, String> dataEntry : sendData.entrySet()) {

                if (dataEntry.getValue() != null) {
                    // 参数不为空，才参与签名
                    keyList.add(dataEntry.getKey());
                }
            }

            // 加入应用编号
            sendData.put(APP_CODE, ApplicationAttribute.getAppCode());
            keyList.add(APP_CODE);

            // 排序
            Collections.sort(keyList);

            StringBuilder builder = new StringBuilder();
            // 拼接
            for (String key : keyList) {
                builder.append(key).append(sendData.get(key));
            }

            // 拼接应用令牌
            builder.append(ApplicationAttribute.getAppToken());

            String sign = HashMD5.hash(builder.toString());

            // 加入签名串
            sendData.put(SIGN, sign);

            Log.v(logTag, "signForText app code:" + ApplicationAttribute.getAppCode() + " " +
                    "sign:" + sign);
        } else {
            Log.v(logTag, "signForText parameters has null");
        }
    }
}
