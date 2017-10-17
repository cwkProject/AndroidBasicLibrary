package org.cwk.android.library.common;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import org.cwk.android.library.global.Global;

/**
 * 通用列表分割线
 *
 * @author 超悟空
 * @version 1.0 2016/3/31
 * @since 1.0
 */
public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    /**
     * 默认分割线高度
     */
    private static final int DEFAULT_DIVIDER_SIZE = 2;

    /**
     * 画笔
     */
    private Paint mPaint = null;

    /**
     * 图片分割线
     */
    private Drawable mDivider = null;

    /**
     * 分割线高度
     */
    private int mDividerSize = DEFAULT_DIVIDER_SIZE;

    /**
     * 列表的方向：LinearLayoutManager.VERTICAL或LinearLayoutManager.HORIZONTAL
     */
    private int mOrientation = LinearLayoutManager.VERTICAL;

    /**
     * 距开始位置距离
     */
    private int startPadding = 0;

    /**
     * 距结束位置距离
     */
    private int endPadding = 0;

    /**
     * 默认分割线：高度为2px，颜色为灰色，列表为垂直
     */
    public RecyclerViewDivider() {
        this(LinearLayoutManager.VERTICAL);
    }

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param orientation 列表方向
     */
    public RecyclerViewDivider(int orientation) {
        this(orientation, Global.getContext().getResources().getColor(android.R.color.darker_gray));
    }

    /**
     * 默认分割线：高度为2px，颜色为灰色
     *
     * @param orientation  列表方向
     * @param startPadding 距开始位置距离
     * @param endPadding   距结束位置距离
     */
    public RecyclerViewDivider(int orientation, int startPadding, int endPadding) {
        this(orientation, Global.getContext().getResources().getColor(android.R.color
                .darker_gray), startPadding, endPadding);
    }

    /**
     * 自定义分割线
     *
     * @param orientation 列表方向
     * @param drawable    分割线图片
     */
    public RecyclerViewDivider(int orientation, Drawable drawable) {
        this(orientation, drawable, DEFAULT_DIVIDER_SIZE);
    }

    /**
     * 自定义分割线
     *
     * @param orientation  列表方向
     * @param drawable     分割线图片
     * @param startPadding 距开始位置距离
     * @param endPadding   距结束位置距离
     */
    public RecyclerViewDivider(int orientation, Drawable drawable, int startPadding, int
            endPadding) {
        this(orientation, drawable, DEFAULT_DIVIDER_SIZE, startPadding, endPadding);
    }

    /**
     * 自定义分割线
     *
     * @param orientation 列表方向
     * @param drawable    分割线图片
     * @param dividerSize 分割线大小
     */
    public RecyclerViewDivider(int orientation, Drawable drawable, int dividerSize) {
        this(orientation, drawable, dividerSize, 0, 0);
    }

    /**
     * 自定义分割线
     *
     * @param orientation  列表方向
     * @param drawable     分割线图片
     * @param dividerSize  分割线大小
     * @param startPadding 距开始位置距离
     * @param endPadding   距结束位置距离
     */
    public RecyclerViewDivider(int orientation, Drawable drawable, int dividerSize, int
            startPadding, int endPadding) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager
                .HORIZONTAL) {
            throw new IllegalArgumentException("orientation error");
        }
        mOrientation = orientation;
        mDivider = drawable;
        mDividerSize = dividerSize;
        this.startPadding = startPadding;
        this.endPadding = endPadding;
    }

    /**
     * 自定义分割线
     *
     * @param orientation  列表方向
     * @param dividerColor 分割线颜色
     */
    public RecyclerViewDivider(int orientation, int dividerColor) {
        this(orientation, dividerColor, DEFAULT_DIVIDER_SIZE);
    }

    /**
     * 自定义分割线
     *
     * @param orientation  列表方向
     * @param dividerColor 分割线颜色
     * @param startPadding 距开始位置距离
     * @param endPadding   距结束位置距离
     */
    public RecyclerViewDivider(int orientation, int dividerColor, int startPadding, int
            endPadding) {
        this(orientation, dividerColor, DEFAULT_DIVIDER_SIZE, startPadding, endPadding);
    }

    /**
     * 自定义分割线
     *
     * @param orientation  列表方向
     * @param dividerColor 分割线颜色
     * @param dividerSize  分割线大小
     * @param startPadding 距开始位置距离
     * @param endPadding   距结束位置距离
     */
    public RecyclerViewDivider(int orientation, int dividerColor, int dividerSize, int
            startPadding, int endPadding) {
        if (orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager
                .HORIZONTAL) {
            throw new IllegalArgumentException("orientation error");
        }
        mOrientation = orientation;

        mDividerSize = dividerSize;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(dividerColor);
        mPaint.setStyle(Paint.Style.FILL);
        this.startPadding = startPadding;
        this.endPadding = endPadding;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State
            state) {
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, mDividerSize);
        } else {
            outRect.set(0, 0, mDividerSize, 0);
        }
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        if (mOrientation == LinearLayoutManager.VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }
    }

    /**
     * 绘制纵向列表分割线
     *
     * @param canvas 画布
     * @param parent 画笔
     */
    private void drawVertical(Canvas canvas, RecyclerView parent) {
        final int left = parent.getPaddingLeft() + startPadding;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight() - endPadding;
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin;
            final int bottom = top + mDividerSize;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }

    /**
     * 绘制横向列表分割线
     *
     * @param canvas 画布
     * @param parent 画笔
     */
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        final int top = parent.getPaddingTop() + startPadding;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() - endPadding;
        final int childSize = parent.getChildCount();
        for (int i = 0; i < childSize; i++) {
            final View child = parent.getChildAt(i);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin;
            final int right = left + mDividerSize;
            if (mDivider != null) {
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(canvas);
            }
            if (mPaint != null) {
                canvas.drawRect(left, top, right, bottom, mPaint);
            }
        }
    }
}
