package org.ff4j.cache;

import org.ff4j.property.Property;
import org.ff4j.store.PropertyStore;


/**
 * Poll target stores on a fixed delay basis and fill cache to avoid reaching TTL of key.
 * 
 * @author Cedrick LUNVEN (@clunven)
 */
public class PropertyStoreCachePollingScheduler extends CachePollingScheduler < Property<?> > {
    
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
    public PropertyStoreCachePollingScheduler(PropertyStore sf, CacheManager<String, Property<?>> cp) {
        worker = new CacheWorker<Property<?>>(sf, cp);
        initExecutor("FF4j_Polling_PropertyStore2Cache");
    }

}
