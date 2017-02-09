package org.cwk.android.library.util;
/**
 * Created by 超悟空 on 2015/12/10.
 */

import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Log;

import org.cwk.android.library.cache.util.CacheTool;
import org.cwk.android.library.common.function.ImageCompression;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 图片处理工具，与{@link CacheTool}高耦合，负责压缩图片和创建缓存
 *
 * @author 超悟空
 * @version 1.0 2015/12/10
 * @since 1.0
 */
public class ImageUtil {

    /**
     * 日志标签前缀
     */
    private static final String LOG_TAG = "ImageUtil.";

    /**
     * 原图缓存key前缀
     */
    public static final String SOURCE_IMAGE_CACHE_PRE = "source_";

    /**
     * 压缩后图片缓存key前缀
     */
    public static final String COMPRESSION_IMAGE_CACHE_PRE = "compression_";

    /**
     * 缩略图缓存key前缀
     */
    public static final String THUMBNAIL_CACHE_PRE = "thumbnail_";

    /**
     * 线程池线程数
     */
    private static final int POOL_COUNT = Runtime.getRuntime().availableProcessors() * 3 + 2;

    /**
     * 线程池
     */
    private static ExecutorService taskExecutor = Executors.newFixedThreadPool(POOL_COUNT);

    /**
     * 图片处理完成监听器
     */
    public interface ProcessFinishListener {

        /**
         * 图片处理完成
         *
         * @param cacheTool 存放处理后图片的缓存工具
         * @param key       处理后图片的缓存key(已包含前缀)，处理失败则返回null
         */
        void finish(CacheTool cacheTool, String key);
    }

