package com.jjlink.jieyun.njuwlan.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;

import com.jjlink.jieyun.njuwlan.log.JieyunLog;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by zlu on 15-3-5.
 */
public class NetUtil {
    private static String URL_ADDR="http://www.baidu.com/";
    private static String SERVER_NAME="[redirect.nju.edu.cn]";
    private static boolean IS_AUTO = false;
    private static JieyunLog logger=new JieyunLog();
//    /**
//     * 检测是否能ping通url地址
//     * @param ip
//     * @return
//     */
//    public static boolean isReachable(String ip){
//        try {
//            Process p =Runtime.getRuntime().exec("/system/bin/ping -c 3 -w 100"+ip);
//            InputStream input=p.getInputStream();
//            BufferedReader in=new BufferedReader(new InputStreamReader(input));
//            StringBuffer sb=new StringBuffer();
//            String content=null;
//            while((content=in.readLine())!=null){
//                sb.append(content);
//            }
//            logger.debug("TTT","result content:"+sb.toString());
//            int status=p.waitFor();
//            if(status==0){
//                return true;
//            }else{
//                logger.debug("FAIL:","IP address not reachable status="+status);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        /*try {
//            InetAddress addr=InetAddress.getByName(ip);
//            boolean status=addr.isReachable(3000);
//            logger.debug("UDP","status="+status);
//            System.out.println("addr="+addr);
//            if(addr.isReachable(3000)){
//                System.out.println("ping成功...");
//                return true;
//            }
//        } catch (Exception e) {
//        }*/
//        return false;
//    }

//    /**
//     * 检查Internet是否连接
//     * @param mContext
//     * @return
//     */
//    public static boolean checkNetWork(Context mContext){
//        ConnectivityManager connectivityManager=(ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info=connectivityManager.getActiveNetworkInfo();
//        boolean isNetAvab=false;
////        System.out.println("info="+info+"-------info.isNetAvab="+info.isAvailable());
//        if(info!=null&&info.isAvailable()){
//            isNetAvab=isReachable(urlAddr);
//            System.out.println("isNetAvab----------------"+isNetAvab);
//        }
//        return isNetAvab;
//    }


    /**
     * 判断能否连上www.baidu.com
     * @return
     *//*
    public static boolean isWifiToInternet(){
        HttpURLConnection conn=null;
        try {
            URL url=new URL(URL_ADDR);
            conn=(HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");

            conn.connect();
            int code=conn.getResponseCode();
            //conn.getHeaderField()
            //System.out.println(conn.getURL().toString());
            Map<String,List<String>> maps=conn.getHeaderFields();
            Iterator it=maps.entrySet().iterator();
            StringBuffer sb=new StringBuffer();
            String server=Arrays.toString(maps.get("Server").toArray());
            //System.out.println("server:"+server);
            conn.setConnectTimeout(20);
            conn.setReadTimeout(20);
            if(code==200&&!server.trim().equals(SERVER_NAME)){
                logger.debug("判断能否连上www.baidu.com:internet可用");
                return true;
            }
        } catch (Exception e) {
            //e.printStackTrace();
            logger.debug("检查是否能上网:不能上网");

            return false;
        }finally {
            if(conn!=null){
                conn.disconnect();
            }
        }
        logger.debug("判断能否连上www.baidu.com:internet不可用");
        return false;
    }
*/

    /**
     * 查看网络是否可用
     *
     * @return
     */
    public static boolean isWifiToInternet() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(URL_ADDR);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.connect();
            int code = conn.getResponseCode();
            //conn.getHeaderField()
            //System.out.println(conn.getURL().toString());
            Map<String, List<String>> maps = conn.getHeaderFields();
            Iterator it = maps.entrySet().iterator();
            StringBuffer sb = new StringBuffer();
            String server = Arrays.toString(maps.get("Server").toArray());
            //System.out.println("server:" + server);

