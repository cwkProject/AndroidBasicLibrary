package org.cwk.android.library;

import android.util.Log;

import org.cwk.android.library.model.data.WorkResult;
import org.cwk.android.library.model.work.SimpleWorkModel;
import org.cwk.android.library.model.work.WorkFactory;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Map;

import io.reactivex.functions.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * 测试{@link WorkFactory}
 *
 * @author 超悟空
 * @version 1.0 2017/4/3
 * @since 1.0
 */
public class TestRxAndroidWorkFactory {

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
            Log.v("TestRxAndroidWorkFactory.TestWork.onSuccess", "thread id:" + Thread
                    .currentThread().getId());
        }

        @Override
        protected String onTaskUri() {
            return "http://218.92.115.55/WlkgbsgsApp/Service/test.aspx";
        }
    }

    @Test
    public void test() throws Exception {

        final Integer LOCK = 1;

        WorkFactory.from(TestWork.class).observable("123").subscribe(new Consumer<WorkResult<String>>() {
            @Override
            public void accept(WorkResult<String> stringWorkResult) throws Exception {
                Log.v("TestRxAndroidWorkFactory.test", "state:" + stringWorkResult.isSuccess() +
                        " ;" + "" + "" + "" + "" + "" + "" + "" + "" + "result:" +
                        stringWorkResult.getResult());
                Log.v("TestRxAndroidWorkFactory.test", "thread id:" + Thread.currentThread()
                        .getId());
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
