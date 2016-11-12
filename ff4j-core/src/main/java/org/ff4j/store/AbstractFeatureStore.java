package org.ff4j.store;

import static org.ff4j.utils.FF4jUtils.assertHasLength;
import static org.ff4j.utils.FF4jUtils.assertNotNull;
import static org.ff4j.utils.JsonUtils.attributeAsJson;
import static org.ff4j.utils.JsonUtils.cacheJson;
import static org.ff4j.utils.JsonUtils.collectionAsJson;

/*
 * #%L
 * ff4j-core
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ff4j.conf.XmlParser;
import org.ff4j.exception.FeatureAlreadyExistException;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.exception.GroupNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.utils.FF4jUtils;
import org.ff4j.utils.Util;

/**
 * SuperClass for stores.
 *
 * @author Cedrick Lunven (@clunven)
 */
public abstract class AbstractFeatureStore implements FeatureStore {

    /**
     * Initialize store from XML Configuration File.
     *
     * @param xmlConfFile
     *      xml configuration file
     */
    public Map < String, Feature > importFeaturesFromXmlFile(String xmlConfFile) {
        assertHasLength("xml conf file", xmlConfFile);
        
        // Load as Inputstream
        InputStream xmlIS = getClass().getClassLoader().getResourceAsStream(xmlConfFile);
        assertNotNull(xmlIS);
        
        // Use the Feature Parser
        Map < String, Feature > features = new XmlParser().parseConfigurationFile(xmlIS).getFeatures();
        save(features.values());
        return features;
    }
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        /* 
         * In most of cases there is nothing to do. The feature and properties are createdat runtime.
         * But not always (JDBC, Mongo, Cassandra)... this is the reason why the dedicated store must 
         * override this method. It a default implementation (Pattern Adapter).
         */
        return;
    }
    
    /**
     * Import features from a set of feature.
     *
     * @param features
     */
    @Override
    public void save(Collection < Feature > features) {
        if (features != null) {
            features.stream().forEach(feature -> {
                if (exists(feature.getUid())) {
                    delete(feature.getUid());
                }
                create(feature);
            });
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends Feature> entities) {
        if (null != entities) {
            entities.forEach(this::delete);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Feature entity) {
        assertFeatureExist(entity.getUid());
        delete(entity.getUid());
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream<Feature> findAll(Iterable<String> candidates) {
        if (candidates == null) return null;
        List < Feature > targets  = new ArrayList<>();
        candidates.forEach(id -> targets.add(read(id)));
        return targets.stream();
    }
    
    /** {@inheritDoc} */
    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append(attributeAsJson("type", this.getClass().getCanonicalName()));
        sb.append(cacheJson(this));
        Set<String> myFeatures = FF4jUtils.setOf(findAll().map(Feature::getUid));
        sb.append(",\"numberOfFeatures\":" + myFeatures.size());
        sb.append(",\"features\":" + collectionAsJson(myFeatures));
        Set<String> groups = readAllGroups().collect(Collectors.toSet());
        sb.append(",\"numberOfGroups\":" + groups.size());
        sb.append(",\"groups\":" + collectionAsJson(groups));
        sb.append("}");
        return sb.toString();
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toJson();
    }
    
    /**
     * Validate feature uid.
     *
     * @param uid
     *      target uid
     */
    protected void assertFeatureExist(String uid) {
        Util.assertHasLength(uid);
        if (!exists(uid)) {
            throw new FeatureNotFoundException(uid);
        }
    }
    
    /**
     * Check that current feature does not exist.
     *
     * @param uid
     *      current feature identifier.s
     */
    protected void assertFeatureNotExist(String uid) {
        Util.assertHasLength(uid);
        if (exists(uid)) {
            throw new FeatureAlreadyExistException(uid);
        }
    }
    
    /**
     * Validate feature uid.
     *
     * @param uid
     *      target uid
     */
    protected void assertGroupExist(String groupName) {
        Util.assertHasLength(groupName);
        if (!existGroup(groupName)) {
            throw new GroupNotFoundException(groupName);
        }
    }
    
    /**
     * Validate feature uid.
     *
     * @param uid
     *      target uid
     */
    protected void assertFeatureNotNull(Feature feature) {
        if (feature == null) {
            throw new IllegalArgumentException("Feature cannot be null nor empty");
        }
    } 
    
    /** {@inheritDoc} */
    @Override
    public Feature read(String id) {
        assertFeatureExist(id);
        return findById(id).get();
    }
    
    /** {@inheritDoc} */
    @Override
    public void enable(String uid) {
        assertFeatureExist(uid);
        update(read(uid).toggleOn());
    }
    
    /** {@inheritDoc} */
    @Override
    public void disable(String uid) {
        assertFeatureExist(uid);
        update(read(uid).toggleOff());
    }
    
}
