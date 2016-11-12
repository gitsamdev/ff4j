package org.ff4j.cache;

import org.ff4j.feature.Feature;
import org.ff4j.store.FeatureStore;


/**
 * Poll target stores on a fixed delay basis and fill cache to avoid reaching TTL of key.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class FeatureStoreCachePollingScheduler extends CachePollingScheduler < Feature > {
    
    /** serialVersionUID. */
    private static final long serialVersionUID = 1L;

    /**
     * Parameterized constructor.
     *
     * @param sf
     *      source feature store
     * @param sp
     *      source property store
     * @param cp
     *      current cache manager
     */
    public FeatureStoreCachePollingScheduler(FeatureStore sf, CacheManager<String, Feature> cp) {
        worker = new CacheWorker<Feature>(sf, cp);
        initExecutor("FF4j_Polling_FeatureStore2Cache");
    }

}
