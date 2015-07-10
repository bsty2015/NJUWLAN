package com.jjlink.jieyun.njuwlan.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.jjlink.jieyun.R;
import com.jjlink.jieyun.njuwlan.log.JieyunLog;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;
import com.jjlink.jieyun.njuwlan.util.NetUtil;
import com.jjlink.jieyun.njuwlan.util.ToolUtil;
import com.jjlink.jieyun.selfservice.activity.Welcome;

import org.json.JSONObject;

import java.util.Map;


public class Logining extends JieyunActivity {
    private Button nCancelBtn = null;
    private ContextUtil app;
    private String TAG = "http";
    // private Thread mThread;

    private Thread mThread = null;
    //private static final String action = "http://192.168.150.1:8080/portalSpring/applogin";
    //private static final String action = "http://219.219.114.244:4444/portalSpring/applogin";
    private static final String action = "http://p.nju.edu.cn:8080/portal_io/app/login";
    //private static final String action = "http://192.9.2.186:8080/portalSpring/applogin";
    //private static final String action = "http://192.168.1.118:8080/portalSpring/applogin";
    private static final String appPackageName="com.jjlink.jieyun";
    private HandlerThread handlerThread;
    private Handler handler;
    JieyunLog logger=new JieyunLog();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.logining);
        nCancelBtn = (Button) findViewById(R.id.btn_back);

        handlerThread =new HandlerThread("loginThread");
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
        handler.post(
                new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(100);
                            // NetUtil.createNotification(ContextUtil.getInstance(), (NotificationManager) getSystemService(NOTIFICATION_SERVICE),"正在登录", R.drawable.portal_unconnect, Logining.class);
                            doPost();
                        } catch (Exception e) {
                            //e.printStackTrace();
                            logger.debug("登录时发生异常:" + e.getMessage());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(Logining.this, "服务器连接失败", Toast.LENGTH_SHORT).show();
                                    Logining.this.finish();
                                }
                            });

                        }

                    }
                }
        );
       /* mThread = new Thread();
        mThread.start();*/
        nCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logining.this.finish();
            }
        });


    }

    public void doPost() {
        Bundle bundle = Logining.this.getIntent().getExtras();
        Map<String,Object> map= null;
        try {
            map = NetUtil.LoginOfPost(bundle, action);
            int code= (int) map.get("responseCode");

            String result= (String) map.get("result");

            if (code == 200) {

                JSONObject jsonObject=new JSONObject(result);
                //只有当portal返回码是1或者6的时候,portal验证成功跳转成功
                int repCode=jsonObject.getInt("reply_code");
                final String replyMsg= (String) jsonObject.get("reply_msg");
                if(repCode==1||repCode==6){
                    Log.i("登陆结果：", "登陆成功");
                    //NetUtil.createNotification(ContextUtil.getInstance(), (NotificationManager) getSystemService(NOTIFICATION_SERVICE), "登录成功，可以上网", R.drawable.logo, Welcome.class);
                    Intent intent = new Intent(Logining.this, Welcome.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    app=ContextUtil.getInstance();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("result",result);
                    intent.putExtras(bundle1);
                    //将得到的bundle数据存放在全局变量中,当下次点击通知取不出数据时候就从全局变量里取用,避免发生空指针异常
                    app.setBundle(bundle1);
                    Logining.this.startActivity(intent);
                    Logining.this.overridePendingTransition(R.anim.enteralpha,R.anim.exitalpha);
                    Logining.this.finish();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Logining.this,replyMsg,Toast.LENGTH_SHORT).show();
                    }
                });
                Logining.this.finish();
            } else {
                Log.i("登陆结果:", "登陆失败");
                //NetUtil.createNotification(ContextUtil.getInstance(), (NotificationManager) getSystemService(NOTIFICATION_SERVICE), "登录失败", R.drawable.portal_unconnect, LoginActivity.class);
                Bundle bundle2 = new Bundle();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(Logining.this,"登陆超时,稍后重试",Toast.LENGTH_SHORT).show();
                    }
                });
                //b2.putString("encryptStr",encryptStr);
                mThread.interrupt();
                startActivity(new Intent(Logining.this,LoginActivity.class));
                finish();
            }
        } catch (Exception e) {
            logger.debug("发送登录请求异常:" + e.getMessage());
            mThread.interrupt();
            startActivity(new Intent(Logining.this, LoginActivity.class));
            finish();
            //NetUtil.createNotification(ContextUtil.getInstance(), (NotificationManager) getSystemService(NOTIFICATION_SERVICE), "连接不上服务器", R.drawable.portal_unconnect, LoginActivity.class);
           // e.printStackTrace();
        }



        }




  /*  private void createWelcomeNotification(String contextText,int icon,Class<?> clas{
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(ContextUtil.getInstance());
        builder.setContentTitle("捷运上网助手");
        builder.setContentText(contextText);
        builder.setSmallIcon(icon);
        Intent i=new Intent(this, clas);
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent= PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        Notification notification=builder.build();
        notification.flags=Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(66,notification);
    }*/

    @Override
    protected void onStop() {
        if (mThread != null) {
            mThread.interrupt();
            Thread dummy = mThread;
            mThread = null;
            dummy.interrupt();
        }
        super.onStop();
    }

    private byte[] toTlvBytes(char tag, char length, String value) throws Exception {
        if(value!=null&&value.length()>0){
            byte[] b1=String.valueOf(tag).getBytes();
            byte[] b2=String.valueOf(length).getBytes();
            byte[] b3=value.getBytes();
            byte[] b4= ToolUtil.uniteArry(b1, b2);
            System.out.println(ToolUtil.bytesToHexString(ToolUtil.uniteArry(b3,b4)));
            return ToolUtil.uniteArry(b4,b3);
        }
        return null;
    }
}
