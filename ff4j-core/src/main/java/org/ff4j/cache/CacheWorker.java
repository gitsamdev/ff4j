package org.ff4j.cache;

import java.io.Serializable;

import org.ff4j.FF4jBaseObject;
import org.ff4j.store.FF4jRepository;

/**
 * Worker invoke on fixed delay basis.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <K>
 *      cache key
 * @param <V>
 *      cache value
 */
public class CacheWorker< V extends FF4jBaseObject<?> > implements Runnable, Serializable {
    
    /** serialVersionUID. */
    private static final long serialVersionUID = 7679893286791023790L;
    
    /** cache manager to hold infos. */
    protected CacheManager< String, V > cacheManager;
    
    /** target ff4j repository (featureStore, propertyStore...). */
    protected FF4jRepository < String, V > ff4jRepository;
    
    /**
     * Default constructor.
     *
     * @param repo
     *      current repo
     * @param cacheManager
     *      current cache manager
     */
    public CacheWorker(FF4jRepository < String, V > repo, CacheManager< String, V > cacheManager) {
        this.cacheManager   = cacheManager;
        this.ff4jRepository = repo; 
    }

    /** {@inheritDoc} */
    @Override
    public void run() {
        if (ff4jRepository != null) {
            cacheManager.clear();
            ff4jRepository.findAll().forEach(f -> cacheManager.put(f.getUid(), f));
        }
    }
    
}
