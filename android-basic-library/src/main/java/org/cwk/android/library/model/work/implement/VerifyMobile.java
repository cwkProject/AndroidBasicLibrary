package org.cwk.android.library.model.work.implement;
/**
 * Created by 超悟空 on 2016/3/21.
 */

import org.cwk.android.library.R;
import org.cwk.android.library.global.ApplicationStaticValue;
import org.cwk.android.library.global.Global;
import org.cwk.android.library.model.data.implement.VerificationMobileData;
import org.cwk.android.library.model.work.DefaultWorkModel;
import org.cwk.android.library.network.factory.NetworkType;

/**
 * 验证手机号任务
 *
 * @author 超悟空
 * @version 1.0 2016/3/21
 * @since 1.0
 */
public class VerifyMobile extends DefaultWorkModel<String, String, VerificationMobileData> {
    @Override
    protected boolean onCheckParameters(String... parameters) {
        return parameters != null && parameters.length == 2;
    }

    @Override
    protected NetworkType onNetworkType() {
        return NetworkType.POST;
    }

    @Override
    protected String onTaskUri() {
        return ApplicationStaticValue.Url.VERIFY_MOBILE_URL;
    }

    @Override
    protected String onRequestSuccessSetResult(VerificationMobileData data) {
        return null;
    }

    @Override
    protected String onParseFailedSetMessage(VerificationMobileData data) {
        return Global.getContext().getString(R.string.verify_error_field_required);
    }

    @Override
    protected VerificationMobileData onCreateDataModel(String... parameters) {
        VerificationMobileData data = new VerificationMobileData();
        data.setMobile(parameters[0]);
        data.setVerificationCode(parameters[1]);
        return data;
    }
}
