package org.ff4j.cache;

import java.util.Optional;
import java.util.stream.Stream;

import org.ff4j.property.Property;
import org.ff4j.store.FeatureStore;
import org.ff4j.store.PropertyStore;

/**
 * Access to {@link FeatureStore} could generate some overhead and decrease performances. This is the reason why cache is provided
 * though proxies.
 * 
 * As applications are distributed, the cache itself could be distributed. The default implement is
 * {@link InMemoryFeatureStoreCacheProxy} but other are provided to use distributed cache system as redis or memcached.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class PropertyStoreCacheProxy extends CacheProxy< String, Property<?>> implements PropertyStore {

    /** Target property store to be proxified to cache properties. */
    private PropertyStore targetPropertyStore;
    
    /**
     * Initialization through constructor.
     * 
     * @param store
     *            target store to retrieve features
     * @param cache
     *            cache manager to limit overhead of store
     */
    public PropertyStoreCacheProxy(PropertyStore fStore, CacheManager< String, Property<?> > cache) {
        this.cacheManager        = cache;
        this.targetPropertyStore  = fStore;
        this.scheduler = new PropertyStoreCachePollingScheduler(fStore, cache);
    }
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        getTargetPropertyStore().createSchema();
    }
    
    /** {@inheritDoc} */
    @Override
    public Property<?> read(String name, Property<?> defaultValue) {
        Optional < Property<?> > fp = getCacheManager().get(name);
        // Not in cache but may has been created from now
        // Or in cache but with different value that default
        if (!fp.isPresent()) {
            fp = Optional.of(getTargetPropertyStore().read(name, defaultValue));
            getCacheManager().put(fp.get());
        }
        return fp.get();
    }

    /** {@inheritDoc} */
    @Override
    public void update(String name, String newValue) {
        // Retrieve the full object from its name
        Property<?> fp = getTargetPropertyStore().read(name);
        fp.setValueFromString(newValue);
        // Update value in target store
        getTargetPropertyStore().update(fp);
        // Remove from cache old value
        getCacheManager().evict(fp.getUid());
        // Add new value in the cache
        getCacheManager().put(fp);
    }

    /** {@inheritDoc} */
    @Override
    public Stream<String> listPropertyNames() {
        return getTargetPropertyStore().listPropertyNames();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return getTargetPropertyStore().isEmpty();
    } 
    
    /**
     * Getter accessor for attribute 'target'.
     * 
     * @return current value of 'target'
     */
    public PropertyStore getTargetPropertyStore() {
        if (targetPropertyStore == null) {
            throw new IllegalArgumentException("ff4j-core: Target for cache proxy has not been provided");
        }
        return targetPropertyStore;
    }

     
}
