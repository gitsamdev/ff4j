package org.ff4j.strategy;

import org.ff4j.FF4jExecutionContext;
import org.ff4j.feature.FeatureStore;

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
    public boolean evaluate(String featureName, FeatureStore store, FF4jExecutionContext executionContext) {
        return !super.evaluate(featureName, store, executionContext);
    }
}
