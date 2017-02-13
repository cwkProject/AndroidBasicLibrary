package org.cwk.android.library.common.function;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * 图片压缩工具
 *
 * @author 超悟空
 * @version 1.0 2015/11/30
 * @since 1.0
 */
public class ImageCompression {

    /**
     * 日志标签前缀
     */
    private static final String LOG_TAG = "ImageCompression.";

    /**
     * 计算图片压缩量<br>
     * 为保证图片质量，按宽高中缩放比例较小的为基准
     *
     * @param options   空加载的图片属性
     * @param reqWidth  期望宽度
     * @param reqHeight 期望高度
     *
     * @return 1表示不缩放，大于1为计算后的缩放值
     */
    public static int calculateLowSampleSize(@NotNull BitmapFactory.Options options, int
            reqWidth, int reqHeight) {
        // 如果有0值则仅参考另一项
        if (reqHeight <= 0) {
            return calculateWidthSampleSize(options, reqWidth, false);
        }

        if (reqWidth <= 0) {
            return calculateHeightSampleSize(options, reqHeight, false);
        }

        // 原始宽高
        int height = options.outHeight;
        int width = options.outWidth;

        Log.v(LOG_TAG + "calculateLowSampleSize", "old height:" + height + " old width:" + width);
        Log.v(LOG_TAG + "calculateLowSampleSize", "target height:" + reqHeight + " target width:" +
                reqWidth);

        // 宽高矫正
        if ((height > width && reqHeight < reqWidth) || (height < width && reqHeight > reqWidth)) {
            int temp = width;
            width = height;
            height = temp;
        }

        // 计算后缩放值
        int heightRatio = 1;
        int widthRatio = 1;

        if (height > reqHeight && width > reqWidth) {
            heightRatio = Math.round((float) height / (float) reqHeight);
            widthRatio = Math.round((float) width / (float) reqWidth);
        }

        // 最终压缩比例
        int sampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

        Log.v(LOG_TAG + "calculateLowSampleSize", "sampleSize is " + sampleSize);

        return sampleSize;
    }

    /**
     * 计算图片压缩量<br>
     * 为减小图片体积，按宽高中缩放比例较大的为基准
     *
     * @param options   空加载的图片属性
     * @param reqWidth  期望宽度
     * @param reqHeight 期望高度
     *
     * @return 1表示不缩放，大于1为计算后的缩放值
     */
    public static int calculateHighSampleSize(@NotNull BitmapFactory.Options options, int
            reqWidth, int reqHeight) {

        // 如果有0值则仅参考另一项
        if (reqHeight <= 0) {
            return calculateWidthSampleSize(options, reqWidth, true);
        }

        if (reqWidth <= 0) {
            return calculateHeightSampleSize(options, reqHeight, true);
        }

        // 原始宽高
        int height = options.outHeight;
        int width = options.outWidth;

        Log.v(LOG_TAG + "calculateHighSampleSize", "old height:" + height + " old width:" + width);
        Log.v(LOG_TAG + "calculateHighSampleSize", "target height:" + reqHeight + " target width:" +
                reqWidth);

        // 宽高矫正
        if ((height > width && reqHeight < reqWidth) || (height < width && reqHeight > reqWidth)) {
            int temp = width;
            width = height;
            height = temp;
        }

        // 计算后缩放值
        int heightRatio = 1;
        int widthRatio = 1;

        if (height > reqHeight || width > reqWidth) {
            heightRatio = (int) Math.ceil((float) height / (float) reqHeight);
            widthRatio = (int) Math.ceil((float) width / (float) reqWidth);
        }

        // 最终压缩比例
        int sampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;

        Log.v(LOG_TAG + "calculateHighSampleSize", "sampleSize is " + sampleSize);

        return sampleSize;
    }

    /**
     * 计算图片压缩量<br>
     * 仅以高度为基准
     *
     * @param options   空加载的图片属性
     * @param reqHeight 期望高度
     * @param high      是否高压，true表示高压
     *
     * @return 1表示不缩放，大于1为计算后的缩放值
     */
    public static int calculateHeightSampleSize(@NotNull BitmapFactory.Options options, int
            reqHeight, boolean high) {
        // 原始高
        final int height = options.outHeight;

        Log.v(LOG_TAG + "calculateHeightSampleSize", "old height:" + height + " target height:" +
                reqHeight);

        // 计算后缩放值
        int heightRatio = 1;

        // 计算高缩放
        if (reqHeight > 0 && height > reqHeight) {
            if (high) {
                heightRatio = (int) Math.ceil((float) height / (float) reqHeight);
            } else {
                heightRatio = Math.round((float) height / (float) reqHeight);
            }
        }

        Log.v(LOG_TAG + "calculateHeightSampleSize", "sampleSize is " + heightRatio);

        return heightRatio;
    }

