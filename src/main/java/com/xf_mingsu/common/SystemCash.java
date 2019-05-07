package com.xf_mingsu.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SystemCash{

    private static Map<String,Object> cachMap = new ConcurrentHashMap<>();

    public static Object getKey(String key) {
        return cachMap.get(key);
    }

    public static void setKey(String key, Object value) {
        cachMap.put(key,value);
    }

}
