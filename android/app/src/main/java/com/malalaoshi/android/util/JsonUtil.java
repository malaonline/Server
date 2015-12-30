package com.malalaoshi.android.util;

import android.content.Context;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;


/**
 * Json数据处理工具类
 */
public class JsonUtil {

    /**
     * 测试使用
     * @param id       文件id(对应raw中存放的json文件)
     * @param t        json转换后数据对象类
     * @param context  上下文
     * @param <T>
     * @return
     */
    public static <T> T parseData(int id, Class<T> t, Context context) {
        InputStream inputStream = null;
        InputStreamReader isReader = null;
        BufferedReader reader = null;
        T t1 = null;
        try {
            Gson gson = new Gson();
            String jsonStr = "";
            inputStream = context.getResources().openRawResource(id);
            isReader = new InputStreamReader(inputStream);
            reader = new BufferedReader(isReader);
            StringBuffer buffer = new StringBuffer();
            while ((jsonStr = reader.readLine()) != null) {
                buffer.append(jsonStr);
            }
            t1 =(T)gson.fromJson(buffer.toString(), t);
        } catch (Exception e) {
            //throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }finally {
            try {
                if (reader!=null){
                reader.close();
                }
                if (isReader!=null){
                    isReader.close();
                }
                if (inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return t1;
        }
    }

    /**
     * 将jsonString转换为json对象
     * @param strJson   json字符串
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T parseStringData(String strJson, Class<T> t) {
        try {
            Gson gson = new Gson();
            T t1 =(T)gson.fromJson(strJson, t);
            return t1;
        } catch (Exception e) {
            //throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将jsonString转换为json对象
     * @param strJson
     * @param typeOfT
     * @param <T>
     * @return
     */
    public static <T> T parseStringData(String strJson, Type typeOfT) {
        try {
            Gson gson = new Gson();
            T t1 =(T)gson.fromJson(strJson, typeOfT);
            return t1;
        } catch (Exception e) {
            //throw new HttpMessageNotReadableException("Could not read JSON: " + e.getMessage(), e);
        }
        return null;
    }

    /**
     * 将json对象转换成json字符串
     * @param value
     * @return
     */
    public static String SerialData(Object value){
        Gson gson = new Gson();
        String str = gson.toJson(value);
        return str;
    }

}
