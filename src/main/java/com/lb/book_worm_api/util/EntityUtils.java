package com.lb.book_worm_api.util;

import java.lang.reflect.Field;

public class EntityUtils {

    private EntityUtils(){}

    public static <T> void copyNonNullProperties(T source, T target){
        for (Field field : source.getClass().getDeclaredFields()){
            field.setAccessible(true);
            try {
                Object value = field.get(source);
                if (value != null){
                    field.set(target, value);
                }
            } catch (IllegalAccessException ignored) {}
        }
    }
}
