package com.atguigu.gmall.cache.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

/**
 * @author HaQQ
 * @version 1.0
 * @date 2022/8/28 16:37
 */
public class Jsons {

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toStr(Object obj) {
        try {
            String s = mapper.writeValueAsString(obj);
            return s;
        } catch (JsonProcessingException e) {
            return null;
        }

    }

    public static<T> T toObj(String str,Class<T> clz){
        if (StringUtils.isEmpty(str)){
            return null;
        }
        try {
            T t = mapper.readValue(str, clz);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static<T> T toObj(String str, TypeReference<T> tTypeReference){
        if (StringUtils.isEmpty(str)){
            return null;
        }
        try {
            T t = mapper.readValue(str,tTypeReference);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
