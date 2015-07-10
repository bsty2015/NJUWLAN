package com.jjlink.jieyun.njuwlan.activity;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.jjlink.jieyun.R;
import com.jjlink.jieyun.njuwlan.log.JieyunLog;
import com.jjlink.jieyun.njuwlan.service.AutoConnection;
import com.jjlink.jieyun.njuwlan.service.CheckNetService;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;
import com.jjlink.jieyun.njuwlan.util.NetUtil;
import com.jjlink.jieyun.selfservice.activity.Welcome;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class StartPage extends JieyunActivity {
    public static final String CHECK_NET_SERVICE = "com.jjlink.service.checkNetService";
    public static final String AUTO_CONN_SERVICE = "com.jjlink.jieyun.autoConnection";
    public static final String FLOAT_WINDOW_SERVICE = "com.jjlink.jieyun.floatWindowService";
    private static final String action = "http://p.nju.edu.cn:8080/portalSpring/applogin";
    private JieyunLog logger = new JieyunLog();
    private static final String W_SSID = "NJU-WLAN";
    private ContextUtil app;
    WifiManager wifiManager;
    String ssidStr = "\"" + W_SSID + "\"";
    String username;
    String password;
    SharedPreferences sp;
    private HandlerThread handlerThread;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        sp = this.getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        handlerThread = new HandlerThread("startApp");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.post(
                new Runnable() {
                    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
                    @Override
                    public void run() {
                        Intent checkNetwork = new Intent(StartPage.this, CheckNetService.class);
                        checkNetwork.setAction(CHECK_NET_SERVICE);
                        if (!NetUtil.isServiceWorking("CheckNetService", StartPage.this)) {
                            startService(checkNetwork);
                        }
                        Intent floatWindowService = new Intent(StartPage.this, FloatWindowService.class);
                        floatWindowService.setAction(FLOAT_WINDOW_SERVICE);
                        if (!NetUtil.isServiceWorking("FloatWindowService", StartPage.this)) {
                            startService(floatWindowService);
                        }
                        Intent service = new Intent(StartPage.this, AutoConnection.class);
                        service.setAction(AUTO_CONN_SERVICE);
                        if (NetUtil.isServiceWorking("AutoConnection", StartPage.this)) {
                            stopService(service);
                        }
                        startService(service);
                        if (sp == null) {

                        } else if (sp.getString("USER_NAME", "") == null || sp.getString("USER_NAME", "").isEmpty()
                                || sp.getString("PASSWORD", "") == null || sp.getString("PASSWORD", "").isEmpty()) {
                   /* startActivity(new Intent(StartPage.this, LoginActivity.class));*/
                            openActivity(LoginActivity.class);
                        } else {
                            /**
                             * 当wifi连接正确
                             */
                            int i = 0;
                            for (i = 0; i < 3; i++) {
                                if (!isSSID_connected()) {
                                    try {
                                        Thread.sleep(5 * 1000);
                                    } catch (Exception e) {
                                        logger.debug("线程休眠异常");
                                    }
                                } else {
                                    try {
                                        Bundle bundle = getLoginInfo();
                                        Map<String, Object> map = null;
                                        map = NetUtil.LoginOfPost(bundle, action);
                                        if (map != null) {

                                            int code = (int) map.get("responseCode");
                                            String result = (String) map.get("result");
                                            if (code == 200) {
                                                JSONObject jsonObject = null;
                                                jsonObject = new JSONObject(result);
                                                //只有当portal返回码是1或者6的时候,portal验证成功跳转成功
                                                int repCode = jsonObject.getInt("reply_code");
                                                final String replyMsg = (String) jsonObject.get("reply_msg");
                                                if (repCode == 1 || repCode == 6) {
                                                    //NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                    //NetUtil.createNotification(StartPage.this, notificationManager, "可以上网", R.drawable.logo, Welcome.class);
                                                    Intent intent = new Intent(StartPage.this, Welcome.class);
                                                    //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                                    app = ContextUtil.getInstance();
                                                    Bundle bundle1 = new Bundle();
                                                    bundle1.putString("result", result);
                                                    SharedPreferences.Editor editor = sp.edit();
                                                    editor.putString("result", result);
                                                    intent.putExtras(bundle1);
                                                    //将得到的bundle数据存放在全局变量中,当下次点击通知取不出数据时候就从全局变量里取用,避免发生空指针异常
                                                    app.setBundle(bundle1);
                                                    StartPage.this.startActivity(intent);
                                                    StartPage.this.overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
                                                    StartPage.this.finish();
                                                } else {
                                                    Intent intnt = new Intent(StartPage.this, LoginActivity.class);
                                                    StartPage.this.startActivity(intnt);
                                                    StartPage.this.overridePendingTransition(R.anim.enteralpha, R.anim.exitalpha);
                                                    StartPage.this.finish();
                                                }
                                            }
                                        }

                                    } catch (Exception e) {
                                        logger.debug("json解析异常:" + e.getMessage());
                                        Log.d("Welcome:", "登录失败");
                                        Intent intent = new Intent(StartPage.this, LoginActivity.class);
                                        startActivity(intent);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Toast.makeText(StartPage.this, "连接服务器失败", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        StartPage.this.finish();
                                    }
                                    break;
                                }
                            }
                            if (i >= 3) {
                                //startActivity(new Intent(StartPage.this, LoginActivity.class));
                                openActivity(LoginActivity.class);
                                StartPage.this.finish();
                            }
                        }

                    }
                }
        );
       /* Thread loadLogin = new Thread();
        loadLogin.start();*/
    }

    /**
     * 获取登录需要的用户数据
     *
     * @return
     */
    private Bundle getLoginInfo() {
        Bundle bundle = new Bundle();
        String uname = sp.getString("uname", "");
        String pwd = sp.getString("pwd", "");
        String imsi = sp.getString("imis", "");
        String imei = sp.getString("imei", "");
        String mac = sp.getString("mac", "");

        bundle.putString("username", uname);
        bundle.putString("password", pwd);
        bundle.putString("imsi", imsi);
        bundle.putString("imei", imei);
        bundle.putString("mac", mac);
        return bundle;
    }

    private boolean isSSID_connected() {
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
        logger.debug("没有连接上NJU-WLAN");
        return false;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static boolean isServiceWorked(Context context,String serviceName){
        ActivityManager am= (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServices= (ArrayList<ActivityManager.RunningServiceInfo>) am.getRunningServices(Integer.MAX_VALUE);
        for(int i=0; i<runningServices.size();i++){
            //System.out.println("service-"+i+":"+runningServices.get(i).service.getClassName().toString());
            if(runningServices.get(i).service.getClassName().toString().equals(serviceName)){
                //System.out.println("service-"+i+":"+runningServices.get(i).service.getClassName().toString());
                return true;
            }
        }
        return false;
    }
}
