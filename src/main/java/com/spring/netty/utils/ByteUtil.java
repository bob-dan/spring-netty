package com.spring.netty.utils;


import com.spring.netty.entity.Body;
import com.spring.netty.entity.Message;

import java.io.*;
import java.util.Map;

public class ByteUtil {

    public static byte[] toByteArray(Object obj){
        if(obj == null){
            return null;
        }
        byte[] result =null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos =  oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            result = bos.toByteArray();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Object byteToObject(byte[] obj){
        if(obj==null || obj.length==0){
            return null;
        }
        Object result =null;
        ByteArrayInputStream bis = new ByteArrayInputStream(obj);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            result = ois.readObject();
            ois.close();
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static int calculateLenth(Message msg){
        int total = 18;
        Map<String,Object> attachment = msg.getHead().getAttachment();
        if(attachment!=null && !attachment.isEmpty()){
            int maplen = 2;//k-v对个数所占长度初始化
            for(Map.Entry<String,Object> entry : attachment.entrySet()){
                int keylen = entry.getKey().length();
                Object value = entry.getValue();
                byte[] v = ByteUtil.toByteArray(value);
                int vlen = v.length;
                maplen = maplen+keylen+vlen;
            }
            total+=maplen;
        }
        Body body = msg.getBody();
        if(null !=body){
            Object payload = body.getPayload();
            byte[] data = ByteUtil.toByteArray(payload);
            if(data!=null){
                total+=data.length;
            }
        }
        return total;
    }
}