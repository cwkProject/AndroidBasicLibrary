package org.cwk.android.library.common.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;

import org.cwk.android.library.R;


/**
 * 带有上拉加载更多的刷新器
 *
 * @author 超悟空
 * @version 1.0 2015/1/7
 * @since 1.0
 */
public class RefreshLayout extends SwipeRefreshLayout {

    /**
     * 日志前缀
     */
    private static final String LOG_TAG = "RefreshLayout.";

    /**
     * 上拉监听器，到了最底部的上拉加载操作
     */
    private OnRefreshListener mListener;

    /**
     * 正在加载的布局
     */
    private View loadMoreView;

    /**
     * 可滚动主布局
     */
    private View targetView;

    /**
     * 是否在加载中（上拉加载更多）
     */
    private boolean isLoading = false;

    /**
     * 设置是否开启加载更多功能
     */
    private boolean enabledLoad = false;

    private int mActivePointerId = -1;

    private boolean mIsBeingDragged = false;

    private float mInitialDownY = -1;

    private int mTouchSlop = 0;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 创建加载更多的布局
     */
    private void createLoadMoreView() {
        loadMoreView = LayoutInflater.from(getContext()).inflate(R.layout.load_more_layout, this,
                false);

        if (targetView instanceof RecyclerView) {
            loadMoreView.setBackgroundColor(Color.WHITE);
        }

        loadMoreView.setVisibility(GONE);
        addView(loadMoreView, getChildCount());
    }

    /**
     * 获取目标滚动主布局
     */
    private void ensureTarget() {
        if (targetView == null) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (child instanceof AbsListView || child instanceof ScrollingView) {
                    targetView = child;
                    break;
                }
            }
        }
    }

    /**
     * 判断是否到底部
     *
     * @return true表示到底
     */
    private boolean isBottom() {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (targetView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) targetView;
                return absListView.getCount() == 0 || (absListView.getCount() > 0 && absListView
                        .getChildAt(absListView.getChildCount() - 1).getBottom() == absListView
                        .getHeight() - absListView.getPaddingBottom());
            } else {
                return !(ViewCompat.canScrollVertically(targetView, 1) || targetView.getScrollY()
                        < 0) && (ViewCompat.canScrollVertically(targetView, -1) || targetView
                        .getScrollY() > 0);
            }
        } else {

            return !ViewCompat.canScrollVertically(targetView, 1) && ViewCompat
                    .canScrollVertically(targetView, -1);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        ensureTarget();

        if (!isEnabled() || !enabledLoad || isLoading || !isBottom()) {
            return super.onInterceptTouchEvent(ev);
        }

        final int action = MotionEventCompat.getActionMasked(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mIsBeingDragged = false;
                final float initialDownY = getMotionEventY(ev, mActivePointerId);
                if (initialDownY == -1) {
                    return false;
                }

                mInitialDownY = initialDownY;
                break;

            case MotionEvent.ACTION_MOVE:
                if (mActivePointerId == -1) {
                    Log.e(LOG_TAG + "onInterceptTouchEvent", "Got ACTION_MOVE event but don't " +
                            "have an active pointer id.");
                    return false;
                }

                final float y = getMotionEventY(ev, mActivePointerId);

                if (y == -1) {
                    return false;
                }
                final float yDiff = y - mInitialDownY;

                if (yDiff < -mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true;
                    setLoading(true);
                }
                break;
            case MotionEventCompat.ACTION_POINTER_UP:
                onSecondaryPointerUp(ev);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mIsBeingDragged = false;
                mActivePointerId = -1;
                break;
        }

        return mIsBeingDragged;
    }

    private float getMotionEventY(MotionEvent ev, int activePointerId) {
        final int index = MotionEventCompat.findPointerIndex(ev, activePointerId);
        if (index < 0) {
            return -1;
        }
        return MotionEventCompat.getY(ev, index);
    }

    private void onSecondaryPointerUp(MotionEvent ev) {
        final int pointerIndex = MotionEventCompat.getActionIndex(ev);
        final int pointerId = MotionEventCompat.getPointerId(ev, pointerIndex);
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex);
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (enabledLoad && loadMoreView != null) {
            loadMoreView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth() - getPaddingLeft
                    () - getPaddingRight(), MeasureSpec.EXACTLY), LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (enabledLoad && loadMoreView != null) {
            int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
            int height = loadMoreView.getMeasuredHeight();

            loadMoreView.layout(getPaddingLeft(), getMeasuredHeight() - getPaddingBottom() -
                    height, width + getPaddingLeft(), getMeasuredHeight() - getPaddingBottom());
        }
    }

    /**
     * 设置开启或结束加载
     *
     * @param loading true表示开始加载
     */
    public void setLoading(boolean loading) {
        ensureTarget();

        if (targetView != null && mListener != null && loading && !isLoading) {
            isLoading = true;

            if (isRefreshing()) {
                setRefreshing(false);
            }

            if (!isBottom()) {
                if (targetView instanceof AbsListView) {

                    AbsListView absListView = (AbsListView) targetView;

                    absListView.setSelection(absListView.getAdapter().getCount() - 1);
                } else {
                    if (targetView instanceof ViewGroup) {

                        ViewGroup viewGroup = (ViewGroup) targetView;

                        viewGroup.scrollTo(0, viewGroup.getChildAt(viewGroup.getChildCount() - 1)
                                .getBottom() + getPaddingBottom());
                    }
                }
            }

            if (targetView instanceof RecyclerView) {
                ViewPropertyAnimator animator = loadMoreView.animate().translationY(0)
                        .setDuration(200);
                animator.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loadMoreView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mListener.onRefresh();
                    }
                });
                animator.start();
            } else {
                targetView.scrollBy(0, loadMoreView.getMeasuredHeight());
                loadMoreView.setVisibility(VISIBLE);
                mListener.onRefresh();
            }
        }

        if (!loading) {

            if (isLoading && targetView instanceof RecyclerView) {
                ViewPropertyAnimator animator = loadMoreView.animate().translationY(loadMoreView
                        .getHeight()).setDuration(200);
                animator.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        loadMoreView.setVisibility(View.GONE);
                        isLoading = false;
                    }
                });
                animator.start();
            } else {
                loadMoreView.setVisibility(GONE);
                if (isLoading) {
                    targetView.scrollBy(0, -loadMoreView.getMeasuredHeight());
                }
                isLoading = false;
            }
        }
    }

    /**
     * 设置上拉监听器，且自动开启加载更多功能
     *
     * @param loadListener 上拉监听
     */
    public void setOnLoadListener(OnRefreshListener loadListener) {
        mListener = loadListener;

        ensureTarget();
        enabledLoad = true;
        createLoadMoreView();
        invalidate();
    }

    /**
     * 设置是否开启加载更多功能
     *
     * @param enabledLoad true表示开启，默认false
     */
    public void setEnabledLoad(boolean enabledLoad) {
        this.enabledLoad = enabledLoad;
    }
}
