package com.jjlink.jieyun.njuwlan.activity;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.code.microlog4android.config.PropertyConfigurator;
import com.jjlink.jieyun.R;
import com.jjlink.jieyun.njuwlan.entity.UserInfo;
import com.jjlink.jieyun.njuwlan.log.JieyunLog;
import com.jjlink.jieyun.njuwlan.service.AutoConnection;
import com.jjlink.jieyun.njuwlan.util.AndroidUtil;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;
import com.jjlink.jieyun.njuwlan.util.NetUtil;
import com.jjlink.jieyun.njuwlan.util.UpdateUtil;
import com.jjlink.jieyun.njuwlan.util.WifiUtil;


public class LoginActivity extends JieyunActivity {
    JieyunLog logger = new JieyunLog();
    //    private static final Logger logger= LoggerFactory.getLogger(Main.class);
    //org.apache.log4j.Logger logger= AndroidLog.configLog(this.getClass());
    private String TAG = "http";
    private EditText nNameText = null;
    private EditText nPasswdText = null;
    private Button nLoginButton = null;
    private CheckBox nRembPassword = null;
    private CheckBox nAutoLogin = null;
    private SharedPreferences sp;
    private WifiUtil wifiUtil = null;
    private Long mExitTime = 0L;
    private Intent intent;
    private UserInfo userInfo;
    private String userNameValue, passwordValue;
    private static final String USER_NAME = "user_name";
    private static final String PASSWORD = "password";
    private static final String ISSAVEPASS = "savePassword";
    private static final String AUTOLOGIN = "autoLogin";
    private IntentFilter filter;
    private static final String W_SSID = "JJ_WLAN";
    private ContextUtil app;
    public static LoginActivity instance = null;
    public static final String WIFI_BROAD = "com.jjlink.wifiBord";
    public static final String AUTO_CONN_SERVICE = "com.jjlink.jieyun.autoConnection";
    private static final String appPackageName = "com.jjlink.jieyun";
    private WifiManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        logger.debug("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        PropertyConfigurator.getConfigurator(this).configure();
        instance = this;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        //createNotification("正在运行",R.drawable.portal_unconnect);
        userInfo = new UserInfo(ContextUtil.getInstance());
        nNameText = (EditText) findViewById(R.id.username);
        nPasswdText = (EditText) findViewById(R.id.password);
        nRembPassword = (CheckBox) findViewById(R.id.remb_password);
        nAutoLogin = (CheckBox) findViewById(R.id.auto_login);
        nLoginButton = (Button) findViewById(R.id.btn_login);
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);


        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        String ssidStr = "\"" + W_SSID + "\"";

       /* //检查更新
        new Thread(new Runnable() {
            @Override
            public void run() {
               // Intent service=new Intent(LoginActivity.this,AutoConnection.class);
                //service.setAction(AUTO_CONN_SERVICE);
                //startService(service);
                if (NetUtil.isConnectingToInternet()) {
                    checkUpdate();
                }
            }
        }).start();*/

        nLoginButton.setOnClickListener(mPostClickListener);


        //判断记住密码多选框的状态
        if (sp.getBoolean("ISCHECK", false)) {
            //设置默认是记录密码状态
            nRembPassword.setChecked(true);
            nNameText.setText(sp.getString("USER_NAME", ""));
            nPasswdText.setText(sp.getString("PASSWORD", ""));
            //判断自动登录多选框状态
            if (sp.getBoolean("AUTO_ISCHECK", false)) {
                //设置自动登陆多选框状态
                nAutoLogin.setChecked(true);
                //跳转界面
               /* Intent intent = new Intent(LoginActivity.this, Logining.class);
                LoginActivity.this.startActivity(intent);*/
                openActivity(Logining.class);
            }
        }

