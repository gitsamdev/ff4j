package org.ff4j.cache;

import java.util.stream.Stream;

import org.ff4j.feature.Feature;
import org.ff4j.store.FeatureStore;

/**
 * Access to {@link FeatureStore} could generate some overhead and decrease performances. This is the reason why cache is provided
 * though proxies.
 * 
 * As applications are distributed, the cache itself could be distributed. The default implement is
 * {@link InMemoryFeatureStoreCacheProxy} but other are provided to use distributed cache system as redis or memcached.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class FeatureStoreCacheProxy extends CacheProxy< String, Feature> implements FeatureStore {

    /**
     * Initialization through constructor.
     * 
     * @param store
     *            target store to retrieve features
     * @param cache
     *            cache manager to limit overhead of store
     */
    public FeatureStoreCacheProxy(FeatureStore fStore, CacheManager< String, Feature > cache) {
        this.cacheManager = cache;
        this.targetStore  = fStore;
        this.scheduler    = new FeatureStoreCachePollingScheduler(fStore, cache);
    }

    /** {@inheritDoc} */
    @Override
    public void toggleOn(String featureId) {
        // Reach target
        getTargetFeatureStore().toggleOn(featureId);
        // Modification => flush cache
        cacheManager.evict(featureId);
    }

    /** {@inheritDoc} */
    @Override
    public void toggleOff(String featureId) {
        // Reach target
        getTargetFeatureStore().toggleOff(featureId);
        // Cache Operations : As modification, flush cache for this
        cacheManager.evict(featureId);
    }   
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        // Create table for features but not only
        getTargetFeatureStore().createSchema();
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream <String> readAllGroups() {
        // Cannot be sure of whole cache - do not test any feature one-by-one : accessing FeatureStore
        return getTargetFeatureStore().readAllGroups();
    }
    
    /** {@inheritDoc} */
    @Override
    public void grantRoleOnFeature(String featureId, String roleName) {
        getTargetFeatureStore().grantRoleOnFeature(featureId, roleName);
        cacheManager.evict(featureId);
    }

    /** {@inheritDoc} */
    @Override
    public void removeRoleFromFeature(String featureId, String roleName) {
        getTargetFeatureStore().removeRoleFromFeature(featureId, roleName);
        cacheManager.evict(featureId);
    }

    /** {@inheritDoc} */
    @Override
    public void enableGroup(String groupName) {
        getTargetFeatureStore().enableGroup(groupName);
        // Cannot know wich feature to work with (exceptional event) : flush cache
        cacheManager.clear();
    }

    /** {@inheritDoc} */
    @Override
    public void disableGroup(String groupName) {
        getTargetFeatureStore().disableGroup(groupName);
        // Cannot know wich feature to work with (exceptional event) : flush cache
        cacheManager.clear();
    }

    /** {@inheritDoc} */
    @Override
    public boolean existGroup(String groupName) {
        // Cache cannot help you
        return getTargetFeatureStore().existGroup(groupName);
    }

    /** {@inheritDoc} */
    @Override
    public Stream <Feature> readGroup(String groupName) {
        // Cache cannot help you
        return getTargetFeatureStore().readGroup(groupName);
    }

    /** {@inheritDoc} */
    @Override
    public void addToGroup(String featureId, String groupName) {
        getTargetFeatureStore().addToGroup(featureId, groupName);
        cacheManager.evict(featureId);
    }

    /** {@inheritDoc} */
    @Override
    public void removeFromGroup(String featureId, String groupName) {
        getTargetFeatureStore().removeFromGroup(featureId, groupName);
        cacheManager.evict(featureId);
    }
    
    /**
     * Getter accessor for attribute 'target'.
     * 
     * @return current value of 'target'
     */
    public FeatureStore getTargetFeatureStore() {
        if (targetStore == null) {
            throw new IllegalArgumentException("ff4j-core: Target for cache proxy has not been provided");
        }
        return (FeatureStore) targetStore;
    }

}
