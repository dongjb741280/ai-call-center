package org.voice9.cc.configration;

import java.lang.annotation.*;

/**
 * Create by dongjb on 2025/09/03
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HandlerType {
    String value();
}