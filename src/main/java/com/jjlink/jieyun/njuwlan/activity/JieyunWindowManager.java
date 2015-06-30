package com.jjlink.jieyun.njuwlan.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.jjlink.jieyun.R;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;

/**
 * Created by zlu on 5/13/15.
 */
public class JieyunWindowManager{
    /**
     * 小悬浮窗view的实例
     */
    private static FloatWindowService.FloatWindowSmallView smallWindow;
    /**
     * 小悬浮窗view的参数
     */
    private static WindowManager.LayoutParams smallWindowParams;
    /**
     * 用于控制在屏幕上添加或移出悬浮窗
     */
    private static WindowManager mWindowManager;

    /**
     * 创建一个小悬浮窗，初始位置为屏幕左边中部位置
     *
     * @param context 必须为应用程序的context
     */
    public static void createSmallWindow(Context context) {
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWindow == null) {
            //内部类的实例化方法
            FloatWindowService floatWindowService=new FloatWindowService();
            smallWindow =floatWindowService.new FloatWindowSmallView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new WindowManager.LayoutParams();
                smallWindowParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = smallWindow.viewWidth;
                smallWindowParams.height = smallWindow.viewHeight;
                smallWindowParams.x = 0;
                smallWindowParams.y = screenHeight / 2;
            }

            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }
    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context
     */
    public static void removeSmallWindow(Context context) {
        if (smallWindow != null) {
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }


    /**
     * 创建一个大悬浮窗，初始位置为小悬浮窗位置
     *
     * @param context 必须为应用程序的context
     */
    public static void createBigWindow(Context context) {

    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }

    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null;
    }

    public static void updateIconSmallWindow(Context context) {
        if (smallWindow != null) {
            Button button = (Button) smallWindow.findViewById(R.id.float_id);
            //是否连网
            SharedPreferences sp= ContextUtil.getInstance().getSharedPreferences("NETSTAT",Context.MODE_WORLD_READABLE);
            //if (ContextUtil.getInstance().isNetwork()) {
            if (sp.getBoolean("NETWORKENABLE",false)) {
                button.setBackgroundResource(R.drawable.logo);
            }else{
                button.setBackgroundResource(R.drawable.portal_unconnect);
            }
        }
    }

    public static void setWindowInvisable(Context context) {
        if (smallWindow != null) {
            smallWindow.setVisibility(View.INVISIBLE);
        }
    }

}