            if (code == 200 && !server.trim().equals(SERVER_NAME)) {
                //logger.debug("判断能否连上www.baidu.com:internet可用");
                //createNotification("可以上网",R.drawable.logo);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("检查是否能上网:不能上网" + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        logger.debug("判断能否连上www.baidu.com:internet不可用");
        return false;
    }
    /**
     * 是否能上网,有一种情况除外,当连上需要登陆的wifi的时候,需要做另外的逻辑判断
     * @return
     */
    public static boolean isConnectingToInternet(){
        ConnectivityManager connectivity=(ConnectivityManager)ContextUtil.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity!=null){
            NetworkInfo[] info=connectivity.getAllNetworkInfo();
            if(info!=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()== NetworkInfo.State.CONNECTED){
                        /**
                         * 当连上需要登陆的wifi的时候,需要做另外的逻辑判断
                         */
                        if(isWifiToInternet()){
                            return true;
                        }
                        return  false;
                    }
                }
            }
        }
        return false;
    }

    public static boolean checkNetwork(){
        ConnectivityManager connectivity=(ConnectivityManager)ContextUtil.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity!=null){
            NetworkInfo[] info=connectivity.getAllNetworkInfo();
            if(info!=null){
                for(int i=0;i<info.length;i++){
                    if(info[i].getState()== NetworkInfo.State.CONNECTED){
                            return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断服务是否在运行
     * @param className
     * @return
     */
    public static boolean isServiceWorking(String className,Activity activity) {
        ActivityManager manager = (ActivityManager) activity.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServices = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(100);
        if (runningServices.size() > 0) {

            for (int i = 0; i < runningServices.size(); i++) {
                if (runningServices.get(i).service.getClassName().toString()
                        .equals(className)) {
                    logger.debug(className+"服务正在运行......");
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isServiceWorking(String className,Context context) {
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningServices = (ArrayList<ActivityManager.RunningServiceInfo>) manager.getRunningServices(100);
        if (runningServices.size() > 0) {
            for (int i = 0; i < runningServices.size(); i++) {
                if (runningServices.get(i).service.getClassName().toString()
                        .equals(className)) {
                    logger.debug(className+"服务正在运行......");
                    return true;
                }
            }
        }
        return false;
    }
/*

    */
/**
     * 创建通知方法
     * @param context
     * @param notificationManager
     * @param contextText
     * @param icon
     * @param clas
     *//*

    public static void createNotification(Context context,NotificationManager notificationManager,String contextText,int icon,Class<?> clas){
        NotificationCompat.Builder builder=new NotificationCompat.Builder(ContextUtil.getInstance());
        builder.setContentTitle("捷运上网助手");
        builder.setContentText(contextText);
        builder.setSmallIcon(icon);
       */
/* Intent intent=this.getPackageManager().getLaunchIntentForPackage(appPackageName);
        PendingIntent pendingIntent=PendingIntent.getActivity(ContextUtil.getInstance(),0,intent,0);
        builder.setContentIntent(pendingIntent);*//*

        //builder.setOngoing(true);

//        Intent intent=new Intent(BringToFrontReceiver.ACTION_BRING_TO_FRONT);
//        builder.setContentIntent(PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        Intent i=new Intent(context,clas);
        PendingIntent intent=PendingIntent.getActivity(context,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        Notification notification=builder.build();
        notification.flags=Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(66,notification);
    }
*/

    /**
     * 通过post请求登陆
     * @param bundle
     * @param urlPath
     * @return
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static Map<String,Object> LoginOfPost(Bundle bundle,String urlPath) throws Exception {
        logger.debug("登陆portal:开始>>>>>>>>>>>>>>>>>>>");


            Map<String,Object> results=new HashMap<String,Object>();
            String randStr = ThreeDes.getRandInt();
            String requestID = MD5.GetMD5Code(randStr);
            //logger.debug("requestID**************"+requestID);
            String requestParams;
        /*Tlv数据t的值定义*/
            char t1=(char)1;
            char t2=(char)2;
            char t3=(char)3;
            char t4=(char)4;
            char t5=(char)5;
        /*取得传过来的参数*/
            // Bundle bundle = Logining.this.getIntent().getExtras();
            String userName = bundle.getString("username");
            String password = bundle.getString("password");
            String imsi = (bundle.getString("imsi")==null||bundle.getString("imsi").isEmpty())?"123456789012345":bundle.getString("imsi");
            String imei = (bundle.getString("imei")==null||bundle.getString("imei").isEmpty())?"123456789012345":bundle.getString("imei");
            String mac = bundle.getString("mac");
            long startTime=bundle.getLong("startTime");
            //logger.debug("开始时间:"+startTime);
            //logger.debug("LOGININFO:username:" + userName + "\npassword: " + password + "\nimsi:" + imsi + "\nimei:" + imei + "\nmac:" + mac);
            String unEncryptStr = "加密前的参数:\n产生的随机数:" + randStr + "\nrequestID:" + requestID + "\nusername:" + userName + "\npassword: " + password + "\nimsi:" + imsi + "\nimei:" + imei + "\nmac:" + mac;

        /*拼接参数字节流*/
            byte[] b1=toTlvBytes(t1,(char)(userName.length()+2),userName);
            byte[] b2=toTlvBytes(t2,(char)(password.length()+2),password);
            byte[] b3=toTlvBytes(t3,(char)(imsi.length()+2),imsi)==null?null:toTlvBytes(t3,(char)(imsi.length()+2),imsi);
            byte[] b4= new byte[0];
            b4 = toTlvBytes(t4,(char)(imei.length()+2),imei)==null?null:toTlvBytes(t4,(char)(imei.length()+2),imei);
            byte[] b5=toTlvBytes(t5,(char)(mac.length()+2),mac)==null?null:toTlvBytes(t5,(char)(mac.length()+2),mac);

            byte[] bs=ToolUtil.uniteArry(b1,b2);
            bs=ToolUtil.uniteArry(bs,b3);
            bs=ToolUtil.uniteArry(bs,b4);
            bs=ToolUtil.uniteArry(bs,b5);

            // System.out.println("加密前的字节流:"+Arrays.toString(bs));
            String key=ThreeDes.getKey(randStr);
            //System.out.println("keybytes===================="+Arrays.toString(key.getBytes()));
            requestParams=ThreeDes.toBase64Des(key.getBytes(),bs);
            byte[] deParams= Base64.decode(requestParams, Base64.NO_WRAP);
            byte[] deParamStr=ThreeDes.decryptMode(key.getBytes(),deParams);
            //System.out.println("解密后的字节流:"+Arrays.toString(deParamStr));


            userName=ToolUtil.bytesToHexString(TlvUtils.toTlvBytes(t1,(char)(userName.length()+2),userName));
            password=ToolUtil.bytesToHexString(TlvUtils.toTlvBytes(t2,(char)(password.length()+2),password));
            imsi=ToolUtil.bytesToHexString(TlvUtils.toTlvBytes(t3,(char)(imsi.length()+2),imsi));
            imei=ToolUtil.bytesToHexString(TlvUtils.toTlvBytes(t4,(char)(imei.length()+2),imei));
            mac=ToolUtil.bytesToHexString(TlvUtils.toTlvBytes(t5,(char)(mac.length()+2),mac));
            String unBase64Params= "\nusername:" + userName + "\npassword: " + password + "\nimsi:" + imsi + "\nimei:" + imei + "\nmac:" + mac;


            HttpPost httpRequest = new HttpPost(urlPath);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // params.add(new BasicNameValuePair("requestId",requestID));
            requestParams=requestID+requestParams;
            params.add(new BasicNameValuePair("params", requestParams));
            String encryptStr = "加密后的请求参数: " + requestParams;
            //logger.debug("加密后的请求参数**************"+ encryptStr);
            //logger.debug("unEncryptStr:"+unEncryptStr);
            //logger.debug("密钥:"+key);

            httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
            HttpClient client=new DefaultHttpClient();
            //请求超时
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
            //读取超时
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,5000);

            HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
            String result= EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
            results.put("unEncryptStr", unEncryptStr);
            results.put("encryptStr", encryptStr);
            results.put("unBase64Params", unBase64Params);
            results.put("responseCode",httpResponse.getStatusLine().getStatusCode());
            results.put("result",result);
            logger.debug("登陆portal:结束<<<<<<<<<<<<<<<<<<<");
            return results;

    }

    /**
     * 注销登陆的portal账号
     * @param url
     * @return
     */
    public static Map<String,Object> logout(String url) {
        try {
            HttpPost httpReqeust = new HttpPost(url);
            Map<String, Object> results = new HashMap<String, Object>();
            HttpClient client = new DefaultHttpClient();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
            HttpResponse httpResponse = null;
            httpResponse = new DefaultHttpClient().execute(httpReqeust);
            int code=httpResponse.getStatusLine().getStatusCode();
            String result=EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            //去掉末尾的回车符号
            result=result.split("\n")[0];
            //System.out.println(result);
            results.put("result", result);
            JSONObject jsonObject=new JSONObject(result);
            //JSONObject jsonObject=JSONObject.fromObject(results);
            //JSONObject res= (JSONObject) jsonObject.get("result");
            int replyCode=jsonObject.getInt("reply_code");
            String replyMsg=jsonObject.getString("reply_msg");
            results.put("reply_code",replyCode);
            results.put("reply_msg",replyMsg);
            results.put("code",code);
            return results;
        } catch (Exception e) {
           // e.printStackTrace();
            logger.debug("注销：失败"+e.getMessage());
        }
        return null;
    }

    private static byte[] toTlvBytes(char tag, char length, String value) throws Exception {
        if(value!=null&&value.length()>0){
            byte[] b1=String.valueOf(tag).getBytes();
            byte[] b2=String.valueOf(length).getBytes();
            byte[] b3=value.getBytes();
            byte[] b4=ToolUtil.uniteArry(b1,b2);
            //System.out.println(ToolUtil.bytesToHexString(ToolUtil.uniteArry(b3,b4)));
            return ToolUtil.uniteArry(b4,b3);
        }
        return null;
    }

    /**
     * 根据输入流返回一个字符串
     * @param is
     * @return
     * @throws Exception
     */
    private static String getStringFromInputStream(InputStream is) throws Exception{
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        byte[] buff= new byte[1024];
        int len=-1;
        while((len=is.read(buff))!=-1){
            baos.write(buff,0,len);
        }
        is.close();
        String html=baos.toString();
        baos.close();
        return html;
    }
}