    /**
     * 计算图片压缩量<br>
     * 仅以宽度为基准
     *
     * @param options  空加载的图片属性
     * @param reqWidth 期望宽度
     * @param high     是否高压，true表示高压
     *
     * @return 1表示不缩放，大于1为计算后的缩放值
     */
    public static int calculateWidthSampleSize(@NotNull BitmapFactory.Options options, int
            reqWidth, boolean high) {
        // 原始宽
        final int width = options.outWidth;

        Log.v(LOG_TAG + "calculateWidthSampleSize", "old width:" + width + " target width:" +
                reqWidth);

        // 计算后缩放值
        int widthRatio = 1;

        // 计算宽缩放
        if (reqWidth > 0 && width > reqWidth) {
            if (high) {
                widthRatio = (int) Math.ceil((float) width / (float) reqWidth);
            } else {
                widthRatio = Math.round((float) width / (float) reqWidth);
            }
        }

        Log.v(LOG_TAG + "calculateWidthSampleSize", "sampleSize is " + widthRatio);

        return widthRatio;
    }

    /**
     * 压缩图片到指定大小
     *
     * @param image   要压缩图片
     * @param maxSize 目标大小，单位KB，0表示不压缩
     *
     * @return 保存有图片数据的内存输出流
     */
    public static ByteArrayOutputStream compressImage(Bitmap image, int maxSize) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        // 初始压缩比率，从70%开始
        int scale = 70;
        // 第一次压缩
        image.compress(Bitmap.CompressFormat.JPEG, scale, os);

        Log.v(LOG_TAG + "compressImage", "now image size is " + os.size() / 1024 + "KB, " +
                "compress scale is " + scale);

        if (maxSize <= 0) {
            Log.v(LOG_TAG + "compressImage", "maxSize is 0, not compress");
            return os;
        }

        // 循环压缩尝试
        while (os.size() / 1024 > maxSize && scale > 10) {
            // 清除流
            os.reset();

            // 计算新压缩比
            scale = decCompressScale(scale);

            // 重新压缩
            image.compress(Bitmap.CompressFormat.JPEG, scale, os);

            Log.v(LOG_TAG + "compressImage", "now image size is " + os.size() / 1024 + "KB, " +
                    "compress scale is " + scale);
        }

        return os;
    }

    /**
     * 计算递减压缩比例
     *
     * @param scale 当前压缩比
     *
     * @return 递减后压缩比，在0-70之间
     */
    private static int decCompressScale(int scale) {
        switch (scale) {
            case 70:
                // 首次压缩递减20
                return 50;
            case 50:
            case 40:
            case 30:
                // 之后递减10
                return scale - 10;
            case 20:
            case 15:
            default:
                // 之后递减5
                return scale - 5;
        }
    }

    /**
     * 像素压缩
     *
     * @param file   图片路径
     * @param width  目标宽
     * @param height 目标高
     *
     * @return 压缩图
     */
    public static Bitmap resolutionBitmap(File file, int width, int height) {
        Log.i(LOG_TAG + "resolutionBitmap", "resolution compression begin");
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file.getPath(), options);

        options.inSampleSize = calculateLowSampleSize(options, width, height);

        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        Log.i(LOG_TAG + "resolutionBitmap", "resolution compression end");

        return bitmap;
    }

    /**
     * 像素压缩，高比率压缩，图像可能比预期更小且可能失真
     *
     * @param file   图片路径
     * @param width  目标宽
     * @param height 目标高
     *
     * @return 压缩图
     */
    public static Bitmap resolutionHighBitmap(File file, int width, int height) {
        Log.i(LOG_TAG + "resolutionBitmap", "resolution compression begin");
        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file.getPath(), options);

        options.inSampleSize = calculateHighSampleSize(options, width, height);

        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getPath(), options);
        Log.i(LOG_TAG + "resolutionBitmap", "resolution compression end");

        return bitmap;
    }
}
