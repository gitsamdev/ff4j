package org.ff4j.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
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

    /** Target feature store to be proxified to cache features. */
    private FeatureStore targetFeatureStore;

    /**
     * Initialization through constructor.
     * 
     * @param store
     *            target store to retrieve features
     * @param cache
     *            cache manager to limit overhead of store
     */
    public FeatureStoreCacheProxy(FeatureStore fStore, CacheManager< String, Feature > cache) {
        this.cacheManager        = cache;
        this.targetFeatureStore  = fStore;
        this.scheduler = new FeatureStoreCachePollingScheduler(fStore, cache);
    }
    

    /** {@inheritDoc} */
    @Override
    public void enable(String featureId) {
        // Reach target
        getTargetFeatureStore().enable(featureId);
        // Modification => flush cache
        cacheManager.evict(featureId);
    }

    /** {@inheritDoc} */
    @Override
    public void disable(String featureId) {
        // Reach target
        getTargetFeatureStore().disable(featureId);
        // Cache Operations : As modification, flush cache for this
        cacheManager.evict(featureId);
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(String featureId) {
        // not in cache but maybe created from last access
        if (cacheManager.get(featureId) == null) {
            return getTargetFeatureStore().exists(featureId);
        }
        return true;
    }
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        // Create table for features but not only
        getTargetFeatureStore().createSchema();
    }

    /** {@inheritDoc} */
    @Override
    public void create(Feature fp) {
        getTargetFeatureStore().create(fp);
        cacheManager.put(fp.getUid(), fp);
    }

    /** {@inheritDoc} */
    @Override
    public Feature read(String featureUid) {
        Optional<Feature> fp = cacheManager.get(featureUid);
        // not in cache but may has been created from now
        if (!fp.isPresent()) {
            Feature f = getTargetFeatureStore().read(featureUid);
            cacheManager.put(f.getUid(), f);
            fp = Optional.of(f);
        }
        return fp.get();
    }

    /** {@inheritDoc} */
    @Override
    public Stream <Feature> findAll() {
        // Cannot be sure of whole cache - do not test any feature one-by-one : accessing FeatureStore
        return getTargetFeatureStore().findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Stream <String> readAllGroups() {
        // Cannot be sure of whole cache - do not test any feature one-by-one : accessing FeatureStore
        return getTargetFeatureStore().readAllGroups();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(String featureId) {
        // Access target store
        getTargetFeatureStore().delete(featureId);
        // even is not present, evict won't failed
        cacheManager.evict(featureId);
    }

    /** {@inheritDoc} */
    @Override
    public void update(Feature fp) {
        getTargetFeatureStore().update(fp);
        cacheManager.evict(fp.getUid());
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

    /** {@inheritDoc} */
    @Override
    public long count() {
        // Cache cannot help you
        return getTargetFeatureStore().count();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends Feature> entities) {
        if (entities != null) {
            entities.forEach(this::delete);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Feature entity) {
        // Access target store
        getTargetFeatureStore().delete(entity.getUid());
        // even is not present, evict won't failed
        cacheManager.evict(entity.getUid());
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Feature> findAll(Iterable<String> ids) {
        List<Feature> listOfFeatures = new ArrayList<>();
        ids.forEach(id -> listOfFeatures.add(this.read(id)));
        return listOfFeatures.stream();
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Feature> findById(String id) {
        Optional <Feature> fp = cacheManager.get(id);
        // not in cache but may has been created from now
        if (!fp.isPresent()) {
            fp = getTargetFeatureStore().findById(id);
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
        getTargetFeatureStore().deleteAll();
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<Feature> features) {
        cacheManager.clear();
        getTargetFeatureStore().save(features);
    }
    
    /**
     * Getter accessor for attribute 'target'.
     * 
     * @return current value of 'target'
     */
    public FeatureStore getTargetFeatureStore() {
        if (targetFeatureStore == null) {
            throw new IllegalArgumentException("ff4j-core: Target for cache proxy has not been provided");
        }
        return targetFeatureStore;
    }

}
