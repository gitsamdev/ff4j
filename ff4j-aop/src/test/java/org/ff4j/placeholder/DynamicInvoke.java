package org.ff4j.placeholder;

import org.ff4j.FF4j;
import org.ff4j.property.PropertyString;
import org.ff4j.spring.autowire.FF4JProperty;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class DynamicInvoke implements ApplicationContextAware {
    
    public static interface MySampleService { void doSomething(); }
    
    public static class MySampleServiceImpl1 implements MySampleService { 
        public void doSomething() { System.out.println("x"); } 
    }
    public static class MySampleServiceImpl2 implements MySampleService { 
        public void doSomething() { System.out.println("Y"); } 
    }
    
    @Autowired
    private FF4j ff4j;
    
    private ApplicationContext ctx;
    
    public void mySampleMethodsToToggle() {
        
        String targetBeanName = ff4j.getProperty("testProperty").asString();
        MySampleService s = (MySampleService) ctx.getBean(targetBeanName);
        s.doSomething();
        
    }

    @Override
    public void setApplicationContext(ApplicationContext appCtx) throws BeansException {
        ctx = appCtx;
    }
    

}
