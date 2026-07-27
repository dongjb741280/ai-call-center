package com.voxai.cc.configration;

import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public class HandlerContext {

    private Map<String, Class> handlerMap;

    public HandlerContext(Map<String, Class> handlerMap) {
        this.handlerMap = handlerMap;
    }


    public Handler getInstance(String type) {
        Class clazz = handlerMap.get(type);
        if (clazz == null) {
            return null;
        }
        return (Handler) BeanUtil.getBean(clazz);
    }



}
