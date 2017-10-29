package org.cwk.android.library.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import org.cwk.android.library.model.operate.BackHandle;

import java.util.List;

/**
 * 处理Fragment返回监听帮助类
 *
 * @author 超悟空
 * @version 1.0 2017/10/29
 * @since 1.0
 */
public class BackHandleHelper {

    /**
     * 将back事件分发给 FragmentManager 中管理的子Fragment
     *
     * @return 如果处理了back键则返回true
     */
    private static boolean handleBackPress(FragmentManager fragmentManager) {
        List<Fragment> fragments = fragmentManager.getFragments();

        if (fragments != null && fragments.size() > 0) {
            for (Fragment fragment : fragments) {
                if (isFragmentBackHandled(fragment)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 将返回事件向下传递给子Fragment
     *
     * @param fragment 当前Fragment
     *
     * @return 如果子Fragment处理了back键则返回true
     */
    public static boolean handleBackPress(Fragment fragment) {
        return handleBackPress(fragment.getChildFragmentManager());
    }

    /**
     * 将返回事件向下传递给Fragment
     *
     * @param fragmentActivity 当前Activity
     *
     * @return 如果Fragment处理了back键则返回true
     */
    public static boolean handleBackPress(FragmentActivity fragmentActivity) {
        return handleBackPress(fragmentActivity.getSupportFragmentManager());
    }

    /**
     * 判断Fragment是否处理了Back键
     *
     * @return 如果处理了back键则返回true
     */
    private static boolean isFragmentBackHandled(Fragment fragment) {
        return fragment != null && fragment.isVisible() && fragment.getUserVisibleHint() &&
                fragment instanceof BackHandle && ((BackHandle) fragment).onBackPressed();
    }
}
