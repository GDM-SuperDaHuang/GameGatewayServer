package com.slg.module.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 放到外层，确保spring启动优先创建BeanTool
 */
@Component
public class BeanTool implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static final ConcurrentHashMap<Class<?>, Object> beanCache = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanTool.applicationContext = applicationContext;
    }

    public static Object getBean(String name) {
        //name表示其他要注入的注解name名
        return applicationContext.getBean(name);
    }
    /**
     * 拿到ApplicationContext对象实例后就可以手动获取Bean的注入实例对象
     */
//    public static <T> T getBean(Class<T> clazz) {
//
//        return applicationContext.getBean(clazz);
//    }


    public static <T> T getBean(Class<T> clazz) {
        Map<String, T> beansOfType = applicationContext.getBeansOfType(clazz);
        if (!beansOfType.isEmpty()) {
            // 返回第一个找到的 bean，基于 Map 的迭代顺序
            return beansOfType.values().iterator().next();
        }
        return null;
    }
}
