package org.voice9.cc.configration;

/**
 * Create by dongjb on 2025/09/03
 */
public interface Handler<T> {

    void handleEvent(T event);

}
