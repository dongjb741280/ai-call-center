package com.voxai.cc.configration;

import java.lang.annotation.*;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HandlerType {
    String value();
}