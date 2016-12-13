package org.ff4j.observable;

/**
 * Listener meant to be invoked when a feature is executed.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
@FunctionalInterface
public interface FeatureUsageListener {
    
    void onFeatureExecuted(String uid);

}
