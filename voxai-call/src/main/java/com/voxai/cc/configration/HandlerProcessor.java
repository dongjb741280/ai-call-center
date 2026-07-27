package com.voxai.cc.configration;

import com.voxai.VoxaiCallApplication;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dongjb
 * @date 2026/07/27
 */
@Component
public class HandlerProcessor implements BeanFactoryPostProcessor {

    private String handlerPackage = VoxaiCallApplication.class.getPackage().getName();

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        Map<String, Class> handlerMap = new HashMap<>();
        ClassScanerUtil.scan(handlerPackage, HandlerType.class).forEach(clazz -> {
            String type = clazz.getAnnotation(HandlerType.class).value();
            handlerMap.put(type, clazz);
        });
        HandlerContext context = new HandlerContext(handlerMap);
        beanFactory.registerSingleton(HandlerContext.class.getName(), context);
    }
}
