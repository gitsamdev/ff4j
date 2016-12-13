package org.ff4j.observable;

public class TotoFeatureUsage extends AbstractObservableMixin < FeatureUsageListener > {
    
   
    public void Toggle (String uid) {
        
        this.notify((listener) -> listener.onFeatureExecuted(uid));
    }
    
    
}
