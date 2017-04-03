package org.cwk.android.library;

import android.util.Log;

import org.cwk.android.library.model.data.WorkResult;
import org.cwk.android.library.model.work.RxAndroidWorkUtil;
import org.cwk.android.library.model.work.SimpleWorkModel;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 测试{@link RxAndroidWorkUtil}
 *
 * @author 超悟空
 * @version 1.0 2017/4/3
 * @since 1.0
 */
public class TestRxAndroidWorkUtil {

    public static class TestWork extends SimpleWorkModel<String, String> {
        @Override
        protected void onFill(Map<String, String> dataMap, String... parameters) {
            dataMap.put("Data", "{\"state\":true,\"message\":null,\"result\":\"" + parameters[0]
                    + "\"}");
        }

        @Override
        protected String onSuccessExtract(JSONObject jsonResult) throws Exception {
            return jsonResult.getString(RESULT_TAG);
        }

        @Override
        protected void onSuccess() {
            Log.v("TestRxAndroidWorkUtil.TestWork.onSuccess", "thread id:" + Thread.currentThread
                    ().getId());
        }

        @Override
        protected String onTaskUri() {
            return "http://218.92.115.55/WlkgbsgsApp/Service/test.aspx";
        }
    }

    @Test
    public void test() throws Exception {

        final Integer LOCK = 1;

        RxAndroidWorkUtil.from(TestWork.class).createObservable("123").subscribe(new Consumer<WorkResult<String>>() {
            @Override
            public void accept(@NonNull WorkResult<String> stringWorkResult) throws Exception {
                Log.v("TestRxAndroidWorkUtil.test", "state:" + stringWorkResult.isSuccess() + " ;" +
                        "" + "" + "" + "" + "" + "" + "" + "" + "result:" + stringWorkResult
                        .getResult());
                Log.v("TestRxAndroidWorkUtil.test", "thread id:" + Thread.currentThread().getId());
                assertTrue(stringWorkResult.isSuccess());
                assertEquals("123", stringWorkResult.getResult().trim());

                synchronized (LOCK) {
                    LOCK.notify();
                }
            }
        });

        synchronized (LOCK) {
            LOCK.wait();
        }
    }
}
