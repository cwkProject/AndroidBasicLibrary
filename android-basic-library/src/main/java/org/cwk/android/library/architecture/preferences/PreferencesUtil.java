package org.cwk.android.library.architecture.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.cwk.android.library.annotation.Encrypt;

import java.lang.reflect.Field;

/**
 * 基于{@link SharedPreferences}的对象属性持久化工具,
 * 只支持Sting,int,boolean,float,long的属性值，其他类型将不会被保存
 *
 * @author 超悟空
 * @version 1.0 2015/1/8
 * @since 1.0
 */
public class PreferencesUtil {

    /**
     * 日志标签前缀
     */
    private static final String TAG = "PreferencesUtil";

    /**
     * 成员属性加解密工具
     */
    public interface DataCipher {
        /**
         * 加密数据
         *
         * @param data 要加密的数据，非String类型成员会被转换为String类型
         *
         * @return 加密后的文本
         */
        String encrypt(String data);

        /**
         * 解密数据
         *
         * @param cipherText 密文
         *
         * @return 解密后的数据，非String类型成员会自动被转换
         */
        String decrypt(String cipherText);
    }

    /**
     * 加解密器
     */
    private DataCipher cipher = null;

    /**
     * 存储器对象
     */
    private SharedPreferences sharedPreferences = null;

    /**
     * 传入上下文和文件名的构造函数
     *
     * @param context 上下文
     */
    public PreferencesUtil(Context context, String fileName) {
        this.sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        Log.v(TAG, "file name is " + fileName);
    }

    /**
     * 设置加解密器
     *
     * @param cipher 加解密工具
     */
    public void setDataCipher(DataCipher cipher) {
        this.cipher = cipher;
    }

    /**
     * 保存指定对象的属性值到文件
     *
     * @param obj 要保存的对象
     */
    public void save(Object obj) {
        Log.v(TAG, "save start");

        if (isNullObject(obj)) {
            return;
        }

        // 获取文件编辑器
        SharedPreferences.Editor editor = getEditor();

        // 获取对象类名
        String objectName = obj.getClass().getName();
        Log.v(TAG, "save object name is " + objectName);

        // 获取对象的全部属性
        Field[] fields = obj.getClass().getDeclaredFields();

        // 遍历全部对象属性
        for (Field field : fields) {
            // 写一个属性值
            put(editor, field, obj, objectName);
        }

        // 提交保存
        editor.commit();
        Log.v(TAG, "save end");
    }

    /**
     * 判断传入的对象是否为空
     *
     * @param obj 要检查的对象
     *
     * @return 为空返回true
     */
    private boolean isNullObject(Object obj) {
        if (obj == null) {
            Log.d(TAG, "The target Object is null");
            return true;
        }
        return false;
    }

    /**
     * 向文件写入属性值
     *
     * @param editor 指定的编辑器
     * @param field  要写入的属性
     * @param obj    取值对象
     * @param pre    key前缀
     */
    private void put(SharedPreferences.Editor editor, Field field, Object obj, String pre) {
        // 关闭属性访问权限检查
        field.setAccessible(true);

        try {
            Log.v(TAG, "put field type:" + field.getType().getName() + "; name:" + field.getName
                    () + "; value:" + field.get(obj));

            // 是否需要加密
            if (cipher != null && field.isAnnotationPresent(Encrypt.class)) {
                Log.v(TAG, "put field " + field.getName() + " is encrypt");

                Object data = field.get(obj);

                if (data != null) {
                    editor.putString(pre + "." + field.getName(), cipher.encrypt(String.valueOf
                            (data)));
                } else {
                    editor.remove(pre + "." + field.getName());
                }

                return;
            }

            // 根据属性类型选择操作
            switch (field.getType().getName()) {
                case "java.lang.String":
                    editor.putString(pre + "." + field.getName(), (String) field.get(obj));
                    break;
                case "int":
                    editor.putInt(pre + "." + field.getName(), field.getInt(obj));
                    break;
                case "boolean":
                    editor.putBoolean(pre + "." + field.getName(), field.getBoolean(obj));
                    break;
                case "float":
                    editor.putFloat(pre + "." + field.getName(), field.getFloat(obj));
                    break;
                case "long":
                    editor.putLong(pre + "." + field.getName(), field.getLong(obj));
                    break;
            }
        } catch (IllegalAccessException e) {
            Log.e(TAG, "put error ", e);
        }
    }

