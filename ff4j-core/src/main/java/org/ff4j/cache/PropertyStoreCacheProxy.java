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
        if (getCacheManager().get(propertyName) == null) {
            return targetPropertyStore.exists(propertyName);
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public void create(Property<?> property) {
        getTargetPropertyStore().create(property);
        getCacheManager().put(property.getUid(), property);
    }

    /** {@inheritDoc} */
    @Override
    public Property<?> read(String name) {
        Optional <Property<?>> fp = getCacheManager().get(name);
        // not in cache but may has been created from now
        if (!fp.isPresent()) {
            fp = Optional.of(getTargetPropertyStore().read(name));
            getCacheManager().put(fp.get());
        }
        return fp.get();
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
    public void update(Property<?> propertyValue) {
        // Update the property
        getTargetPropertyStore().update(propertyValue);
        // Update the cache accordirly
        getCacheManager().evict(propertyValue.getUid());
        // Update the property in cache
        getCacheManager().put(propertyValue);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(String name) {
        // Access target store
        getTargetPropertyStore().delete(name);
        // even is not present, evict name failed
        getCacheManager().evict(name);
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
        getCacheManager().clear();
        getTargetPropertyStore().deleteAll();
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<Property<?>> properties) {
        getCacheManager().clear();
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
        getCacheManager().evict(entity.getUid());
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
        Optional <Property<?>> fp = getCacheManager().get(id);
        // not in cache but may has been created from now
        if (!fp.isPresent()) {
            fp = getTargetPropertyStore().findById(id);
            if (fp.isPresent()) {
                getCacheManager().put(fp.get());
            }
        }
        return fp;
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

     
}
