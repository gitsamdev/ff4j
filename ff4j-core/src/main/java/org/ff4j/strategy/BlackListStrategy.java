package org.ff4j.strategy;

import org.ff4j.FF4jContext;
import org.ff4j.feature.Feature;

/**
 * BLOCK acces for defined list of Clients.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class BlackListStrategy extends ClientFilterStrategy {

    /**
     * Default Constructor.
     */
    public BlackListStrategy() {
        super();
    }

    /**
     * Parameterized constructor.
     * 
     * @param threshold
     *            threshold
     */
    public BlackListStrategy(String clientList) {
        super(clientList);
    }
    
   /**
    * {@inheritDoc}
    */
    @Override
    public boolean isToggled(Feature feature, FF4jContext executionContext) {
        return !super.isToggled(feature, executionContext);
    }
}
