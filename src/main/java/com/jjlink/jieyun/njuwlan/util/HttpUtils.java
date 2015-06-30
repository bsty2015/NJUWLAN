package com.jjlink.jieyun.njuwlan.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Created by zlu on 15-3-4.
 */
public class HttpUtils {

    public static String submitPostData(String strUrlPath,Map<String,String> params, String encode){
        byte[] data = getRequestData(params,encode).toString().getBytes();//获得请求体

        try {
            URL url= new URL(strUrlPath);
            HttpURLConnection httpURLConnection=(HttpURLConnection)url.openConnection();
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoInput(true);         //打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true);         //打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setUseCaches(false);       //使用Post方式不能使用缓存

            //设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-type","application/x-www-form-urlencoded");
            //设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
            //获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            //获得服务器的响应码
            int response =httpURLConnection.getResponseCode();
            if(response==httpURLConnection.HTTP_OK) {
                InputStream inputStream=httpURLConnection.getInputStream();
                return dealResponseResult(inputStream);
            }
        }catch(Exception e) {
            return "err: "+e.getMessage().toString();
        }
        return "-1";
    }

    private static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer sb=new StringBuffer();//存储封装好的请求信息
        try {
        for(Map.Entry<String , String> entry: params.entrySet()){

                sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(),encode)).append("&");
            }
            sb.deleteCharAt(sb.length() - 1);    //删除最后的一个"&"
        }catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sb;
    }


    /*
   * Function  :   处理服务器的响应结果（将输入流转化成字符串）
   * Param     :   inputStream服务器的响应输入流
   */
    public static String dealResponseResult(InputStream inputStream) {
        String resultData = null;      //存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }


}

