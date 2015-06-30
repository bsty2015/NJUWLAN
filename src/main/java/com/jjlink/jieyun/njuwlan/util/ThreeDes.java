package com.jjlink.jieyun.njuwlan.util;



/*
import org.apache.xerces.utils.Base64;*/
import android.util.Base64;
import android.util.Log;

import java.util.Arrays;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by zlu on 15-3-10.
 */
public class ThreeDes {
    private static final String Algorithm="DESede";//定义加密算法，可用DES,DESede,Blowfish
    //private static final String CIPHER_ALGORITHM = "DESede/CFB/NoPadding";
    /**
     *  3des加密
     * @param keybyte 加密密钥，长度为24位
     * @param src 被加密的数据缓冲区（源）
     * @return
     */
    public static byte[] encryptMode(byte[] keybyte, byte[] src){
        try {
            SecretKey deskey=new SecretKeySpec(keybyte,Algorithm);
            Log.i("keybyte:------",Arrays.toString(keybyte));
           // System.out.println(deskey==null);
            Cipher c=Cipher.getInstance(Algorithm);
            c.init(Cipher.ENCRYPT_MODE,deskey);
            return c.doFinal(src);
        }  catch (Exception e) {
           Log.i("ex:",e.toString());
        }
        return null;
    }

    /**
     * 3des解密
     * @param keybyte
     * @param src
     * @return
     */
    public static byte[] decryptMode(byte[] keybyte, byte[] src){
        try {
            SecretKey deskey=new SecretKeySpec(keybyte,Algorithm);
            Cipher c1=Cipher.getInstance(Algorithm);
            c1.init(Cipher.DECRYPT_MODE,deskey);
            return c1.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 3DES+BASE64加密
     * @param keybyte
     * @param src
     * @return
     * @throws Exception
     */
    public static String toBase64Des(byte[] keybyte, byte[] src ) throws Exception {
        byte[] result=encryptMode(keybyte,src);
        System.out.println(Arrays.toString(result));
        //return Base64.encodeToString(result,0,result.length,Base64.DEFAULT);
        //return new String(Base64.encode(result,Base64.DEFAULT));
        return Base64.encodeToString(result,Base64.NO_WRAP);
    }



    /**
     * 取3des加密的key
     * 生成32位（int）随机数，随机数MD5加密后取第15位的ascii值对8取模，
     * 将得到的结果作为加密后字符串的起始位置，向后取出24位作为加密的密钥
     * @return
     */
    public static String getKey(String randStr) throws Exception {
        String key=null;
        String keyStr=MD5.GetMD5Code(randStr);
        char c=keyStr.charAt(14);
        int m= Integer.valueOf(c);
        m=m%8;
        key=keyStr.substring(m,m+24);
        return key;
    }

    /**
     * 随机的32位int
     * @return
     */
    public static String getRandInt(){
        return Integer.toString(new Random().nextInt());
    }


}
