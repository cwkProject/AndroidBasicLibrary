package org.cwk.android.library.model.work;

import org.cwk.android.library.model.data.IIntegratedDataModel;
import org.cwk.android.library.network.communication.Communication;

/**
 * 集成式网络任务模型基类<br>
 * 内部使用{@link IIntegratedDataModel}作为默认的数据模型类，
 * 使用{@link Communication}作为网络请求工具<br>
 *
 * @author 超悟空
 * @version 1.0 2016/7/23
 * @since 1.0
 */
public abstract class IntegratedWorkModel<Parameters, Result, DataModel extends
        IIntegratedDataModel<Parameters, Result, ?, ?>> extends DefaultWorkModel<Parameters,
        Result, DataModel> {

    @SafeVarargs
    @Override
    protected final DataModel onCreateDataModel(Parameters... parameters) {
        DataModel data = onCreateDataModel();
        data.setParameters(parameters);
        return data;
    }

    /**
     * 创建数据模型对象并填充参数
     *
     * @return 参数设置完毕后的数据模型对象
     */
    protected abstract DataModel onCreateDataModel();
}
