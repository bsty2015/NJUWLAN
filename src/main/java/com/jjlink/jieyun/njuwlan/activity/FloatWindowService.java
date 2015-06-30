package com.jjlink.jieyun.njuwlan.activity;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import com.jjlink.jieyun.R;
import com.jjlink.jieyun.njuwlan.log.JieyunLog;
import com.jjlink.jieyun.njuwlan.service.DaemonService;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zlu on 5/13/15.
 */
public class FloatWindowService extends Service {
    /**
     * 用于在线程中创建或移除悬浮窗
     */
    private Handler handler = new Handler();
    JieyunLog logger = new JieyunLog();

    /**
     * 定时器，定时进行检测当前应该创建还是移出悬浮窗
     */
    private Timer timer;

    //悬浮窗布局
    LinearLayout mFloatWindow;
    WindowManager.LayoutParams wmParams;
    //创建悬浮窗口设置布局的对象
    WindowManager mWindowManager;

    Button mFloatView;

    private static final String TAG = "FloatWindowService";


   /* private void createFloatWindow() {
        wmParams = new WindowManager.LayoutParams();
        //获取的是WindowManagerImpl.ComopatModeWrapper
        mWindowManager = (WindowManager) getApplication().getSystemService(getApplication().WINDOW_SERVICE);
        Log.i(TAG, "WindowManager--->" + mWindowManager);
        //设置window type
        wmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        //设置图片格式，效果为背景透明
        wmParams.format = PixelFormat.RGBA_8888;
        //设置悬浮窗口不可聚焦（实现操作除悬浮窗口外的其他可见窗口的操作）
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE ;
        //调整悬浮窗显示的停靠位置为左侧置顶
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //一屏幕左上角为原点，设置x,y的值，相对于gravity
        wmParams.x = 0;
        wmParams.y = 0;

        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        LayoutInflater inflater = LayoutInflater.from(getApplication());
        //获取悬浮窗口视图所在布局
        mFloatWindow = (LinearLayout) inflater.inflate(R.layout.float_window, null);
        //添加mFloatWindow
        mWindowManager.addView(mFloatWindow, wmParams);
        //浮动窗口按钮
        mFloatView = (Button) mFloatWindow.findViewById(R.id.float_id);
        mFloatWindow.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );

        Log.i(TAG, "Width/2--->" + mFloatView.getMeasuredWidth() / 2);
        Log.i(TAG, "Height/2--->" + mFloatView.getMeasuredHeight() / 2);

        //设置监听浮动窗口的触摸移动
        mFloatView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //getRawX是触摸位置相对于屏幕的坐标，getX是相对于按钮的坐标
                wmParams.x = (int) (event.getRawX() - mFloatView.getMeasuredWidth() / 2);
                Log.i(TAG, "RawX:" + event.getRawX());
                Log.i(TAG, "x:" + event.getX());
                //减25为状态栏的高度
                wmParams.y = (int) (event.getRawY() - mFloatView.getMeasuredHeight() / 2 - 25);
                Log.i(TAG, "RawY:" + event.getRawY());
                Log.i(TAG, "Y:" + event.getY());
                //刷新
                mWindowManager.updateViewLayout(mFloatWindow, wmParams);
                return false;//此处必须返回false，否则OnClickListener获取不到监听
            }
        });

        mFloatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(FloatWindowService.this, "Onclick", Toast.LENGTH_SHORT).show();
            }
        });

    }*/


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logger.debug("悬浮窗服务启动了。。。");
        if (timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new RefreshTask(), 0, 500);
        }
        return START_REDELIVER_INTENT;

    }

    class RefreshTask extends TimerTask {
        @Override
        public void run() {
            //守护线程
            boolean daemonService=StartPage.isServiceWorked(FloatWindowService.this,"com.jjlink.service.DaemonService");
            if(!daemonService){
                startService(new Intent(FloatWindowService.this, DaemonService.class));
                //Log.e("FloatWindowService","Start FloatWindowService");
            }
            // 当前界面是桌面，且没有悬浮窗显示，则创建悬浮窗。
            if (isHome() && !JieyunWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JieyunWindowManager.createSmallWindow(getApplication());
                    }
                });
            }

            // 当前界面不是桌面，且有悬浮窗显示，则移除悬浮窗。
            if (!isHome() && JieyunWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JieyunWindowManager.removeSmallWindow(getApplication());
                    }
                });
            }

            // 当前界面是桌面，且有悬浮窗显示，则更新图标。
            if (isHome() && JieyunWindowManager.isWindowShowing()) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        JieyunWindowManager.updateIconSmallWindow(getApplication());
                    }
                });
            }
        }
    }


    private boolean isHome() {
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return getHomes().contains(rti.get(0).topActivity.getPackageName());
    }

    /**
     * 获取属于桌面的应用的应用名称
     *
     * @return 返回包含所有包名的字符串列表
     */
    private List<String> getHomes() {
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = this.getPackageManager();
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo ri : resolveInfo) {
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class FloatWindowSmallView extends LinearLayout {

        private ContextUtil app;
        /**
         * 记录小悬浮窗的宽度
         */
        public int viewWidth;
        /**
         * 记录小悬浮窗的高度
         */
        public int viewHeight;

        /**
         * 记录系统状态栏的高度
         */
        private int statusBarHeight;

        /**
         * 用于更新小悬浮窗的位置
         */
        private WindowManager windowManager;
        /**
         * 小悬浮窗的参数
         */
        private WindowManager.LayoutParams mParams;
        /**
         * 记录当前手指位置在屏幕上的横坐标位置
         */
        private float xInScreen;
        /**
         * 记录当前手指位置在屏幕上的纵坐标位置
         */
        private float yInScreen;
        /**
         * 记录手指按下时在屏幕上的横坐标的位置
         */
        private float xDownInScreen;
        /**
         * 记录手指按下时在屏幕上的纵坐标的位置
         */
        private float yDownInScreen;
        /**
         * 记录手指按下时小悬浮窗在view上的横坐标位置
         */
        private float xInView;
        /**
         * 记录手指按下时小悬浮窗在view上的纵坐标位置
         */
        private float yInView;

        public FloatWindowSmallView(Context context) {
            super(context);
            windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            LayoutInflater.from(context).inflate(R.layout.float_window, this);
            View view = findViewById(R.id.float_window);
            Button button = (Button) findViewById(R.id.float_id);
            button.setOnClickListener(clickListener);
            button.setOnTouchListener(touchListener);
            button.setOnLongClickListener(longClickListener);
            Button hide = (Button) findViewById(R.id.hide);
            hide.setOnClickListener(hideClick);
            viewWidth = view.getLayoutParams().width;
            viewHeight = view.getLayoutParams().height;
        }

        OnClickListener clickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                app=ContextUtil.getInstance();
                if(!app.isFloatWdMove()){
                    Intent i = new Intent(ContextUtil.getInstance(), StartPage.class);
                    Log.d("", "单击了按钮");
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    ContextUtil.getInstance().startActivity(i);
                }
            }
        };

        private void clickBtn(final Context context) {
            Button button = (Button) findViewById(R.id.float_id);
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 单击按钮事件
                     */
                    app = ContextUtil.getInstance();
                    if (!app.isFloatWdMove()) {
                        Log.d("","没有移动");
                        Intent i = new Intent(ContextUtil.getInstance(), StartPage.class);
                        Log.d("", "单击了按钮");
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }

                }
            });
        }

        /**
         * 长按悬浮窗，显示隐藏悬浮窗按钮
         */
        OnLongClickListener longClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showHide();
                return true;
            }
        };


        /**
         * 悬浮窗的触摸事件：可自由拖动，单击时进入startPage界面
         */
        OnTouchListener touchListener = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //手指按下时记录必要数据，纵坐标都需要减去状态栏高度
                        xInView = event.getX();
                        yInView = event.getY();
                        xDownInScreen = event.getRawX();
                        yDownInScreen = event.getRawY() - getStatusBarHeight();
                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY() - getStatusBarHeight();
                        //Log.d("坐标:","yInView:" + yInView + " yDownInScreen:" + yDownInScreen + " yInScreen:" + yInScreen);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY() - getStatusBarHeight();
                        //Log.d("坐标：","xInScreen:"+xInScreen+" yInScreen:"+yInScreen);
                        //手移动的时候更新小悬浮窗的位置
                        updateViewPosition();
                        break;
                /*case MotionEvent.ACTION_UP:
                    Service floatService=new FloatActivity();
                    Intent intent=new Intent(floatService,StartPage.class);
                    floatService.startActivity(intent);
                    // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                    break;*/
                    case MotionEvent.ACTION_UP:
                        endMoveFloatWindow();
                        //clickBtn(ContextUtil.getInstance());
                        Log.d("", "离开了button");
                        break;
                    default:
                        break;
                }
                //return true;
                return false;
            }
        };


        OnClickListener hideClick = new OnClickListener() {
            @Override
            public void onClick(View v) {
                JieyunWindowManager.setWindowInvisable(ContextUtil.getInstance());
            }
        };


        /**
         * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
         *
         * @param params 小悬浮窗的参数
         */
        public void setParams(WindowManager.LayoutParams params) {
            mParams = params;
        }

        private void showHide() {
            Button btn = (Button) findViewById(R.id.hide);
            if (btn.getVisibility() == VISIBLE) {
                btn.setVisibility(INVISIBLE);
            } else {
                btn.setVisibility(VISIBLE);
            }
        }


        private int isVisible(View view) {
            if (view.getVisibility() == View.VISIBLE) {
                return View.INVISIBLE;
            } else {
                return View.VISIBLE;
            }
        }

        private void updateViewPosition() {
            mParams.x = (int) (xInScreen - xInView);
            mParams.y = (int) (yInScreen - yInView);
            windowManager.updateViewLayout(this, mParams);
        }

        /**
         * 悬浮窗只能靠边缘的方法
         */
        private void endMoveFloatWindow() {
            WindowManager windowManager = (WindowManager) ContextUtil.getInstance().getSystemService(WINDOW_SERVICE);
            float screen_width = windowManager.getDefaultDisplay().getWidth();
            //float screen_height=windowManager.getDefaultDisplay().getHeight();
            if ((xInScreen - xInView) <= screen_width / 2) {
                mParams.x = 0;
            } else {
                mParams.x = (int) screen_width;
            }
            int initY = (int)yInScreen;
            Log.d("initY:=======", String.valueOf(initY));
            //mParams.x = (int) (xInScreen - xInView);
            int endY = (int) (yInScreen - yInView);
            mParams.y = endY;
            Log.d("endY:=======", String.valueOf(endY));
            app = ContextUtil.getInstance();
            if (Math.abs(initY - endY)>30) {
                app.setIsFloatWdMove(true);
                Log.d("", "悬浮窗移动了");
            } else {
                app.setIsFloatWdMove(false);
                Log.d("", "悬浮窗没有移动");
            }
            windowManager.updateViewLayout(this, mParams);
        }

        /**
         * 用于获取状态栏高度
         *
         * @return
         */
        private float getStatusBarHeight() {
            if (statusBarHeight == 0) {
                try {
                    Class<?> c = Class.forName("com.android.internal.R$dimen");
                    Object o = c.newInstance();
                    Field field = c.getField("status_bar_height");
                    int x = (Integer) field.get(o);
                    statusBarHeight = getResources().getDimensionPixelSize(x);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return statusBarHeight;
        }
    }

}
