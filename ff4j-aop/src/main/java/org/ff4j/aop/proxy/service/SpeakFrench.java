/**
 * 
 */
package org.ff4j.aop.proxy.service;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN  (@clunven)
 * @author Andre Blaszczyk (@AndrBLASZCZYK)
 *
 */
public class SpeakFrench implements SpeakService {

    public SpeakFrench() {
        System.out.println("create new SpeakFrench");
    }
    
    /** {@inheritDoc} */
    @Override
    public void sayHello(String foo) {
        System.out.println("Bonjour " + foo);
    }

}
