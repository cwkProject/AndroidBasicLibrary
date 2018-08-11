package org.cwk.android.library.network.factory;

import org.cwk.android.library.annotation.Delete;
import org.cwk.android.library.annotation.Download;
import org.cwk.android.library.annotation.Get;
import org.cwk.android.library.annotation.Post;
import org.cwk.android.library.annotation.Put;
import org.cwk.android.library.annotation.Upload;
import org.cwk.android.library.annotation.UploadStream;
import org.cwk.android.library.network.communication.ICommunication;
import org.cwk.android.library.network.communication.OkHttpCommunication;
import org.cwk.android.library.network.communication.OkHttpDeleteCommunication;
import org.cwk.android.library.network.communication.OkHttpDownloadCommunication;
import org.cwk.android.library.network.communication.OkHttpGetCommunication;
import org.cwk.android.library.network.communication.OkHttpPostCommunication;
import org.cwk.android.library.network.communication.OkHttpPutCommunication;
import org.cwk.android.library.network.communication.OkHttpStreamUploadCommunication;
import org.cwk.android.library.network.communication.OkHttpUploadCommunication;
import org.cwk.android.library.network.util.NetworkRefreshProgressHandler;
import org.cwk.android.library.network.util.NetworkTimeout;
import org.cwk.android.library.network.util.OnNetworkProgressListener;

import java.lang.reflect.Method;

import okhttp3.Headers;

import static org.cwk.android.library.network.factory.NetworkType.DELETE;
import static org.cwk.android.library.network.factory.NetworkType.DOWNLOAD;
import static org.cwk.android.library.network.factory.NetworkType.GET;
import static org.cwk.android.library.network.factory.NetworkType.POST;
import static org.cwk.android.library.network.factory.NetworkType.PUT;
import static org.cwk.android.library.network.factory.NetworkType.UPLOAD;
import static org.cwk.android.library.network.factory.NetworkType.UPLOAD_STREAM;

/**
 * 通讯对象构造器
 *
 * @author 超悟空
 * @version 4.0 2017/11/13
 * @since 1.0
 */
public class CommunicationBuilder {

    /**
     * 网络工具请求类型
     */
    private int networkType = GET;

    /**
     * 进度监听器，仅上传和下载时有效
     */
    private OnNetworkProgressListener progressListener = null;

    /**
     * 请求头信息
     */
    private Headers.Builder headers = null;

    /**
     * 请求重试次数
     */
    protected int retryTimes = 0;

    /**
     * 请求超时时间
     */
    private int connectTimeout = -1;

    /**
     * 读取超时时间
     */
    private int readTimeout = -1;

    /**
     * 写入超时时间
     */
    private int writeTimeout = -1;

    /**
     * 请求编码
     */
    private String encoded = null;

    /**
     * 跟踪日志
     */
    private final String logTag;

    /**
     * 新建网络工具构造器
     *
     * @param tag         标签，用于跟踪日志
     * @param networkType 网络请求类型
     */
    public CommunicationBuilder(String tag , int networkType) {
        this.logTag = tag;
        this.networkType = networkType;
    }

    /**
     * 新建网络工具构造器
     *
     * @param tag       标签，用于跟踪日志
     * @param workClass 任务类
     */
    public CommunicationBuilder(String tag , Class<?> workClass) {
        this.logTag = tag;
        this.networkType = onNetworkType(workClass);
    }

    /**
     * 设置网络请求类型<br>
     * 用于{@link #CommunicationBuilder(String , Class)}生产网络请求实例，
     * 默认为{@link NetworkType#GET}
     *
     * @return 网络请求类型枚举
     */
    private int onNetworkType(Class<?> workClass) {
        Class<?> thisClass = workClass;

        Method method = null;

        while (method == null) {
            for (Method name : thisClass.getDeclaredMethods()) {

                if (name.getName().equals("onTaskUri") && name.getParameterTypes().length == 0) {
                    method = name;
                    break;
                }
            }

            thisClass = thisClass.getSuperclass();
        }

        if (method.isAnnotationPresent(Get.class)) {
            return NetworkType.GET;
        }
        if (method.isAnnotationPresent(Post.class)) {
            return NetworkType.POST;
        }
        if (method.isAnnotationPresent(Download.class)) {
            return NetworkType.DOWNLOAD;
        }
        if (method.isAnnotationPresent(Upload.class)) {
            return NetworkType.UPLOAD;
        }
        if (method.isAnnotationPresent(Put.class)) {
            return NetworkType.PUT;
        }
        if (method.isAnnotationPresent(Delete.class)) {
            return NetworkType.DELETE;
        }
        if (method.isAnnotationPresent(UploadStream.class)) {
            return NetworkType.UPLOAD_STREAM;
        }

        return NetworkType.GET;
    }

