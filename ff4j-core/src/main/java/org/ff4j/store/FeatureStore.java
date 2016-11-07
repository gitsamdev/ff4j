package org.ff4j.store;

import java.util.Collection;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 Ff4J
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

import java.util.Map;
import java.util.Set;

import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.exception.GroupNotFoundException;
import org.ff4j.feature.Feature;

/**
 * Repository to persist {@link Feature}(s)
 * 
 * @author Cedrick Lunven (@clunven)
 */
public interface FeatureStore {

    /**
     * Toggle ON a feature by its identifier.
     * 
     * @param uid
     *            unique feature identifier
     */
    void enable(String uid);

    /**
     * Toggle off a feature by its identifier.
     * 
     * @param uid
     *            unique feature identifier
     */
    void disable(String uid);

    /**
     * Check if a feature exists or not
     * 
     * @param uid
     *            unique feature identifier
     */
    boolean exist(String uid);

    /**
     * Create a new feature in the target storage.
     * 
     * @param fp
     *            create roles
     */
    void create(Feature feature);

    /**
     * Read a feature by id, get a {@link FeatureNotFoundException} eventually.
     * 
     * @param uid
     *            unique feature identifier
     */
    Feature read(String uid);

    /**
     * Retrieve all features available in target storage.
     * 
     * @return all features
     */
    Map<String, Feature> readAll();

    /**
     * Delete a feature by its id, get a {@link FeatureNotFoundException} eventually.
     * 
     * @param uid
     *            unique feature identifier
     */
    void delete(String uid);

    /**
     * Update a feature in the storage.
     * 
     * @param feature
     *           feature to be updated
     */
    void update(Feature feature);

    /**
     * Grant role on target feature.
     * 
     * @param uid
     *      feature unique identifier
     * @param roleName
     *      current role name
     */
    void grantRoleOnFeature(String uid, String roleName);

    /**
     * Remove role on target feature.
     *
       * @param uid
     *      feature unique identifier
     * @param roleName
     *      current role name
     */
    void removeRoleFromFeature(String uid, String roleName);

    /**
     * Enable all features related to the parameter group
     * 
     * @param groupName
     *            target group name
     */
    void enableGroup(String groupName);

    /**
     * Disable all features related to the parameter group
     * 
     * @param groupName
     *            target group name
     */
    void disableGroup(String groupName);

    /**
     * Check if current group exist or not.
     * 
     * @param groupName
     *            target group name
     */
    boolean existGroup(String groupName);

    /**
     * Read all features within target group.
     * 
     * @param groupName
     *            target group name
     * @return return all feature from group or groupnotfoundException if does not exist
     * 
     * @throws GroupNotFoundException
     *              if group does not exist
     */
    Map<String, Feature> readGroup(String groupName);
    
    /**
     * Add target {@link Feature} to target group.
     * 
     * @param uid
     *            target feature identifier
     * @param groupName
     *            target groupName
     */
    void addToGroup(String uid, String groupName);
    
    /**
     * Remove target {@link Feature} from group.
     * 
     * @param uid
     *            target feature identifier
     * @param groupName
     *            target groupName
     */
    void removeFromGroup(String uid, String groupName);
    
    /**
     * Return a set of existing groups.
     * 
     * @return set of group in the store
     */
    Set<String> readAllGroups();
    
    /**
     * Empty the store
     */
    void clear();
    
    /**
     * Import feature into store.
     *
     * @param features
     *      list of features.s
     */
    void importFeatures(Collection < Feature > features);
    
    /**
     * Initialize the target database schema by creating expected structures.
     * 
     * <li> TABLE, INDEX will be created for JDBC, but also COLLECTION and INDEXS for MongoDb, or COLUMN FAMILY for Cassandra.
     * <li> The structures will be created only if they don't exist.
     * <li> In some cases, there is nothing todo (Ehcache, Redis, InMemory), the method won't failed but do nothing (it does not clear the DB) 
     * 
     * @since 1.6
     */
    void createSchema();
    
}