    /**
     * 创建缩略图，异步方法
     *
     * @param file      原图文件
     * @param cacheTool 存放缩略图的缓存工具
     * @param key       要存放的缓存key（不含前缀）
     * @param width     缩略图宽
     * @param height    缩略图高
     * @param listener  处理完成监听器
     */
    public static void createThumbnail(@NotNull final File file, @NotNull final CacheTool
            cacheTool, @NotNull final String key, final int width, final int height, @Nullable
    final ProcessFinishListener listener) {

        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.finish(cacheTool, createThumbnail(file, cacheTool, key, width,
                            height));
                }
            }
        });
    }

    /**
     * 创建缩略图，同步方法
     *
     * @param file      原图文件
     * @param cacheTool 存放缩略图的缓存工具
     * @param key       要存放的缓存key（不含前缀）
     * @param width     缩略图宽
     * @param height    缩略图高
     *
     * @return 处理后图片的缓存key(已包含前缀)，处理失败则返回null
     */
    public static String createThumbnail(@NotNull File file, @NotNull CacheTool cacheTool,
                                         @NotNull String key, int width, int height) {
        Log.i(LOG_TAG + "createThumbnail", "image path:" + file.getPath() + " target cache key" +
                key);

        // 创建缩略图
        Log.i(LOG_TAG + "createThumbnail", "thumbnail begin");

        Bitmap bitmap = ImageCompression.resolutionBitmap(file, width, height);

        if (bitmap != null) {
            cacheTool.put(THUMBNAIL_CACHE_PRE + key, bitmap);
            Log.i(LOG_TAG + "createThumbnail", "thumbnail end");
            return THUMBNAIL_CACHE_PRE + key;
        } else {
            Log.d(LOG_TAG + "createThumbnail", "thumbnail failed");
            return null;
        }
    }

    /**
     * 处理图片，同时进行像素压缩和质量压缩，大幅缩小图片体积
     *
     * @param file      要处理的原图文件
     * @param cacheTool 存放压缩图的缓存工具
     * @param key       要存放的缓存key（不含前缀）
     * @param width     压缩图宽
     * @param height    压缩图高
     * @param size      目标容量，单位KB
     * @param listener  处理完成监听器
     */
    public static void processPicture(@NotNull final File file, @NotNull final CacheTool
            cacheTool, final String key, final int width, final int height, final int size,
                                      @Nullable final ProcessFinishListener listener) {
        Log.i(LOG_TAG + "processPicture", "image path:" + file.getPath() + " target cache key" +
                key);

        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {

                // 像素压缩
                Bitmap bitmap = ImageCompression.resolutionBitmap(file, width, height);

                if (bitmap == null) {
                    //压缩失败
                    if (listener != null) {
                        listener.finish(cacheTool, null);
                    }
                    return;
                }

                if (listener != null) {
                    listener.finish(cacheTool, qualityBitmap(cacheTool, key, bitmap, size));
                }
            }
        });
    }

    /**
     * 质量压缩，同步方法，与resolutionBitmap方法不兼容，缓存会相互覆盖
     *
     * @param cacheTool 存放压缩图的缓存工具
     * @param key       要存放的缓存key（不含前缀）
     * @param bitmap    要压缩的图片
     * @param size      目标容量，单位KB
     *
     * @return 处理后图片的缓存key(已包含前缀)，处理失败则返回null
     */
    public static String qualityBitmap(@NotNull CacheTool cacheTool, String key, Bitmap bitmap,
                                       int size) {
        Log.i(LOG_TAG + "qualityBitmap", "quality compression begin");
        // 进行质量压缩
        ByteArrayOutputStream byteArrayOutputStream = ImageCompression.compressImage(bitmap, size);

        // 获取一个缓存位置
        String newKey = COMPRESSION_IMAGE_CACHE_PRE + key;
        FileOutputStream fileOutputStream = cacheTool.putBackStream(newKey);

        try {
            byteArrayOutputStream.writeTo(fileOutputStream);

            byteArrayOutputStream.flush();

            byteArrayOutputStream.close();

            fileOutputStream.flush();

            fileOutputStream.close();

        } catch (IOException e) {
            key = null;
            Log.e(LOG_TAG + "qualityBitmap", "IOException is " + e.getMessage());
        }

        Log.i(LOG_TAG + "qualityBitmap", "quality compression end");
        return key;
    }

    /**
     * 质量压缩，异步方法，与resolutionBitmap方法不兼容，缓存会相互覆盖
     *
     * @param cacheTool 存放压缩图的缓存工具
     * @param key       要存放的缓存key（不含前缀）
     * @param bitmap    要压缩的图片
     * @param size      目标容量，单位KB
     * @param listener  处理完成监听器
     */
    public static void qualityBitmap(@NotNull final CacheTool cacheTool, final String key, final
    Bitmap bitmap, final int size, @Nullable final ProcessFinishListener listener) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.finish(cacheTool, qualityBitmap(cacheTool, key, bitmap, size));
                }
            }
        });
    }

    /**
     * 像素压缩，异步方法，与qualityBitmap方法不兼容，缓存会相互覆盖
     *
     * @param file      原图文件
     * @param cacheTool 存放压缩图的缓存工具
     * @param key       要存放的缓存key（不含前缀）
     * @param width     压缩图宽
     * @param height    压缩图高
     * @param listener  处理完成监听器
     */
    public static void resolutionBitmap(@NotNull final File file, @NotNull final CacheTool
            cacheTool, @NotNull final String key, final int width, final int height, @Nullable
    final ProcessFinishListener listener) {
        taskExecutor.submit(new Runnable() {
            @Override
            public void run() {
                if (listener != null) {
                    listener.finish(cacheTool, resolutionBitmap(file, cacheTool, key, width,
                            height));
                }
            }
        });
    }

    /**
     * 像素压缩，同步方法，与qualityBitmap方法不兼容，缓存会相互覆盖
     *
     * @param file      原图文件
     * @param cacheTool 存放压缩图的缓存工具
     * @param key       要存放的缓存key（不含前缀）
     * @param width     压缩图宽
     * @param height    压缩图高
     *
     * @return 处理后图片的缓存key(已包含前缀)，处理失败则返回null
     */
    public static String resolutionBitmap(@NotNull File file, @NotNull CacheTool cacheTool,
                                          @NotNull String key, int width, int height) {
        Log.i(LOG_TAG + "resolutionBitmap", "image path:" + file.getPath() + " target cache key" +
                key);

        // 创建缩略图
        Log.i(LOG_TAG + "resolutionBitmap", "quality compression");

        Bitmap bitmap = ImageCompression.resolutionBitmap(file, width, height);

        if (bitmap != null) {
            cacheTool.put(COMPRESSION_IMAGE_CACHE_PRE + key, bitmap);
            Log.i(LOG_TAG + "resolutionBitmap", "quality compression");
            return COMPRESSION_IMAGE_CACHE_PRE + key;
        } else {
            Log.d(LOG_TAG + "resolutionBitmap", "quality compression");
            return null;
        }
    }

    /**
     * 当前的缓存工具
     */
    private CacheTool cacheTool = null;

    /**
     * 当前要压缩的固定宽度
     */
    private int width = 0;

    /**
     * 当前要压缩的固定高度
     */
    private int height = 0;

    /**
     * 当前缩略图的固定宽度
     */
    private int thumbnailWidth = 0;

    /**
     * 当前缩略图的固定高度
     */
    private int thumbnailHeight = 0;

    /**
     * 当前要压缩的目标大小，单位KB
     */
    private int size = 0;

    /**
     * 创建一个图片工具实例，使用同一个缓存工具，维持固定压缩比率，值为0表示不压缩该项
     *
     * @param cacheTool       缓存工具
     * @param width           压缩图宽
     * @param height          压缩图高
     * @param size            目标容量，单位KB
     * @param thumbnailWidth  缩略图宽
     * @param thumbnailHeight 缩略图高
     */
    public ImageUtil(@NotNull CacheTool cacheTool, int width, int height, int size, int
            thumbnailWidth, int thumbnailHeight) {
        this.cacheTool = cacheTool;
        this.width = width;
        this.height = height;
        this.size = size;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    /**
     * 创建缩略图，异步方法
     *
     * @param file     原图文件
     * @param key      要存放的缓存key（不含前缀）
     * @param listener 处理完成监听器
     */
    public void createThumbnail(@NotNull File file, @NotNull String key, @Nullable final
    ProcessFinishListener listener) {
        createThumbnail(file, cacheTool, key, thumbnailWidth, thumbnailHeight, listener);
    }

    /**
     * 创建缩略图，同步方法
     *
     * @param file 原图文件
     * @param key  要存放的缓存key（不含前缀）
     *
     * @return 处理后图片的缓存key(已包含前缀)，处理失败则返回null
     */
    public String createThumbnail(@NotNull File file, @NotNull String key) {
        return createThumbnail(file, cacheTool, key, thumbnailWidth, thumbnailHeight);
    }

    /**
     * 处理图片，同时进行像素压缩和质量压缩，大幅缩小图片体积
     *
     * @param file     要处理的原图文件
     * @param key      要存放的缓存key（不含前缀）
     * @param listener 处理完成监听器
     */
    public void processPicture(@NotNull File file, @NotNull String key, @Nullable
            ProcessFinishListener listener) {
        processPicture(file, cacheTool, key, width, height, size, listener);
    }

    /**
     * 质量压缩，同步方法，与resolutionBitmap方法不兼容，缓存会相互覆盖
     *
     * @param key    要存放的缓存key（不含前缀）
     * @param bitmap 要压缩的图片
     *
     * @return 处理后图片的缓存key(已包含前缀)，处理失败则返回null
     */
    public String qualityBitmap(String key, Bitmap bitmap) {
        return qualityBitmap(cacheTool, key, bitmap, size);
    }

    /**
     * 质量压缩，异步方法，与resolutionBitmap方法不兼容，缓存会相互覆盖
     *
     * @param key      要存放的缓存key（不含前缀）
     * @param bitmap   要压缩的图片
     * @param listener 处理完成监听器
     */
    public void qualityBitmap(String key, Bitmap bitmap, @Nullable ProcessFinishListener listener) {
        qualityBitmap(cacheTool, key, bitmap, size);
    }

    /**
     * 像素压缩，异步方法，与qualityBitmap方法不兼容，缓存会相互覆盖
     *
     * @param file     原图文件
     * @param key      要存放的缓存key（不含前缀）
     * @param listener 处理完成监听器
     */
    public void resolutionBitmap(@NotNull File file, @NotNull String key, @Nullable
            ProcessFinishListener listener) {
        resolutionBitmap(file, cacheTool, key, width, height, listener);
    }

    /**
     * 像素压缩，同步方法，与qualityBitmap方法不兼容，缓存会相互覆盖
     *
     * @param file 原图文件
     * @param key  要存放的缓存key（不含前缀）
     *
     * @return 处理后图片的缓存key(已包含前缀)，处理失败则返回null
     */
    public String resolutionBitmap(@NotNull File file, @NotNull String key) {
        return resolutionBitmap(file, cacheTool, key, width, height);
    }
}
