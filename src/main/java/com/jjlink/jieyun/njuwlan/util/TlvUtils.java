package com.jjlink.jieyun.njuwlan.util;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * 将字符串转换成TLV对象
 * Created by zlu on 15-3-10.
 */
public class TlvUtils {

//    /**
//     * 将字符串编码成16进制字符串,适用于所有字符（包括中文）
//     * @param str
//     * @return
//     * @throws Exception
//     */
//    public static String toHexString(String str) throws Exception {
//        byte[] bytes=str.getBytes("UTF-8");
//        StringBuffer sb= new StringBuffer();
//        for(int i=0; i<str.length();i++){
//            sb.append(str.charAt((bytes[i]&0xf0)>>4));
//            sb.append(str.charAt((bytes[i]&0x0f)>>0));
//        }
//        return sb.toString();
//    }

    /**
     * 将tlv数据包转换成byte[]
     * 便于加密
     * @param tag
     * @param length
     * @param value
     * @return
     * @throws Exception
     */
    public static byte[] toTlvBytes(char tag, char length, String value) throws Exception {
        if(value!=null&&value.length()>0){
            byte[] b1=String.valueOf(tag).getBytes();
            byte[] b2=String.valueOf(length).getBytes();
            byte[] b3=value.getBytes();
            byte[] b4=ToolUtil.uniteArry(b1,b2);
            System.out.println(ToolUtil.bytesToHexString(ToolUtil.uniteArry(b3,b4)));
            return ToolUtil.uniteArry(b4,b3);
        }
        return null;
    }

    public static String[] bytesToHexStrings(byte[] src){
        if (src == null || src.length <= 0) {
            return null;
        }
        String[] str = new String[src.length];

        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                str[i] = "0";
            }
            str[i] = hv;
        }
        return str;
    }


    /**
     * 将16进制字符串转换为TLV对象列表
     * @param hexString
     * @return
     */
    public static List<Tlv> builderTlvList(String hexString){
        List<Tlv> tlvs= new ArrayList<Tlv>();
        int postion=0;

        while(postion!= StringUtils.length(hexString)){
            String _hexTag=getTag(hexString,postion);
            postion +=_hexTag.length();

            LPosition l_position=getLengthAndPosition(hexString,postion);
            int _vl=l_position.get_vL();
            postion=l_position.get_position();
            String _value=StringUtils.substring(hexString,postion,postion+_vl*2);
            postion=postion+_value.length();
            tlvs.add(new Tlv(_hexTag,_vl,_value));
        }
        return tlvs;
    }

    /**
     * 将16进制字符串转换为TLV对象MAP
     * @param hexString
     * @return
     */
    public static Map<String, Tlv> builderTlvMap(String hexString){
        Map<String, Tlv> tlvs=new HashMap<String,Tlv>();
        int position=0;
        while(position !=hexString.length()){
            String _hexTag= getTag(hexString,position);
            position+=_hexTag.length();
            LPosition l_position=getLengthAndPosition(hexString,position);
            int _vl=l_position.get_vL();
            position=l_position.get_position();
            String _value=hexString.substring(position,position+_vl*2);
            position=position+_value.length();

            tlvs.put(_hexTag,new Tlv(_hexTag,_vl,_value));
        }

        return  tlvs;
    }

    /**
     * 返回最后的Value的长度
     * @param hexString
     * @param position
     * @return
     */
    private static LPosition getLengthAndPosition(String hexString, int position){
        String firstByteString= hexString.substring(position,position+2);
        int i=Integer.parseInt(firstByteString,16);
        String hexLength="";

        if(((1>>>7)&1)==0){
            hexLength =hexString.substring(position,position+2);
            position=position+2;
        }else{
            // 当最左侧的bit位为1的时候，取得后7bit的值
            int _L_Len= i & 127;
            position=position+2;
            hexLength=hexString.substring(position,position+_L_Len*2);
            // position表示第一个字节，后面的表示有多少个字节来表示后面的Value值
            position=position+_L_Len*2;
        }

        return new LPosition(Integer.parseInt(hexLength,16),position);
    }

    /**
     * 取得子域Tag标签
     * @param hexString
     * @param position
     * @return
     */
    private static String getTag(String hexString, int position){
        String firstByte= StringUtils.substring(hexString,position,position+2);
        int i=Integer.parseInt(firstByte,16);
        if((i&0x1f)==0x1f){
            return hexString.substring(position,position+4);
        }else{
            return hexString.substring(position,position+2);
        }
    }
}
