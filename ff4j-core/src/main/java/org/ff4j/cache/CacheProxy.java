package org.ff4j.cache;

import java.io.Serializable;

import org.ff4j.FF4jBaseObject;

/**
 * Class to TODO
 *
 * @author Cedrick LUNVEN  (@clunven)
 * @author Andre Blaszczyk (@AndrBLASZCZYK)
 *
 * @param <K>
 *      cache key
 * @param <V>
 *      cache value
 */
public class CacheProxy < K extends Serializable, V extends FF4jBaseObject<?> > {
    
    /** cache manager. */
    protected CacheManager< K, V > cacheManager;
    
    /** Daemon to fetch data from target store to cache on a fixed delay basis. */
    protected CachePollingScheduler< V > scheduler = null;
    
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
     * Getter accessor for attribute 'cacheManager'.
     * 
     * @return current value of 'cacheManager'
     */
    public CacheManager< K, V> getCacheManager() {
        if (cacheManager == null) {
            throw new IllegalArgumentException("CacheManager for cache proxy has not been provided but it's required");
        }
        return cacheManager;
    }

}
