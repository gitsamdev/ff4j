package org.ff4j.cache;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.ff4j.FF4jBaseObject;
import org.ff4j.store.FF4jRepository;

/**
 * Cache abstraction for ff4j.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 * @author Andre Blaszczyk (@AndrBLASZCZYK)
 *
 * @param <K>
 *      cache key
 * @param <V>
 *      cache value
 */
public class CacheProxy < K extends Serializable, V extends FF4jBaseObject<?> > implements FF4jRepository< String, V > {
    
    /** cache manager. */
    protected CacheManager< String , V > cacheManager;
    
    /** Daemon to fetch data from target store to cache on a fixed delay basis. */
    protected CachePollingScheduler< V > scheduler = null;
    
    /** Target store. */
    protected FF4jRepository< String, V > targetStore;
    
    /**
     * Start the polling of target store is required.
     */
    public void startPolling(long delay) {
        if (scheduler == null) {
            throw new IllegalStateException("The poller has not been initialize, please check");
        }
        scheduler.start(delay);
    }
    
    /**
     * Stop the polling of target store is required.
     */
    public void stopPolling() {
        if (scheduler == null) {
            throw new IllegalStateException("The poller has not been initialize, please check");
        }
        scheduler.stop();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean exists(String uid) {
        // not in cache but maybe created from last access
        if (cacheManager.get(uid) == null) {
            return getTargetStore().exists(uid);
        }
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public void create(V fp) {
        getTargetStore().create(fp);
        cacheManager.put(fp.getUid(), fp);
    }

    /** {@inheritDoc} */
    @Override
    public V read(String uid) {
        Optional<V> fp = cacheManager.get(uid);
        // not in cache but may has been created from now
        if (!fp.isPresent()) {
            V f = getTargetStore().read(uid);
            cacheManager.put(f.getUid(), f);
            fp = Optional.of(f);
        }
        return fp.get();
    }

    /** {@inheritDoc} */
    @Override
    public Stream <V> findAll() {
        // Cannot be sure of whole cache - do not test any feature one-by-one : accessing FeatureStore
        return getTargetStore().findAll();
    }    

    /** {@inheritDoc} */
    @Override
    public void delete(String uid) {
        // Access target store
        getTargetStore().delete(uid);
        // even is not present, evict won't failed
        cacheManager.evict(uid);
    }

    /** {@inheritDoc} */
    @Override
    public void update(V fp) {
        getTargetStore().update(fp);
        cacheManager.evict(fp.getUid());
    }    

    /** {@inheritDoc} */
    @Override
    public long count() {
        // Cache cannot help you
        return getTargetStore().count();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends V> entities) {
        if (entities != null) {
            entities.forEach(this::delete);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(V entity) {
        // Access target store
        getTargetStore().delete(entity.getUid());
        // even is not present, evict won't failed
        cacheManager.evict(entity.getUid());
    }

    /** {@inheritDoc} */
    @Override
    public Stream<V> findAll(Iterable<String> ids) {
        List<V> listOfFeatures = new ArrayList<>();
        ids.forEach(id -> listOfFeatures.add(this.read(id)));
        return listOfFeatures.stream();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<V> findById(String id) {
        Optional <V> fp = cacheManager.get(id);
        // not in cache but may has been created from now
        if (!fp.isPresent()) {
            fp = getTargetStore().findById(id);
            if (fp.isPresent()) {
                cacheManager.put(fp.get().getUid(), fp.get());
            }
        }
        return fp;
    }    
    
    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        // Cache Operations : As modification, flush cache for this
        cacheManager.clear();
        getTargetStore().deleteAll();
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<V> features) {
        cacheManager.clear();
        getTargetStore().save(features);
    }
    
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
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        /* Most of the time there is nothing to do. The feature and properties are createdat runtime.
         * But not always (JDBC, Mongo, Cassandra)... this is the reason why the dedicated store must 
         * override this method. It a default implementation (Pattern Adapter).
         */
        return;
    }
    
    /**
     * Getter accessor for attribute 'cacheManager'.
     * 
     * @return current value of 'cacheManager'
     */
    public CacheManager< String, V> getCacheManager() {
        if (cacheManager == null) {
            throw new IllegalArgumentException("CacheManager for cache proxy has not been provided but it's required");
        }
        return cacheManager;
    }

    /**
     * Getter accessor for attribute 'targetStore'.
     *
     * @return
     *       current value of 'targetStore'
     */
    public FF4jRepository<String, V> getTargetStore() {
        return targetStore;
    }

}
