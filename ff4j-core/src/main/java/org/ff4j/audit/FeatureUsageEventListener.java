package org.ff4j.audit;

import org.ff4j.feature.Feature;
import org.ff4j.store.FF4jRepository;

/**
 * Listener invoked by the observer (notify) when a feature is used. 
 * 
 * There can be as many listeners as you want but the default implementation is to store
 * events into external DB leveraging on {@link FF4jRepository}.
 * 
 * @see {@link FeatureUsageEventStore}
 * @see {@link FeatureUsageEventSupport}
 * 
 * @author Cedrick LUNVEN  (@clunven)
 */
@FunctionalInterface
public interface FeatureUsageEventListener {
    
    /**
     * Execute operation when new feature is executed.
     *
     * @param feature
     *      target feature
     */
    void onFeatureExecuted(Feature feature);

}
