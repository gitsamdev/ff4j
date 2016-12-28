package org.ff4j.audit.usage;

import org.ff4j.feature.Feature;

/**
 * Listener meant to be invoked when a feature is executed.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
@FunctionalInterface
public interface FeatureUsageListener {
    
    /**
     * Execute operation when new feature is executed.
     *
     * @param feature
     *      target feature
     */
    void onFeatureExecuted(Feature feature);

}
