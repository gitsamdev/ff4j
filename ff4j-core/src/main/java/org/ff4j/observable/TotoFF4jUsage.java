package org.ff4j.observable;

import org.ff4j.event.EventScope;
import org.ff4j.feature.Feature;

public class TotoFF4jUsage extends AbstractObservableMixin < FF4jRepositoryListener < Feature > > {
    
    public void create() {
        
        this.notify(listener -> listener.onCreate(EventScope.FEATURE, new Feature("s")));
    }
    
}
