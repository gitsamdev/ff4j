package org.ff4j.audit.usage;

import org.ff4j.event.Event;
import org.ff4j.feature.Feature;
import org.ff4j.store.AbstractFF4jRepository;
import org.ff4j.store.FF4jRepositoryEventListener;
import org.ff4j.store.FF4jRepositoryListener;

/**
 * Allow to track features usage.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class AbstractFeatureUsageService 
                extends AbstractFF4jRepository < Event , FF4jRepositoryEventListener> 
                implements FeatureUsageListener, FeatureUsageService {

    /** serialVersionUID. */
    private static final long serialVersionUID = -8194421012227669426L;
    
    /** {@inheritDoc} */
    @Override
    public void onFeatureExecuted(Feature feature) {
        featureUsageHit(feature);
    }
    
    /** {@inheritDoc} */
    @Override
    public void registerListener(String name, FF4jRepositoryListener<Event> listener) {
        // Enforce subclass to reach AbstractObservable.registerListener(..)
        registerListener(name, (FF4jRepositoryEventListener) listener);
    }
    
   
}
