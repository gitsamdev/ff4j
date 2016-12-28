package org.ff4j.aop.proxy.cglib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.ff4j.FF4j;
import org.ff4j.aop.proxy.annotation.FeatureToggle;
import org.ff4j.aop.proxy.service.SpeakEnglish;
import org.ff4j.aop.proxy.service.SpeakFrench;
import org.ff4j.aop.proxy.service.SpeakService;

import net.sf.cglib.proxy.MethodInterceptor;

public class ProxyCglibDemo {
    
    @FeatureToggle(feature="f1", on=SpeakFrench.class, off=SpeakEnglish.class)
    private SpeakService speakInterface;
    
    @FeatureToggle(feature="f1", on=SpeakFrench.class)
    private SpeakEnglish speakConcreteClass;
    
    // Lui aussi un proxu qui execture cette methode avant d'appeler les autres ?
    public ProxyCglibDemo() throws Exception {
        
        // Post Initialisation
        // Need FF4J
        
    }
    
    public void useSpeak() {
        speakInterface.sayHello("Cedrick");
    }
    
    public static void main(String[] args) throws Exception {
        
        ProxyCglibDemo demo = new ProxyCglibDemo();
        
        FF4j ff4j = new FF4j();
        ProxyCglibDemo.populateAnnotatedAttributes(ff4j, demo);
        
        demo.useSpeak();
    }
    
    public static Method findSetter(Object bean, String attributeName) {
        return null;
        
    }
    
    public static void populateAnnotatedAttributes(FF4j ff4j, Object o) throws Exception{
        
        MethodInterceptor featureToggler = new FF4jCglibMethodInterceptor<>();
        
        for (Field f : o.getClass().getDeclaredFields()) {
            // Put annotated field as accessible to detect if an annotation is there
            f.setAccessible(true);
            // Expect to find the annotation
            if (f.isAnnotationPresent(FeatureToggle.class)) {
                FeatureToggle featureToggle = f.getAnnotation(FeatureToggle.class);
                // If annotation and interface injecting the 'off' as default value and proxy
                if (f.getType().isInterface()) {
                    // Proxy CGLIB to feature toggle : Default is there the 'off' attribute (as current is interface)
                    f.set(o, FF4jCglibProxyFactory.createProxy(featureToggle.off(), featureToggler));
                } else {
                 // Proxy CGLIB to feature toggle : Default is there the current class (concrete)
                    f.set(o, FF4jCglibProxyFactory.createProxy(f.getType(), featureToggler));
                }
            }
        }
    }
   

}
