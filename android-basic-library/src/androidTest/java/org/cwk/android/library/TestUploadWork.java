package org.cwk.android.library;

import android.os.Environment;

import org.cwk.android.library.model.work.SimpleUploadWorkModel;
import org.cwk.android.library.struct.FileInfo;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.util.Map;

/**
 * 测试文件上传
 *
 * @author 超悟空
 * @version 1.0 2017/2/24
 * @since 1.0 2017/2/24
 **/
public class TestUploadWork {

    private class Upload extends SimpleUploadWorkModel<Void, Void> {

        @Override
        protected String onTaskUri(Void... parameters) {
            return "http://192.168.0.66:5080/MeetingServer/mobile/uploadFile";
        }

        @Override
        protected void onFill(Map<String, Object> dataMap, Void... parameters) {
            dataMap.put("file", new FileInfo(new File(Environment.getExternalStorageDirectory() +
                    "/download/znlh.apk")));
        }

        @Override
        protected Void onSuccessExtract(JSONObject jsonResult) throws Exception {
            return null;
        }
    }

    @Test
    public void name() throws Exception {

        new Upload().beginExecute();
    }
}
