package org.ff4j.aop.proxy.cglib;

import java.util.HashMap;
import java.util.Map;

import org.ff4j.utils.Util;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

/**
 * Factory class to create CGLIB proxies of annotated Beans.
 *  
 * @author Cedrick LUNVEN  (@clunven)
 */
public class FF4jCglibProxyFactory {
    
    /** Map of existing proxies. */
    private static Map < String, Object > existingProxies = new HashMap<>();
    
    /**
     * Create proxy based on class with SEVERAL interceptors and a filter to analyze whihch one used.
     *
     * @param targetClass
     *      bean currenty be proxifed (instanciate by CGLIB)
     * @param filter
     *      dispatcher to select which interceptors to invoke
     * @param interceptors
     *      {@link MethodInterceptor}(s) to implement custom behaviour on top of the target.
     * @return
     *      an instance of targetClass wrapped by  CGLIB proxy (built through byte code manipulation).
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class < ? extends T> targetClass, CallbackFilter filter, Callback... interceptors) {
        Util.requireNotNull(targetClass);
        if (!existingProxies.containsKey(targetClass.getName())) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(targetClass);
            enhancer.setCallbacks(interceptors);
            enhancer.setCallbackFilter(filter);
            existingProxies.put(targetClass.getName(), enhancer.create());
        }
        return (T) existingProxies.get(targetClass.getName());
    }
    
    /**
     * Create proxy based on class.
     *
     * @param targetClass
     *      target class
     * @param interceptor
     *      current interceptor
     * @return
     *      proxifed class
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class < ? extends T> targetClass, MethodInterceptor interceptor) {
        Util.requireNotNull(targetClass);
        if (!existingProxies.containsKey(targetClass.getName())) {
            Enhancer enhancer = new Enhancer();
            enhancer.setSuperclass(targetClass);
            enhancer.setCallback(interceptor);
            existingProxies.put(targetClass.getName(), enhancer.create());
        }
        return (T) existingProxies.get(targetClass.getName());
    }

}
