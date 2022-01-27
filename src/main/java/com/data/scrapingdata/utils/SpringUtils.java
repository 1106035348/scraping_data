package com.data.scrapingdata.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Spring容器上下文操作
 * <p>
 * Date: 2019/7/5 15:48
 * Created by luoyingxiong
 */
@Component
public class SpringUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static DefaultListableBeanFactory defaultListableBeanFactory;

    /**
     * @param applicationContext
     * @throws BeansException
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (SpringUtils.applicationContext == null) {
            SpringUtils.applicationContext = applicationContext;
        }
        if (SpringUtils.defaultListableBeanFactory == null) {
            ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;
            SpringUtils.defaultListableBeanFactory = (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();
        }
//        System.out.println("---------------------------------------------------------------------");
//
//        System.out.println("---------------------------------------------------------------------");
//
//        System.out.println("---------------me.shijunjie.util.SpringUtil------------------------------------------------------");
//
//        System.out.println("========ApplicationContext配置成功,在普通类可以通过调用SpringUtils.getAppContext()获取applicationContext对象,applicationContext="+SpringUtils.applicationContext+"========");
//
//        System.out.println("---------------------------------------------------------------------");
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return defaultListableBeanFactory;
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    //通过name,删除指定的Bean
    public static void rmeoveBean(String name) {
        defaultListableBeanFactory.removeBeanDefinition(name);
    }

    //通过name 判断bean是否存在
    public static void containBean(String name) {
        defaultListableBeanFactory.containsBean(name);
    }

    public static <T>T registerBean(String name, Class<T> clazz) {
        boolean containsBean = defaultListableBeanFactory.containsBean(name);
        if (containsBean) {
            defaultListableBeanFactory.removeBeanDefinition(name);
        }
        //默认使用对象的无参构造方法：此对象已经重写无参构造（此处会重新实例化一个新对象）
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
        //构造注入对象参数
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
//        beanDefinition.setAutowireMode(2);
        beanDefinition.setPrimary(false);
        beanDefinition.setSynthetic(false);
//        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        beanDefinition.setScope("singleton");
        beanDefinition.setRole(0);
        defaultListableBeanFactory.registerBeanDefinition(name, beanDefinition);
        return (T) getBean(name);
    }

    public static String[] getActiveProfiles(){
        return getApplicationContext().getEnvironment().getActiveProfiles();
    }



}
