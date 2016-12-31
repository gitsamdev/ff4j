package org.ff4j.audit;

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
public abstract class FeatureUsageEventSupport 
                extends AbstractFF4jRepository < Event , FF4jRepositoryEventListener> 
                implements FeatureUsageEventListener, FeatureUsageEventStore {

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
    
    /** {@inheritDoc} */
    @Override
    public void registerAuditListener(AuditTrail auditTrail) {
        // Don't register audit on audit
    }
    
    /** {@inheritDoc} */
    @Override
    public void unRegisterAuditListener() {
        // Don't register audit on audit
    }
}
