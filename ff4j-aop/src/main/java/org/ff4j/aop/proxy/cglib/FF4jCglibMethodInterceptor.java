package org.ff4j.aop.proxy.cglib;

import java.lang.reflect.Method;

import org.ff4j.FF4j;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * Method Interceptor.
 *
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <T>
 *      current bean to proxify
 */
public class FF4jCglibMethodInterceptor<T> implements MethodInterceptor {
    
    /** Reference to original bean. */
    protected final T original;

    /** Reference to ff4j to allow test feature. */
    protected FF4j ff4j;
    
    /** Feature Name to test. */
    protected String featureName;
    
    protected Object toggleOn;
    
    public FF4jCglibMethodInterceptor(String featureName, Class alterClass) {
        this(null);
    }

    
    public FF4jCglibMethodInterceptor() {
        this(null);
    }

    public FF4jCglibMethodInterceptor(T original) {
        this.original = original;
    }
    
    /** {@inheritDoc} */
    @Override
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        System.out.println("BEFORE");
        if (!ff4j.check(featureName)) {
            return methodProxy.invokeSuper(toggleOn, args);
        }
        // Default behaviour
        return methodProxy.invokeSuper(object, args);
    }   

}
