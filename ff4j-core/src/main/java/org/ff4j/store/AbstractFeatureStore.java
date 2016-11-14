package org.ff4j.store;

import static org.ff4j.utils.JsonUtils.attributeAsJson;
import static org.ff4j.utils.JsonUtils.cacheJson;
import static org.ff4j.utils.JsonUtils.collectionAsJson;
import static org.ff4j.utils.Util.assertHasLength;
import static org.ff4j.utils.Util.assertNotNull;
import static org.ff4j.utils.Util.setOf;

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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ff4j.conf.XmlParser;
import org.ff4j.exception.FeatureAlreadyExistException;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.exception.GroupNotFoundException;
import org.ff4j.exception.ItemAlreadyExistException;
import org.ff4j.exception.ItemNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.utils.Util;

/**
 * SuperClass for stores.
 *
 * @author Cedrick Lunven (@clunven)
 */
public abstract class AbstractFeatureStore extends AbstractFF4jRepository<Feature> implements FeatureStore {

    /** serialVersionUID. */
    private static final long serialVersionUID = -7450698535116107530L;

    /**
     * Initialize store from XML Configuration File.
     *
     * @param xmlConfFile
     *      xml configuration file
     */
    protected Stream < Feature > importFeaturesFromXmlFile(String xmlConfFile) {
        // Load as Inputstream
        assertHasLength(xmlConfFile);
        InputStream xmlIS = getClass().getClassLoader().getResourceAsStream(xmlConfFile);
        assertNotNull(xmlIS);
        // Use the Feature Parser
        Map < String, Feature > features = new XmlParser().parseConfigurationFile(xmlIS).getFeatures();
        save(features.values());
        return features.values().stream();
    }
    
    /** {@inheritDoc} */
    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append(attributeAsJson("type", this.getClass().getCanonicalName()));
        sb.append(cacheJson(this));
        Set<String> myFeatures = setOf(findAll().map(Feature::getUid));
        sb.append(attributeAsJson("numberOfFeatures", myFeatures.size()));
        sb.append(",\"features\":" + collectionAsJson(myFeatures));
        Set<String> groups = readAllGroups().collect(Collectors.toSet());
        sb.append(attributeAsJson("numberOfGroups", groups.size()));
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
        try {
            assertItemExist(uid);
        } catch(ItemNotFoundException infEx) {
            throw new FeatureNotFoundException(uid, infEx);
        }
    }
    
    /**
     * Check that current feature does not exist.
     *
     * @param uid
     *      current feature identifier.s
     */
    protected void assertFeatureNotExist(String uid) {
        try {
            assertItemNotExist(uid);
        } catch(ItemAlreadyExistException infEx) {
            throw new FeatureAlreadyExistException(uid, infEx);
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
    public void toggleOn(String uid) {
        assertFeatureExist(uid);
        update(read(uid).toggleOn());
    }
    
    /** {@inheritDoc} */
    @Override
    public void toggleOff(String uid) {
        assertFeatureExist(uid);
        update(read(uid).toggleOff());
    }
    
}
