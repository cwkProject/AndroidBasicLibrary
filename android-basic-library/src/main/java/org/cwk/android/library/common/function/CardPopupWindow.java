package org.cwk.android.library.common.function;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import org.cwk.android.library.R;


/**
 * 自定义卡片弹出框
 *
 * @author 超悟空
 * @version 1.0 2016/7/29
 * @since 1.0
 */
public class CardPopupWindow {

    /**
     * 用于显示选择列表的窗口
     */
    public PopupWindow popupWindow = null;

    /**
     * 弹出窗口的内容布局
     */
    public CardView cardView = null;

    /**
     * 构造函数
     *
     * @param context 上下文
     */
    public CardPopupWindow(Context context) {
        // 弹出窗口布局
        cardView = (CardView) LayoutInflater.from(context).inflate(R.layout.layout_popup_window,
                null);
        popupWindow = new PopupWindow(context);

        initPopupWindow(context);
    }

    /**
     * 初始化弹出框
     */
    private void initPopupWindow(Context context) {
        popupWindow.setContentView(cardView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(context.getResources().getDimensionPixelOffset(R.dimen
                .filter_popup_window_height));
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    /**
     * 显示PopupWindow
     *
     * @param anchor 依附的布局
     * @param view   要显示的布局
     */
    public void showPopupWindow(View anchor, View view) {
        if (!popupWindow.isShowing()) {
            cardView.removeAllViews();
            cardView.addView(view);
            popupWindow.showAsDropDown(anchor);
        }
    }

    /**
     * 关闭弹出框
     */
    public void dismiss() {
        popupWindow.dismiss();
    }
}