    /**
     * 从文件读取对象属性值，如果文件中没有对应值，则会保留原属性值
     *
     * @param obj 要填充的对象
     */
    public void read(Object obj) {
        Log.v(TAG, "read start");

        if (isNullObject(obj)) {
            return;
        }

        // 获取存储对象
        SharedPreferences reader = getSharedPreferences();

        // 获取对象类名
        String objectName = obj.getClass().getName();
        Log.v(TAG, "read object name is " + objectName);

        // 获取对象的全部属性
        Field[] fields = obj.getClass().getDeclaredFields();

        // 遍历全部对象属性
        for (Field field : fields) {
            // 读一个属性值
            push(reader, field, obj, objectName);
        }

        Log.v(TAG, "read end");
    }

    /**
     * 从文件读取属性值
     *
     * @param reader 读取器
     * @param field  要读取的属性
     * @param obj    目标对象
     * @param pre    key前缀
     */
    private void push(SharedPreferences reader, Field field, Object obj, String pre) {
        // 关闭属性访问权限检查
        field.setAccessible(true);

        try {
            // 是否需要解密
            if (cipher != null && field.isAnnotationPresent(Encrypt.class)) {
                Log.v(TAG, "push field " + field.getName() + " need decrypt");

                String cipherText = reader.getString(pre + "." + field.getName(), (String) field
                        .get(obj));

                if (cipherText != null) {
                    String data = cipher.decrypt(cipherText);

                    // 根据属性类型选择操作
                    switch (field.getType().getName()) {
                        case "java.lang.String":
                            field.set(obj, data);
                            break;
                        case "int":
                            field.setInt(obj, Integer.parseInt(data));
                            break;
                        case "boolean":
                            field.setBoolean(obj, Boolean.parseBoolean(data));
                            break;
                        case "float":
                            field.setFloat(obj, Float.parseFloat(data));
                            break;
                        case "long":
                            field.setLong(obj, Long.parseLong(data));
                            break;
                    }
                }

                return;
            }

            // 根据属性类型选择操作
            switch (field.getType().getName()) {
                case "java.lang.String":
                    field.set(obj, reader.getString(pre + "." + field.getName(), (String) field
                            .get(obj)));
                    break;
                case "int":
                    field.setInt(obj, reader.getInt(pre + "." + field.getName(), field.getInt
                            (obj)));
                    break;
                case "boolean":
                    field.setBoolean(obj, reader.getBoolean(pre + "." + field.getName(), field
                            .getBoolean(obj)));
                    break;
                case "float":
                    field.setFloat(obj, reader.getFloat(pre + "." + field.getName(), field
                            .getFloat(obj)));
                    break;
                case "long":
                    field.setLong(obj, reader.getLong(pre + "." + field.getName(), field.getLong
                            (obj)));
                    break;
            }
            Log.v(TAG, "push field type:" + field.getType().getName() + "; name:" + field.getName
                    () + "; value:" + field.get(obj));
        } catch (IllegalAccessException e) {
            Log.e(TAG, "push error", e);
        }
    }

    /**
     * 从文件清空对象属性
     */
    public void clear(Object obj) {
        Log.v(TAG, "clear start");

        if (isNullObject(obj)) {
            return;
        }

        // 获取文件编辑器
        SharedPreferences.Editor editor = getEditor();

        // 获取对象类名
        String objectName = obj.getClass().getName();
        Log.v(TAG, "clear object name is " + objectName);

        // 获取对象的全部属性
        Field[] fields = obj.getClass().getDeclaredFields();

        // 遍历全部对象属性
        for (Field field : fields) {
            Log.v(TAG, "clear field name is " + field.getName());
            // 移除一个属性值
            editor.remove(objectName + "." + field.getName());
        }

        // 提交保存
        editor.commit();
        Log.v(TAG, "clear end");
    }

    /**
     * 获取当前的存储器对象
     *
     * @return SharedPreferences对象
     */
    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    /**
     * 获取当前的文件编辑器对象
     *
     * @return Editor对象
     */
    public SharedPreferences.Editor getEditor() {
        return getSharedPreferences().edit();
    }
}