    /**
     * 设置进度监听器，仅在上传和下载中有效
     *
     * @param progressListener 监听器对象
     *
     * @return 构造器
     */
    public CommunicationBuilder networkRefreshProgressListener(OnNetworkProgressListener
                                                                       progressListener) {
        this.progressListener = progressListener;
        return this;
    }

    /**
     * 设置连接超时时间
     *
     * @param timeout 超时时间，单位毫秒
     *
     * @return 构造器
     */
    public CommunicationBuilder connectTimeout(int timeout) {
        this.connectTimeout = timeout;
        return this;
    }

    /**
     * 设置读取超时时间
     *
     * @param timeout 超时时间，单位毫秒
     *
     * @return 构造器
     */
    public CommunicationBuilder readTimeout(int timeout) {
        this.readTimeout = timeout;
        return this;
    }

    /**
     * 设置写入超时时间
     *
     * @param timeout 超时时间，单位毫秒
     *
     * @return 构造器
     */
    public CommunicationBuilder writeTimeout(int timeout) {
        this.writeTimeout = timeout;
        return this;
    }

    /**
     * 设置重试次数
     *
     * @param times 重试次数，默认为0即不重试
     *
     * @return 构造器
     */
    public CommunicationBuilder retryTimes(int times) {
        this.retryTimes = times;
        return this;
    }

    /**
     * 设置请求编码，默认为utf-8
     *
     * @param encoded 编码
     *
     * @return 构造器
     */
    public CommunicationBuilder encoded(String encoded) {
        this.encoded = encoded;
        return this;
    }

    /**
     * 设置新的头信息，移除所有旧的头信息
     *
     * @param name  名称
     * @param value 值
     *
     * @return 构造器
     */
    public CommunicationBuilder header(String name , String value) {
        if (headers == null) {
            headers = new Headers.Builder();
        }
        headers.set(name , value);
        return this;
    }

    /**
     * 添加头信息
     *
     * @param name  名称
     * @param value 值
     *
     * @return 构造器
     */
    public CommunicationBuilder addHeader(String name , String value) {
        if (headers == null) {
            headers = new Headers.Builder();
        }
        headers.add(name , value);
        return this;
    }

    /**
     * 移除所有指定名称的头信息
     *
     * @param name 头名称
     *
     * @return 构造器
     */
    public CommunicationBuilder removeHeader(String name) {
        if (headers != null) {
            headers.removeAll(name);
        }
        return this;
    }

    /**
     * 移除所有头信息使用新的头
     *
     * @param headers OkHttp头信息对象
     *
     * @return 构造器
     */
    public CommunicationBuilder headers(Headers headers) {
        this.headers = headers.newBuilder();
        return this;
    }

    /**
     * 构造网络请求工具
     *
     * @return OKHttp网络请求工具
     */
    public ICommunication build() {

        OkHttpCommunication communication;

        switch (networkType) {
            case GET:
                communication = new OkHttpGetCommunication(logTag);
                break;
            case POST:
                communication = new OkHttpPostCommunication(logTag);
                break;
            case UPLOAD:
                communication = new OkHttpUploadCommunication(logTag);
                break;
            case DOWNLOAD:
                communication = new OkHttpDownloadCommunication(logTag);
                break;
            case PUT:
                communication = new OkHttpPutCommunication(logTag);
                break;
            case DELETE:
                communication = new OkHttpDeleteCommunication(logTag);
                break;
            case UPLOAD_STREAM:
                communication = new OkHttpStreamUploadCommunication(logTag);
                break;
            default:
                throw new IllegalArgumentException("error networkType");
        }

        if (connectTimeout + readTimeout + writeTimeout > -3) {
            // 需要设置时间

            NetworkTimeout networkTimeout = new NetworkTimeout();

            networkTimeout.setConnectTimeout(connectTimeout);
            networkTimeout.setReadTimeout(readTimeout);
            networkTimeout.setWriteTimeout(writeTimeout);

            communication.setNetworkTimeout(networkTimeout);
        }

        if (retryTimes > 0) {
            // 需要设置请求重试
            communication.setRetryTimes(retryTimes);
        }

        if (progressListener != null && communication instanceof NetworkRefreshProgressHandler) {
            // 需要设置监听器

            NetworkRefreshProgressHandler refreshProgressHandler =
                    (NetworkRefreshProgressHandler) communication;

            refreshProgressHandler.setNetworkProgressListener(progressListener);
        }

        communication.setEncoded(encoded);

        if (headers != null) {
            communication.setHeaders(headers.build());
        }

        return communication;
    }
}
