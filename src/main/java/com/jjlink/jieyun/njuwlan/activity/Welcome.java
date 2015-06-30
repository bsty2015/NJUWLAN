package com.jjlink.jieyun.njuwlan.activity;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jjlink.jieyun.R;
import com.jjlink.jieyun.njuwlan.log.JieyunLog;
import com.jjlink.jieyun.njuwlan.service.AutoConnection;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;
import com.jjlink.jieyun.njuwlan.util.GestureListener;
import com.jjlink.jieyun.njuwlan.util.NetUtil;
import com.jjlink.jieyun.njuwlan.util.UpdateUtil;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class Welcome extends JieyunActivity {

    private TextView infoText;
    private TextView timeInfo;
    private Button cancelBtn;
    private ContextUtil app;
    private boolean isScorllStart = false;
    private boolean isUpAndDown = false;
    private static final String LOGOUT_URL = "http://p.nju.edu.cn:8080/portalSpring/logout";
    private static final String action = "http://p.nju.edu.cn:8080/portalSpring/applogin";
    private static final String appPackageName = "com.jjlink.jieyun";
    public static final String AUTO_CONN_SERVICE = "com.jjlink.jieyun.autoConnection";
    private static final String SERVICE_NAME = "com.jjlink.jieyun.AutoConnection";
    private static final String W_SSID = "NJU-WLAN";
    WifiManager wifiManager;
    JieyunLog logger = new JieyunLog();
    private Handler handler;
    //Date DATE = new Date();
    SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    private long initDate;
    boolean flag = true;
    SharedPreferences sp;
  /*  Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Date dt = new Date();
            try {
                initDate = getInitDate();
                long seconds = (dt.getTime() - initDate) / 1000;
                long days = (long) Math.floor(seconds / 86400);
                long hours = (long) Math.floor((seconds - days * 86400) / 3600);
                long minutes = (long) Math.floor((seconds - days * 86400 - hours * 3600) / 60);
                long secs = (long) Math.floor(seconds - days * 86400 - hours * 3600 - minutes * 60);
                String s = (secs < 10) ? "0" + secs : secs + "";
                String m = (minutes < 10) ? "0" + minutes : minutes + "";
                //Thread.sleep(1000);
                timeInfo.setText("已在线:" + days + "天" + hours + "小时" + m + "分钟" + s + "秒");
                handler.postDelayed(this, 0);
            } catch (JSONException e) {
                Log.e("获取服务器数据：", "失败!");
            }

        }
    };*/


    public synchronized void doPost() {
        try {
            Map<String, Object> map = NetUtil.logout(LOGOUT_URL);
            //JSONObject jsonObject=
            int code = (int) map.get("code");
            int replyCode = (int) map.get("reply_code");
            final String replyMsg = (String) map.get("reply_msg");
            logger.debug("注销返回code:" + code);
            if (code == 200) {
                if (replyCode == 101) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Welcome.this, "注销成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    //NetUtil.createNotification(ContextUtil.getInstance(), manager, "已注销登录,请手动登录", R.drawable.portal_unconnect, LoginActivity.class);
                    app = (ContextUtil) getApplication();
                    logger.debug("自动登陆状态:" + app.isAuto());
                    app.setAuto(false);
                    logger.debug("改变后自动登陆状态:" + app.isAuto());
                    Intent service = new Intent(Welcome.this, AutoConnection.class);
                    service.setAction(AUTO_CONN_SERVICE);
                    stopService(service);
                    logger.debug("结束了自动连接服务");
                    Welcome.this.startActivity(new Intent(Welcome.this, LoginActivity.class));
                    Welcome.this.overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
                    Welcome.this.finish();
                }


            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Welcome.this, "注销失败", Toast.LENGTH_SHORT).show();
                        logger.debug("注销失败:" + replyMsg);
                    }
                });
                logoutThread.interrupt();
                return;
            }
        } catch (Exception e) {
            logger.debug("发送注销请求时发生异常:" + e.getMessage());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(Welcome.this, "注销失败", Toast.LENGTH_SHORT).show();
                }
            });
            logoutThread.interrupt();
            return;
            //Log.d("注销发生异常",e.getMessage());
        }
    }


    /**
     * 注销登陆的portal账号
     *
     * @param url
     * @return
     */
    public Map<String, Object> logout(String url) {
        try {
            HttpPost httpReqeust = new HttpPost(url);
            Map<String, Object> results = new HashMap<String, Object>();
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            HttpResponse httpResponse = null;
            httpResponse = new DefaultHttpClient().execute(httpReqeust);
            String result = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            logger.debug(result);
            results.put("result", result);
            return results;
        } catch (IOException e) {
            e.printStackTrace();
            logger.debug("发送注销请求异常:" + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        //startService(new Intent(Welcome.this,AutoConnection.class));
        LinearLayout view = (LinearLayout) this.findViewById(R.id.welcome);
        view.setLongClickable(true);
        view.setOnTouchListener(new JieyunGestureListener(this));
        infoText = (TextView) findViewById(R.id.textInfo);
        timeInfo = (TextView) findViewById(R.id.timeInfo);
        //cancelBtn = (Button) findViewById(R.id.btn_logout);
        //infoText.setMovementMethod(ScrollingMovementMethod.getInstance());
        handler = new Handler();

        /**
         * 检查更新线程
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (NetUtil.isConnectingToInternet()) {
                    checkUpdate();
                }
            }
        }).start();
        /**
         * 获取登录用户信息数据的线程
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    app = ContextUtil.getInstance();
                    Bundle bundle = Welcome.this.getIntent().getExtras();
                    if (bundle == null || bundle.isEmpty()) {
                        bundle = getLoginInfo();
                        Map<String, Object> map = NetUtil.LoginOfPost(bundle, action);
                        //int code = (int) map.get("responseCode");
                        String result = (String) map.get("result");
                        bundle.putString("result", result);
                    }
                    createWelcomeUI(bundle);
                } catch (Exception e) {
                    Log.d("Welcome:", "登录失败");
                    Intent intent=new Intent(Welcome.this,LoginActivity.class);
                    startActivity(intent);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Welcome.this,"获取服务器信息失败",Toast.LENGTH_SHORT).show();
                        }
                    });
                    Welcome.this.finish();
                }

            }
        }).start();

//        String unEncryptStr = bundle.getString("unEncryptStr");
//        String encryptStr = bundle.getString("encryptStr");
//        String unBase64Params = bundle.getString("unBase64Params");

        // handler.postDelayed(runnable, 0);

        // mText.setText(unEncryptStr+"\n"+unBase64Params+"\n"+encryptStr+"\nresult:"+result);
    /*    cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app = (ContextUtil) getApplication();
                logger.debug("自动登陆状态:" + app.isAuto());
                app.setAuto(false);
                logger.debug("改变后自动登陆状态:" + app.isAuto());
                logoutThread.start();
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                //createNotification("已注销登录,请手动登录", R.drawable.portal_unconnect);
                stopService(new Intent("com.jjlink.jieyun.autoConnection"));
                logger.debug("主动注销,自动登录服务停止了");
                Welcome.this.finish();
                nm.cancel(66);
            }
        });*/
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private boolean isSSID_connected() {
        String ssidStr = "\"" + W_SSID + "\"";
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Context context = ContextUtil.getInstance();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
/*        logger.debug("wifiManager.isWifiEnabled():"+wifiManager.isWifiEnabled()+"networkInfo.isAvailable():"+networkInfo.isAvailable()
                    +"networkInfo.isConnected():"+networkInfo.isConnected()+"!wifiInfo.getSSID().isEmpty()"+!wifiInfo.getSSID().isEmpty()+"wifiInfo.getSSID().equalsIgnoreCase(ssidStr):"+
                wifiInfo.getSSID().equalsIgnoreCase(ssidStr)+"wifiInfo.getSSID().equalsIgnoreCase(ssidStr):"+wifiInfo.getSSID().equalsIgnoreCase(W_SSID))*/

        if (networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiInfo != null && !wifiInfo.getSSID().isEmpty()
                && (wifiInfo.getSSID().equalsIgnoreCase(W_SSID) || wifiInfo.getSSID().equalsIgnoreCase(ssidStr))) {
            return true;
        }
        //createWifiSettingNotification(W_SSID + "未连接", R.drawable.portal_unconnect);
        logger.debug("没有连接上NJU-WLAN");
        return false;

    }

    private void createWelcomeUI(Bundle bundle) {
        String result = bundle.getString("result");
        logger.debug(result);
        if (result != null && !result.isEmpty())
            try {
                JSONObject jsonObject = new JSONObject(result);
                result = (String) jsonObject.get("reply_msg");
                JSONObject userinfo = (JSONObject) jsonObject.get("userinfo");
                String username = (String) userinfo.get("username");
                String fullname = (String) userinfo.get("fullname");
                String service = (String) userinfo.get("service_name");
                String area_name = (String) userinfo.get("area_name");
                int loginTime = (int) userinfo.get("acctstarttime");
                String loginDate = parseTime(loginTime);

                int payamount = (int) userinfo.get("payamount");
                final String info = result + "\n账号: " + username + "\n姓名: " + fullname + "\n服务类型: " + service + "\n登陆区域: " + area_name + "\n账户余额: " + (double) payamount / 100 + "\n登陆时间: " + loginDate;
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        infoText.setText(info);
                    }
                };
                handler.post(runnable);
            } catch (JSONException e) {
                logger.debug("解析登录返回的json数据异常:" + e.getMessage());
            }
    }

    /**
     * 检查更新
     */
    private void checkUpdate() {
        Looper.prepare();
        UpdateUtil updateUtil = new UpdateUtil(this);
        updateUtil.checkVersion();
        Looper.loop();
    }

    /**
     * 获取登录需要的用户数据
     *
     * @return
     */
    private Bundle getLoginInfo() {
        sp = getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        Bundle bundle = new Bundle();
        String uname = sp.getString("uname", "");
        System.out.println("通过获取记录的bundle\n");
        System.out.println("uname:" + uname);
        String pwd = sp.getString("pwd", "");
        System.out.println("pwd:" + pwd);
        String imsi = sp.getString("imsi", "");
        System.out.println("imsi:" + imsi);
        String imei = sp.getString("imei", "");
        System.out.println("imei:" + imei);
        String mac = sp.getString("mac", "");
        System.out.println("mac:" + mac);
        bundle.putString("username", uname);
        bundle.putString("password", pwd);
        bundle.putString("imsi", imsi);
        bundle.putString("imei", imei);
        bundle.putString("mac", mac);
        return bundle;
    }

    private void logout() {
        logoutThread.start();
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //createNotification("已注销登录,请手动登录", R.drawable.portal_unconnect);
        //logger.debug("主动注销,自动登录服务停止了");
        Welcome.this.finish();
        SharedPreferences sp =app.getSharedPreferences("NETSTAT",Context.MODE_WORLD_READABLE);
        app.setIsNetwork(false);
        app.setIsWifiToInter(false);
        sp.edit().putBoolean("NETWORKENABLE",false).commit();
        sp.edit().putBoolean("WIFITOINTER",false).commit();
        nm.cancel(66);

    }

    /**
     * 获取登录时间用来计算已经登录的时长
     *
     * @return
     */
    private long getInitDate() throws JSONException {
        Bundle bundle = Welcome.this.getIntent().getExtras();
        if (bundle == null || bundle.isEmpty()) {
            bundle = app.getBundle();
            // Log.i("bundle是否为空:", String.valueOf(""+bundle==null));
        }
        String result = bundle.getString("result");
        if (result != null && !result.isEmpty()) {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject userinfo = (JSONObject) jsonObject.get("userinfo");
            int LongTime = userinfo.getInt("acctstarttime");
            return LongTime * 1000l;
        }
        return 0;
    }

    Thread logoutThread = new Thread(new Runnable() {
        @Override
        public void run() {
            //NetUtil.logout(LOGOUT_URL);
            doPost();
        }
    });

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
//            LoginActivity.instance.finish();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private String parseTime(int time) {
        System.setProperty("user.timezone", "Asia/Shanghai");
        TimeZone tz = TimeZone.getTimeZone("Asia/Shanghai");
        TimeZone.setDefault(tz);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //format.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date date = new Date(time * 1000l);
        logger.debug(format.format(date));
        return format.format(date);
    }

    PackageManager pm = ContextUtil.getInstance().getPackageManager();
    ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);

    @Override
    public void finish() {
        logoutThread.interrupt();
        super.finish();
    }

    public void backToDesk() {
        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName(ai.packageName, ai.name));
        startActivity(startIntent);
        moveTaskToBack(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.welcom_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.exit) {
            if (NetUtil.isServiceWorking(SERVICE_NAME, Welcome.this)) {
                JieyunWindowManager.removeSmallWindow(getApplication());
                stopService(new Intent(Welcome.this, AutoConnection.class));
            }
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(66);
            System.exit(0);
            return true;
        }
        /*if (id == R.id.update) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        checkUpdate();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }*/
        return super.onOptionsItemSelected(item);
    }

    private class JieyunGestureListener extends GestureListener {

        public JieyunGestureListener(Context context) {
            super(context);
        }

        @Override
        public boolean up() {
            if (isSSID_connected()) {
                logout();
            } else {
                Toast.makeText(Welcome.this, "注销失败:" + W_SSID + "未连接", Toast.LENGTH_SHORT).show();
            }
            return super.up();
        }

        @Override
        public boolean left() {
            closeSelf();
            return super.left();
        }
    }

    private void closeSelf() {
        moveTaskToBack(true);
//        LoginActivity.instance.finish();
    }
}

