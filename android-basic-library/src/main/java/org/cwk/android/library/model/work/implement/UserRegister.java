package org.cwk.android.library.model.work.implement;
/**
 * Created by 超悟空 on 2016/3/21.
 */

import org.cwk.android.library.R;
import org.cwk.android.library.global.ApplicationAttribute;
import org.cwk.android.library.global.Global;
import org.cwk.android.library.model.data.implement.RegisterData;
import org.cwk.android.library.model.work.DefaultWorkModel;
import org.cwk.android.library.network.factory.NetworkType;

/**
 * 用户注册任务
 *
 * @author 超悟空
 * @version 1.0 2016/3/21
 * @since 1.0
 */
public class UserRegister extends DefaultWorkModel<String, String, RegisterData> {
    @Override
    protected boolean onCheckParameters(String... parameters) {
        return parameters != null && parameters.length == 4;
    }

    @Override
    protected String onTaskUri() {
        return ApplicationAttribute.getRegisterUrl();
    }

    @Override
    protected NetworkType onNetworkType() {
        return NetworkType.POST;
    }

    @Override
    protected String onParseFailedSetMessage(RegisterData data) {
        return Global.getContext().getString(R.string.register_error_field_required);
    }

    @Override
    protected String onRequestSuccessSetResult(RegisterData data) {
        return data.getUserId();
    }

    @Override
    protected RegisterData onCreateDataModel(String... parameters) {
        RegisterData data = new RegisterData();

        data.setUserName(parameters[0]);
        data.setMobile(parameters[1]);
        data.setPassword1(parameters[2]);
        data.setPassword2(parameters[3]);

        return data;
    }
}
