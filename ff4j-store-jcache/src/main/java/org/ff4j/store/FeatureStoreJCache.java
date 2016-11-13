package org.ff4j.store;

/*
 * #%L
 * ff4j-store-redis
 * %%
 * Copyright (C) 2013 - 2014 Ff4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.ff4j.cache.FF4jJCacheManager;
import org.ff4j.exception.FeatureAlreadyExistException;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.exception.GroupNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.utils.Util;

/**
 * Generic {@link FeatureStore} to persist properties in a JCache (JSR107) compliant storage.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class FeatureStoreJCache extends AbstractFeatureStore {
    
    /** Cache Manager. */ 
    private FF4jJCacheManager cacheManager;
    
    /**
     * Initialization with cache manager.
     *
     * @param cacheManager
     */
    public FeatureStoreJCache(FF4jJCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }
    
    /**
     * Default Constructor.
     */
    public FeatureStoreJCache(String cachingProviderClassName) {
       this(new FF4jJCacheManager(cachingProviderClassName));
    } 
    
    /** {@inheritDoc} */
    @Override
    public boolean exist(String uid) {
        Util.assertParamHasLength(uid, "Feature identifier");
        return getCacheManager().getFeature(uid) != null;
    }
    
    /** {@inheritDoc} */
    @Override
    public Feature findById(String uid) {
        if (!exist(uid)) {
            throw new FeatureNotFoundException(uid);
        }
        return getCacheManager().getFeature(uid);
    }
    
    /** {@inheritDoc} */
    @Override
    public void update(Feature fp) {
        if (fp == null) {
            throw new IllegalArgumentException("Feature cannot be null");
        }
        if (!exist(fp.getUid())) {
            throw new FeatureNotFoundException(fp.getUid());
        }
        getCacheManager().putFeature(fp); 
    }
    
    /** {@inheritDoc} */
    @Override
    public void toggleOn(String uid) {
        // Read from redis, feature not found if no present
        Feature f = findById(uid);
        // Update within Object
        f.toggleOn();
        // Serialization and update key, update TTL
        update(f);
    }

    /** {@inheritDoc} */
    @Override
    public void toggleOff(String uid) {
        // Read from redis, feature not found if no present
        Feature f = findById(uid);
        // Update within Object
        f.toggleOff();
        // Serialization and update key, update TTL
        update(f);
    }

    /** {@inheritDoc} */
    @Override
    public void create(Feature fp) {
        if (fp == null) {
            throw new IllegalArgumentException("Feature cannot be null nor empty");
        }
        if (exist(fp.getUid())) {
            throw new FeatureAlreadyExistException(fp.getUid());
        }
        getCacheManager().putFeature(fp); 
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, Feature> findAll() {
        Map<String, Feature> myMap = new HashMap<>();
        getCacheManager().getFeaturesCache().forEach(e->myMap.put(e.getKey(), e.getValue()));
        return myMap;
    }

    /** {@inheritDoc} */
    @Override
    public void delete(String fpId) {
        if (!exist(fpId)) {
            throw new FeatureNotFoundException(fpId);
        }
        getCacheManager().evictFeature(fpId);
    }    

    /** {@inheritDoc} */
    @Override
    public void grantRoleOnFeature(String flipId, String roleName) {
        Util.assertParamHasLength(roleName, "roleName (#2)");
        // retrieve
        Feature f = findById(flipId);
        // modify
        f.addPermission(roleName);
        // persist modification
        update(f);
    }

    /** {@inheritDoc} */
    @Override
    public void removeRoleFromFeature(String flipId, String roleName) {
        Util.assertParamHasLength(roleName, "roleName (#2)");
        // retrieve
        Feature f = findById(flipId);
        f.removePermission(roleName);
        // persist modification
        update(f);
    }
    
    /** {@inheritDoc} */
    @Override
    public Map<String, Feature> readGroup(String groupName) {
        Util.assertParamHasLength(groupName, "groupName");
        Map < String, Feature > features = findAll();
        Map < String, Feature > group = new HashMap<>();
        for (Map.Entry<String,Feature> uid : features.entrySet()) {
            if (groupName.equals(uid.getValue().getGroup())) {
                group.put(uid.getKey(), uid.getValue());
            }
        }
        if (group.isEmpty()) {
            throw new GroupNotFoundException(groupName);
        }
        return group;
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean existGroup(String groupName) {
        Util.assertParamHasLength(groupName, "groupName");
        Map < String, Feature > features = findAll();
        Map < String, Feature > group = new HashMap<>();
        for (Map.Entry<String,Feature> uid : features.entrySet()) {
            if (groupName.equals(uid.getValue().getGroup())) {
                group.put(uid.getKey(), uid.getValue());
            }
        }
        return !group.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public void enableGroup(String groupName) {
        Map < String, Feature > features = readGroup(groupName);
        for (Map.Entry<String,Feature> uid : features.entrySet()) {
            uid.getValue().toggleOn();
            update(uid.getValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void disableGroup(String groupName) {
        Map < String, Feature > features = readGroup(groupName);
        for (Map.Entry<String,Feature> uid : features.entrySet()) {
            uid.getValue().toggleOff();
            update(uid.getValue());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addToGroup(String featureId, String groupName) {
        Util.assertParamHasLength(groupName, "groupName (#2)");
        // retrieve
        Feature f = findById(featureId);
        f.setGroup(groupName);
        // persist modification
        update(f);
    }

    /** {@inheritDoc} */
    @Override
    public void removeFromGroup(String featureId, String groupName) {
        Util.assertParamHasLength(groupName, "groupName (#2)");
        if (!existGroup(groupName)) {
            throw new GroupNotFoundException(groupName);
        }
        // retrieve
        Feature f = findById(featureId);
        f.setGroup(null);
        // persist modification
        update(f);
    }

    /** {@inheritDoc} */
    @Override
    public Set<String> readAllGroups() {
        return findAll().values().stream().map(f->f.getGroup())
            .filter(Optional::isPresent).map(Optional::get)
            .collect(Collectors.toSet());
    }
    
    /** {@inheritDoc} */
    @Override
    public void clear() {
        getCacheManager().getFeaturesCache().removeAll();
    }
    
    /**
     * Getter accessor for attribute 'cacheManager'.
     *
     * @return
     *       current value of 'cacheManager'
     */
    public FF4jJCacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * Setter accessor for attribute 'cacheManager'.
     * @param cacheManager
     * 		new value for 'cacheManager '
     */
    public void setCacheManager(FF4jJCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }   
}
