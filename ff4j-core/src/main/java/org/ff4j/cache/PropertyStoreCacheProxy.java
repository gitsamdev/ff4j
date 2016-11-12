package org.ff4j.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        // Also create tables for properties
        getTargetPropertyStore().createSchema();
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream < Property<?> > findAll() {
        return getTargetPropertyStore().findAll();
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(String propertyName) {
        // not in cache but maybe created from last access
        if (cacheManager.get(propertyName) == null) {
            return getTargetPropertyStore().exists(propertyName);
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void create(Property<?> property) {
        getTargetPropertyStore().create(property);
        getCacheManager().putProperty(property);
    }

    /** {@inheritDoc} */
    @Override
    public Property<?> read(String name) {
        Property<?> fp = getCacheManager().getProperty(name);
        // not in cache but may has been created from now
        if (null == fp) {
            fp = getTargetPropertyStore().read(name);
            getCacheManager().putProperty(fp);
        }
        return fp;
    }
    
    /** {@inheritDoc} */
    @Override
    public Property<?> read(String name, Property<?> defaultValue) {
        Property<?> fp = getCacheManager().getProperty(name);
        // Not in cache but may has been created from now
        // Or in cache but with different value that default
        if (null == fp) {
            fp = getTargetPropertyStore().read(name, defaultValue);
            getCacheManager().putProperty(fp);
        }
        return fp;
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
        getCacheManager().evictProperty(fp.getUid());
        // Add new value in the cache
        getCacheManager().putProperty(fp);
    }

    /** {@inheritDoc} */
    @Override
    public void update(Property<?> propertyValue) {
        // Update the property
        getTargetPropertyStore().update(propertyValue);
        // Update the cache accordirly
        getCacheManager().evictProperty(propertyValue.getUid());
        // Update the property in cache
        getCacheManager().putProperty(propertyValue);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(String name) {
        // Access target store
        getTargetPropertyStore().delete(name);
        // even is not present, evict name failed
        getCacheManager().evictProperty(name);
    }

    /** {@inheritDoc} */
    @Override
    public Stream<String> listPropertyNames() {
        return getTargetPropertyStore().listPropertyNames();
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        // Cache Operations : As modification, flush cache for this
        getCacheManager().clearProperties();
        getTargetPropertyStore().deleteAll();
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<Property<?>> properties) {
        getCacheManager().clearProperties();
        getTargetPropertyStore().save(properties);
    }
    
    /** {@inheritDoc} */
    @Override
    public long count() {
        return getTargetPropertyStore().count();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends Property<?>> entities) {
        if (null != entities) {
            entities.forEach(this::delete);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Property<?> entity) {
        // Access target store
        getTargetPropertyStore().delete(entity);
        // even is not present, evict name failed
        getCacheManager().evictProperty(entity.getUid());
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Property<?>> findAll(Iterable<String> ids) {
        List<Property<?>> listOfProperties = new ArrayList<>();
        ids.forEach(id -> listOfProperties.add(this.read(id)));
        return listOfProperties.stream();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Property<?>> findById(String id) {
        Property<?> fp = getCacheManager().getProperty(id);
        // not in cache but may has been created from now
        if (null == fp) {
            Optional <Property<?> > op = getTargetPropertyStore().findById(id);
            if (op.isPresent()) {
                fp = op.get();
                getCacheManager().putProperty(fp);
            }
        }
        return Optional.of(fp);
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

    /**
     * Getter accessor for attribute 'cacheManager'.
     * 
     * @return current value of 'cacheManager'
     */
    public FF4jCacheManager getCacheManager() {
        if (cacheManager == null) {
            throw new IllegalArgumentException("ff4j-core: CacheManager for cache proxy has not been provided but it's required");
        }
        return cacheManager;
    }

    /**
     * Setter accessor for attribute 'cacheManager'.
     * 
     * @param cacheManager
     *            new value for 'cacheManager '
     */
    public void setCacheManager(FF4jCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    // ------------ Cache related method --------------------

    /** {@inheritDoc} */
    public boolean isCached() {
        return true;
    }

    /** {@inheritDoc} */
    public String getCacheProvider() {
        if (cacheManager != null) {
            return cacheManager.getCacheProviderName();
        } else {
            return null;
        }
    } 

    /**
     * Setter accessor for attribute 'targetPropertyStore'.
     * 
     * @param targetPropertyStore
     *            new value for 'targetPropertyStore '
     */
    public void setTargetPropertyStore(PropertyStore targetPropertyStore) {
        this.targetPropertyStore = targetPropertyStore;
    }

    /**
     * Getter accessor for attribute 'store2CachePoller'.
     *
     * @return
     *       current value of 'store2CachePoller'
     */
    public FF4jCachePollingScheduler getStore2CachePoller() {
        return store2CachePoller;
    }

    /**
     * Setter accessor for attribute 'store2CachePoller'.
     * @param store2CachePoller
     * 		new value for 'store2CachePoller '
     */
    public void setStore2CachePoller(FF4jCachePollingScheduler store2CachePoller) {
        this.store2CachePoller = store2CachePoller;
    }

     
}
