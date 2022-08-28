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

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static String toStr(Object obj) {
        try {
            String s = objectMapper.writeValueAsString(obj);
            return s;
        } catch (JsonProcessingException e) {
            return null;
        }

    }
}
