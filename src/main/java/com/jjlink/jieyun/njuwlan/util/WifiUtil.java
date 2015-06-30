package com.jjlink.jieyun.njuwlan.util;

import android.app.Application;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

/**
 * Created by zlu on 15-3-6.
 */
public class WifiUtil{
    private WifiManager wifiManager;
    private WifiInfo wifiInfo;
    private List<ScanResult> wifiList;
    private List<WifiConfiguration> wifiConfiguration;
    WifiManager.WifiLock wifiLock;



    //构造器
    public WifiUtil(Context context){
        wifiManager=(WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiInfo=wifiManager.getConnectionInfo();

    }

    /**
     * 打开wifi
     */
    public void openWifi(){
        if(!wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(true);
        }
    }

    /**
     * 关闭wifi
     */
    public void closeWifi(){
        if(wifiManager.isWifiEnabled()){
            wifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查当前wifi状态
     */
    public int checkState(){
        return wifiManager.getWifiState();
    }

    /**
     * 锁定wifi
     */
    public void acquireWifiLock(){
        wifiLock.acquire();
    }

    /**
     * 解锁wifi
     */
    public void releaseWifiLock(){
        if(wifiLock.isHeld()){
            wifiLock.acquire();
        }
    }

    /**
     * 创建一个wifiLock
     */
    public void createWifiLock(){
        wifiLock=wifiManager.createWifiLock("wifiLock");
    }

    /**
     * 得到配置好的网络
     */
    public List<WifiConfiguration> getConfiguration(){
        return wifiConfiguration;
    }

    /**
     * 指定配置好的网络进行连接
     */
    public void connectConfiguration(int index){
        //索引大于配置好的网络索引返回
        if(index>wifiConfiguration.size()){
            return;
        }
        wifiManager.enableNetwork(wifiConfiguration.get(index).networkId,true);
    }

    /**
     * 扫描wifi
     */
    public void startScan(){
        wifiManager.startScan();
        wifiList=wifiManager.getScanResults();
        //得到配置好的网络连接
        wifiConfiguration=wifiManager.getConfiguredNetworks();
    }

    /**
     * 得到网络列表
     */
    public List<ScanResult> getWifiList(){
        return wifiList;
    }

    /**
     * 查看扫描结果
     */
    public StringBuilder lookUpScan(){
        StringBuilder stringBuilder=new StringBuilder();
        for(int i=0; i<wifiList.size();i++){
            stringBuilder.append("index_"+new Integer(i+1).toString()+":");
        //将scanResult信息转换成一个字符串包
        //其中包括:BSSID SSID capabilities frequency level
            stringBuilder.append(wifiList.get(i).toString());
            stringBuilder.append("/n");
        }
        return stringBuilder;
    }

    /**
     * 得到mac地址
     */
    public String getMacAddress(){
        return(wifiInfo==null)?"NULL":wifiInfo.getMacAddress();
    }

    /**
     *得到接入点的BSSID
     */
    public String getBSSID(){
        return(wifiInfo==null)?"NULL":wifiInfo.getBSSID();
    }

    /**
     * 得到IP地址
     */
    public int getIPAddress(){
        return (wifiInfo==null)?0:wifiInfo.getIpAddress();
    }

    /**
     * 得到连接的ID
     */
    public int getNetworkId(){
        return(wifiInfo==null)?0:wifiInfo.getNetworkId();
    }

    /**
     * 得到连接的SSID
     * @return
     */
    public String getSSID(){
        return (wifiInfo==null)?"NULL":wifiInfo.getSSID();
    }
    /**
     * 得到wifiInfo的所有信息包
     */
    public String getWifiInfo(){
        return(wifiInfo==null)?"NULL":wifiInfo.toString();
    }

    /**
     * 添加一个网络并连接
     */
    public void addNetwork(WifiConfiguration wcg){
        int wcgID=wifiManager.addNetwork(wcg);
        boolean b= wifiManager.enableNetwork(wcgID,true);
       // System.out.println("id--"+wcgID);
       // System.out.println("boolean--"+b);
    }

    /**
     * 断开指定id的网络
     * @param netId
     */
    public void disconnectWifi(int netId){
        wifiManager.disableNetwork(netId);
        wifiManager.disconnect();
    }

    /**
     *
     */

    public WifiConfiguration createWifiInfo(String SSID, String password, int type){
        WifiConfiguration config=new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID="\""+SSID+"\"";
        WifiConfiguration tempConfig=this.isExists(SSID);
        if(tempConfig!=null){
            wifiManager.removeNetwork(tempConfig.networkId);
            //wifiManager.is5GHzBandSupported();
        }
        if(type==1){ //WIFICIPHER_NOPASS
            //config.wepKeys[0]="";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            //config.wepTxKeyIndex=0;
        }
        if(type==2){//WIFICIPHER_WEP
            config.hiddenSSID=true;
            config.wepKeys[0]="\""+password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex=0;
        }
        if(type==3){//WIFICIPHER_WPA
            config.preSharedKey="\""+password+"\"";
            config.hiddenSSID=true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status=WifiConfiguration.Status.ENABLED;
        }
        return config;
    }


    private WifiConfiguration isExists(String SSID){
        openWifi();
        List<WifiConfiguration> existingConfigs=wifiManager.getConfiguredNetworks();
        if(existingConfigs!=null)
        for(WifiConfiguration existingConfig:existingConfigs){
            if(existingConfig.SSID.equals("\""+SSID+"\"")){
                return existingConfig;
            }
        }
        return null;
    }
}

