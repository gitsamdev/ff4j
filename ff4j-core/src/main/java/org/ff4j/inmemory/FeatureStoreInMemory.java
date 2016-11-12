package org.ff4j.inmemory;

import static org.ff4j.utils.Util.assertHasLength;

/*
 * #%L ff4j-core $Id:$ $HeadURL:$ %% Copyright (C) 2013 Ff4J %% Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. #L%
 */

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ff4j.conf.XmlParser;
import org.ff4j.feature.Feature;
import org.ff4j.store.AbstractFeatureStore;
import org.ff4j.utils.FF4jUtils;

/**
 * Storing states of feature inmemory with initial values. Could be used mostly for testing purpose.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class FeatureStoreInMemory extends AbstractFeatureStore {

    /** XML File where features are load. */
    private String fileName = null;

    /** InMemory Feature Map */
    private Map<String, Feature> featuresMap = new LinkedHashMap<String, Feature>();

    /** Group structure for features. */
    private Map<String, Set<String>> featureGroups = new HashMap<String, Set<String>>();

    /** Default constructor. */
    public FeatureStoreInMemory() {}

    /**
     * Constructor with configuration fileName.
     * 
     * @param fileName
     *            fileName present in classPath or on fileSystem.
     */
    public FeatureStoreInMemory(String fileName) {
        assertHasLength(fileName);
        createSchema();
        loadConfFile(fileName);
    }

    /**
     * Constructor with inputstream fileName.
     * 
     * @param fileName
     *            fileName present in classPath or on fileSystem.
     */
    public FeatureStoreInMemory(InputStream xmlIN) {
        createSchema();
        loadConf(xmlIN);
    }

    /**
     * Constructor with full set of feature.
     * 
     * @param maps
     */
    public FeatureStoreInMemory(Collection<Feature> features) {
        createSchema();
        if (null != features) {
            this.featuresMap = features.stream().collect(
                    Collectors.toMap(Feature::getUid, Function.identity()));
            buildGroupsFromFeatures();
        }
    }
    
    // --- FF4jRepository Methods ---
    
    /** {@inheritDoc} */
    @Override
    public boolean exists(String uid) {
        assertHasLength(uid);
        return featuresMap.containsKey(uid);
    }
    
    /** {@inheritDoc} */
    @Override
    public Optional < Feature > findById(String uid) {
        return Optional.ofNullable(featuresMap.get(uid));
    }
    
    /** {@inheritDoc} */    
    @Override
    public void create(Feature fp) {
        assertFeatureNotNull(fp);
        assertFeatureNotExist(fp.getUid());
        updateFeature(fp);
    }
    
    /** {@inheritDoc} */
    @Override
    public void update(Feature fp) {
        assertFeatureNotNull(fp);
        Feature fpExist = read(fp.getUid());
        
        // Checking new roles
        Set<String> toBeAdded = new HashSet<String>();
        fp.getPermissions().ifPresent(perms -> toBeAdded.addAll(perms));
        fpExist.getPermissions().ifPresent(perms -> toBeAdded.removeAll(perms));
        toBeAdded.stream().forEach(p -> grantRoleOnFeature(fpExist.getUid(), p));
        updateFeature(fp);
    }
    
    /** {@inheritDoc} */
    @Override
    public void delete(String uid) {
        assertFeatureExist(uid);
        featuresMap.remove(uid);
        buildGroupsFromFeatures();
    }
    
    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
       featuresMap.clear();
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream <Feature> findAll() {
        return featuresMap.values().stream();
    }
    
    /** {@inheritDoc} */
    @Override
    public long count() {
        return findAll().count();
    }    
    
    // --- FeatureStore Methods ---
    
    /** {@inheritDoc} */
    @Override
    public void grantRoleOnFeature(String uid, String roleName) {
        assertFeatureExist(uid);
        assertHasLength(roleName);
        featuresMap.get(uid).getPermissions().get().add(roleName);
    }

    /** {@inheritDoc} */
    @Override
    public void removeRoleFromFeature(String uid, String roleName) {
        assertFeatureExist(uid);
        assertHasLength(roleName);
        featuresMap.get(uid).getPermissions().get().remove(roleName);
    }    
    
    /** {@inheritDoc} */
    @Override
    public boolean existGroup(String groupName) {
        assertHasLength(groupName);
        return featureGroups.containsKey(groupName);
    }

    /** {@inheritDoc} */
    @Override
    public void enableGroup(String groupName) {
        assertGroupExist(groupName);
        for (String feat : featureGroups.get(groupName)) {
            this.enable(feat);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void disableGroup(String groupName) {
        assertGroupExist(groupName);
        for (String feat : featureGroups.get(groupName)) {
            this.disable(feat);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream<Feature> readGroup(String groupName) {
        assertGroupExist(groupName);
        return featureGroups.get(groupName).stream()
            .map(featureName -> findById(featureName))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet())
            .stream();
    }

    /** {@inheritDoc} */
    @Override
    public Stream<String> readAllGroups() {
        Set<String> groups = new HashSet<String>();
        groups.addAll(featureGroups.keySet());
        groups.remove(null);
        groups.remove("");
        return groups.stream();
    }

    /** {@inheritDoc} */
    @Override
    public void addToGroup(String uid, String groupName) {
        assertHasLength(uid);
        assertHasLength(groupName);        
        Feature feat = findById(uid).get();
        feat.setGroup(groupName);
        update(feat);
    }

    /** {@inheritDoc} */
    @Override
    public void removeFromGroup(String uid, String groupName) {
        assertFeatureExist(uid);
        assertGroupExist(groupName);
        Feature feat = findById(uid).get();
        feat.setGroup("");
        update(feat);
    }
    
    // --- Utility Methods ---
    
    /**
     * Load configuration through FF4J.vml file.
     * 
     * @param conf
     *            xml filename
     */
    private void loadConfFile(String conf) {
        this.fileName = conf;
        loadConf(getClass().getClassLoader().getResourceAsStream(conf));
    }

    /**
     * Load configuration through FF4J.vml file.
     * 
     * @param conf
     *            xml filename
     */
    private void loadConf(InputStream xmlIN) {
        if (xmlIN == null) {
            throw new IllegalArgumentException("Cannot parse feature stream");
        }
        this.featuresMap = new XmlParser().parseConfigurationFile(xmlIN).getFeatures();
        buildGroupsFromFeatures();
    }

    /**
     * Group is an attribute of the feature and the group structure is rebuild from it.
     */
    private void buildGroupsFromFeatures() {
        
        // Create groups
        featureGroups = featuresMap.values().stream()
            .filter(item -> item.getGroup().isPresent())
            .collect(Collectors.< Feature, String, Set<String>>toMap(
                    f -> f.getGroup().get(), 
                    f -> FF4jUtils.setOf(f.getUid()),
                    //Merged but could we add ?
                    (uid1, uid2) -> { return uid1; }));
        
        // Populate groups
        featuresMap.values().stream()
                .filter(item -> item.getGroup().isPresent())
                .forEach(feature -> featureGroups.get(
                         feature.getGroup().get()).add(feature.getUid()));
    }

    /**
     * Unique update point to force group construction.
     * 
     * @param fp
     *            Target feature to update
     */
    private void updateFeature(Feature fp) {
        featuresMap.put(fp.getUid(), fp);
        buildGroupsFromFeatures();
    }

    /** {@inheritDoc} */
    @Override
    public String toJson() {
        String json = super.toJson();
        // Remove last } to enrich the json document
        json = json.substring(0, json.length() - 1) + ",\"xmlInputFile\":";
        // No filename inputstream, set to true)
        if (null == fileName) {
            json += "null";
        } else {
            json += "\"" + this.fileName + "\"";
        }
        json += "}";
        return json;
    }

    /**
     * Setter accessor for attribute 'locations'.
     * 
     * @param locations
     *            new value for 'locations '
     */
    public void setLocation(String locations) {
        loadConfFile(locations);
    }

    /**
     * Getter accessor for attribute 'fileName'.
     * 
     * @return current value of 'fileName'
     */
    public String getFileName() {
        return fileName;
    }
    
}
