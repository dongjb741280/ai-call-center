package com.voxai.cc.configration;

/**
 * @author dongjb
 * @date 2026/07/27
 */
public interface Handler<T> {

    void handleEvent(T event);

}
