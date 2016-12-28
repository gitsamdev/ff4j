package org.ff4j.audit.usage;

import org.ff4j.event.Event;
import org.ff4j.feature.Feature;
import org.ff4j.store.AbstractFF4jRepository;

/**
 * Allow to track features usage.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class AbstractFeatureUsageService 
                extends AbstractFF4jRepository < Event > 
                implements FeatureUsageListener, FeatureUsageService {

    /** serialVersionUID. */
    private static final long serialVersionUID = -8194421012227669426L;
    
    /** {@inheritDoc} */
    @Override
    public void onFeatureExecuted(Feature feature) {
        featureUsageHit(feature);
    }
   
}
