package org.cwk.android.library.common;

import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import org.cwk.android.library.R;

/**
 * toolbar初始化工具，须在布局中引入{@link org.cwk.android.library.R.layout#toolbar}布局
 *
 * @author 超悟空
 * @version 1.0 2016/7/29
 * @since 1.0
 */
public class ToolbarInitialize {

    /**
     * 初始化Toolbar<br>
     * 须在布局中引入{@link org.cwk.android.library.R.layout#toolbar}布局
     *
     * @param activity Activity
     * @param titleId  标题资源id
     * @param center   true表示标题居中，false表示标题居左
     * @param back     true表示有返回按钮，false表示无返回按钮
     *
     * @return 初始化完成的Toolbar对象
     */
    @SuppressWarnings("ConstantConditions")
    public static Toolbar initToolbar(final AppCompatActivity activity, @StringRes int titleId,
                                      boolean center, boolean back) {
        Toolbar toolbar = (Toolbar) activity.findViewById(R.id.toolbar);

        if (toolbar == null) {
            return null;
        }

        activity.setSupportActionBar(toolbar);
        activity.setTitle(titleId);

        if (center) {
            TextView title = (TextView) toolbar.findViewById(R.id.toolbar_title);

            if (title != null) {
                title.setVisibility(View.VISIBLE);
                title.setText(titleId);
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }

        if (back) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.finish();
                }
            });
        }

        return toolbar;
    }

    /**
     * 初始化Toolbar，带有返回按钮，标题局左<br>
     * 须在布局中引入{@link org.cwk.android.library.R.layout#toolbar}布局
     *
     * @param activity Activity
     * @param titleId  标题资源id
     *
     * @return 初始化完成的Toolbar对象
     */
    public static Toolbar initToolbar(AppCompatActivity activity, @StringRes int titleId) {
        return initToolbar(activity, titleId, false);
    }

    /**
     * 初始化Toolbar，带有返回按钮<br>
     * 须在布局中引入{@link org.cwk.android.library.R.layout#toolbar}布局
     *
     * @param activity Activity
     * @param titleId  标题资源id
     * @param center   true表示标题居中，false表示标题居左
     *
     * @return 初始化完成的Toolbar对象
     */
    public static Toolbar initToolbar(AppCompatActivity activity, @StringRes int titleId, boolean
            center) {
        return initToolbar(activity, titleId, center, true);
    }
}
