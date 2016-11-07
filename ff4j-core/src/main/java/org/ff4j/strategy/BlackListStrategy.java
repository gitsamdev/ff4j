package org.ff4j.strategy;

import org.ff4j.feature.FlippingExecutionContext;
import org.ff4j.store.FeatureStore;

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
    public boolean evaluate(String featureName, FeatureStore store, FlippingExecutionContext executionContext) {
        return !super.evaluate(featureName, store, executionContext);
    }
}
