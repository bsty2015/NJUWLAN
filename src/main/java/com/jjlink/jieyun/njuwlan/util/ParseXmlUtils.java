package com.jjlink.jieyun.njuwlan.util;

import android.util.Log;

import com.jjlink.jieyun.njuwlan.entity.UpInfo;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * 解析xml代码的工具类(这个类主要将从服务器端下载的xml解析到uptInfo实体类中)
 * Created by zlu on 15-3-18.
 */
public class ParseXmlUtils {
    /**
     * 解析xml文件
     * @param in
     * @return
     */
    public static UpInfo parseXml(InputStream in){
        UpInfo upInfo= new UpInfo();
        DocumentBuilderFactory dbf=DocumentBuilderFactory.newInstance();
        DocumentBuilder db=null;
        try {
            db=dbf.newDocumentBuilder();
            Document doc=null;
            doc=db.parse(in);
            Element root=doc.getDocumentElement();
            NodeList resultNode=root.getElementsByTagName("info");
            System.out.println(resultNode.getLength());
            for(int i=0;i<resultNode.getLength();i++){
                Element res= (Element) resultNode.item(i);
                upInfo.setVersion(res.getElementsByTagName("version").item(0).getFirstChild().getNodeValue());
                upInfo.setUrl(res.getElementsByTagName("url").item(0).getFirstChild().getNodeValue());
                upInfo.setDescription(res.getElementsByTagName("description").item(0).getFirstChild().getNodeValue());
            }
        } catch (Exception e) {
            Log.i("ex",e.getMessage());
        }
        System.out.println(upInfo.toString());
        return upInfo;
    }


}