        //监听记住密码事件
        nRembPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nRembPassword.isChecked()) {
                    logger.debug("记住密码已选中");
                    sp.edit().putBoolean("ISCHECK", true).commit();
                } else {
                    logger.debug("记住密码未选中");
                    nAutoLogin.setChecked(false);
                    sp.edit().putBoolean("ISCHECK", false).commit();
                }
            }
        });

        //监听自动登录事件
        nAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (nAutoLogin.isChecked()) {
                    logger.debug("自动登录已选中");
                    nRembPassword.setChecked(true);
                    sp.edit().putBoolean("AUTO_ISCHECK", true).commit();
                } else {
                    logger.debug("自动登录未选中");
                    sp.edit().putBoolean("AUTO_ISCHECK", false).commit();
                }
            }
        });
    }

    private void checkUpdate() {
        Looper.prepare();
        UpdateUtil updateUtil = new UpdateUtil(this);
        updateUtil.checkVersion();
        Looper.loop();
    }

    private boolean checkInternet() {
        if (NetUtil.isConnectingToInternet()) {
            return true;
        }
        return false;
    }

    View.OnClickListener mPostClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startService(new Intent(LoginActivity.this, AutoConnection.class));
            app = (ContextUtil) getApplication();
            //设置自动登陆的标识
            app.setAuto(true);
            Context cx = ContextUtil.getInstance();
            wifiUtil = new WifiUtil(cx);
            String username = nNameText.getText().toString();
            String password = nPasswdText.getText().toString();
            String imsi = AndroidUtil.getIMSI(cx);
            String imei = AndroidUtil.getIMEI(cx);
            String mac = wifiUtil.getMacAddress();
            boolean flag = true;
            if (username == null || username.length() == 0) {
                flag = false;
                Toast.makeText(LoginActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (password == null || password.length() == 0) {
                flag = false;
                Toast.makeText(LoginActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
            if (flag) {
                //是否记住密码
                if (nRembPassword.isChecked()) {
                    logger.debug("USERNAME:记住密码选中");
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("USER_NAME", username);
                    editor.putString("PASSWORD", password);
                    editor.commit();

                }

                //if(NetUtil.checkNetwork()){
                Intent intent_login = new Intent(LoginActivity.this, Logining.class);
                //用Bundle携带数据
                Bundle bundle = new Bundle();


                bundle.putString("username", username);
                bundle.putString("password", password);
                bundle.putString("imsi", imsi);
                bundle.putString("imei", imei);
                bundle.putString("mac", mac);
                bundle.putLong("startTime", System.currentTimeMillis() + 1000);


                intent_login.putExtras(bundle);
                //存放自动登陆参数
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("uname", username);
                editor.putString("pwd", password);
                editor.putString("imis", imsi);
                editor.putString("imei", imei);
                editor.putString("mac", mac);
                editor.commit();
                // intent.putExtras(bundle);
                LoginActivity.this.startActivity(intent_login);
                LoginActivity.this.overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
                // startService(intent);
                LoginActivity.this.finish();
                // }else{
                //   Toast.makeText(LoginActivity.this,"未连接WIFI,开启自动登录",Toast.LENGTH_SHORT).show();

                // }

            }
        }
    };


    PackageManager pm = ContextUtil.getInstance().getPackageManager();
    ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                logger.debug(String.valueOf((System.currentTimeMillis() - mExitTime)));
                mExitTime = System.currentTimeMillis();
                //moveTaskToBack(false);
                return false;
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(66);
            if (NetUtil.isServiceWorking("AutoConnection", LoginActivity.this)) {
                stopService(new Intent(LoginActivity.this, AutoConnection.class));
            }
            this.finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //WifiBroadcast wifiBroadcast = new WifiBroadcast();

    @Override
    protected void onResume() {
        super.onResume();
        //注册通知广播
        // registerReceiver(wifiBroadcast, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregisterReceiver(wifiBroadcast);
    }

   /* @Override
    public void finish() {
       *//* ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startActivity(startIntent);*//*
        //startService(new Intent("com.jjlink.jieyun.autoConnection"));
        moveTaskToBack(true);
    }*/


}
