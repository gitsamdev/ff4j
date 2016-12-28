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
public class SpeakEnglish implements SpeakService {

    /** {@inheritDoc} */
    @Override
    public void sayHello(String foo) {
        System.out.println("Hello " + foo);
    }

}
