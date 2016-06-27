package com.malalaoshi.android.util;

import com.malalaoshi.android.core.utils.EmptyUtils;

/**
 * Created by kang on 16/6/27.
 */
public class ConversionUtils {
    public static Long convertToLong(String string, Long defaultValue){
        Long value = ConversionUtils.convertToLong(string);
        if (value!=null){
            return value;
        }else{
            return defaultValue;
        }
    }

    public static Long convertToLong(String string){
        if (EmptyUtils.isEmpty(string)){
            return null;
        }
        try{
            return Long.valueOf(string);
        }catch (Exception e){
            return null;
        }
    }


    public static Integer convertToInt(String string, Integer defaultValue){
        Integer value = ConversionUtils.convertToInt(string);
        if (value!=null){
            return value;
        }else{
            return defaultValue;
        }
    }

    public static Integer convertToInt(String string){
        if (EmptyUtils.isEmpty(string)){
            return null;
        }
        try{
            return Integer.valueOf(string);
        }catch (Exception e){
            return null;
        }
    }

    public static Double convertToDouble(String string, Double defaultValue){
        Double value = ConversionUtils.convertToDouble(string);
        if (value!=null){
            return value;
        }else{
            return defaultValue;
        }
    }

    public static Double convertToDouble(String string){
        if (EmptyUtils.isEmpty(string)){
            return null;
        }
        try{
            return Double.valueOf(string);
        }catch (Exception e){
            return null;
        }
    }

    public static Float convertToFloat(String string, Float defaultValue){
        Float value = ConversionUtils.convertToFloat(string);
        if (value!=null){
            return value;
        }else{
            return defaultValue;
        }
    }

    public static Float convertToFloat(String string){
        if (EmptyUtils.isEmpty(string)){
            return null;
        }
        try{
            return Float.valueOf(string);
        }catch (Exception e){
            return null;
        }
    }

}
