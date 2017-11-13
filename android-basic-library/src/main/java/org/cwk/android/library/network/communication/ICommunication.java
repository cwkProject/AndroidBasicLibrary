package org.cwk.android.library.network.communication;

import org.cwk.android.library.network.util.AsyncCommunication;
import org.cwk.android.library.network.util.NetworkTimeoutHandler;
import org.cwk.android.library.network.util.SyncCommunication;

/**
 * 同时具有同步和异步请求能力的网络请求接口
 *
 * @author 超悟空
 * @version 1.0 2017/11/13
 * @since 1.0 2017/11/13
 **/
public interface ICommunication<RequestType, ResponseType> extends
        AsyncCommunication<RequestType, ResponseType>, SyncCommunication<RequestType,
        ResponseType>, NetworkTimeoutHandler {
}
