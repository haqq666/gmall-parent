package com.atguigu.gmall.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

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
        try {
            T t = mapper.readValue(str, clz);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
