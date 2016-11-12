package org.ff4j.feature;

import static org.ff4j.utils.JsonUtils.attributeAsJson;
import static org.ff4j.utils.JsonUtils.collectionAsJson;
import static org.ff4j.utils.JsonUtils.customPropertiesAsJson;
import static org.ff4j.utils.JsonUtils.flippingStrategyAsJson;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2016 FF4J
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



import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.ff4j.FF4jBaseObject;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyFactory;
import org.ff4j.utils.FF4jUtils;
import org.ff4j.utils.Util;

/**
 * Represents a feature flag identified by an unique identifier.
 *
 * <p>
 * Features Flags or Features Toggle have been introduced by Martin Fowler for continuous delivery perspective. It consists of
 * enable/disable some functionalities at runtime.
 *
 * <p>
 * <b>SecurityManagement :</b> Even a feature is enabled, you can limit its usage to a group of users (for instance BETA Tester)
 * before wide over all your users.
 * </p>
 *
 * @author Cedrick Lunven (@clunven)
 */
public class Feature extends FF4jBaseObject < Feature > {

    /** serial of the class. */
    private static final long serialVersionUID = -1345806526991179050L;
   
    /** State to decide to toggleOn or not. */
    private boolean enable = false;
    
    /** Feature could be grouped to enable/disable the whole group. */
    private Optional < String > group = Optional.empty();
    
    /** if not empty and @see {@link org.ff4j.security.AuthorizationsManager} provided, limit usage to this roles. */
    private Optional< Set <String> > permissions = Optional.empty();

    /** Custom behaviour to define if feature if enable or not e.g. A/B Testing capabilities. */
    private Optional<FlippingStrategy> flippingStrategy = Optional.empty();
    
    /** Add you own attributes to a feature. */
    private Optional<Map < String, Property<?> >> customProperties = Optional.empty();

    /**
     * Initialize {@link Feature} with id;
     * @param uid
     */
    public Feature(final String uid) {
        super(uid);
        setCreationDate(LocalDateTime.now());
        setLastModified(getCreationDate().get());
    }
    
    public Feature(final Feature f) {
        this(f.getUid(), f);
    }
    
    /**
     * Creatie new feature from existing one.
     * 
     * @param uid
     *      new uid (could be the same)
     * @param f
     */
    public Feature(final String uid, final Feature f) {
        super(uid);
        this.enable = f.isEnable();
        
        // Base Object
        f.getOwner().ifPresent(o-> this.owner = Optional.of(o));
        f.getDescription().ifPresent(d-> this.description = Optional.of(d));
        f.getCreationDate().ifPresent(c-> this.creationDate = Optional.of(c));
        f.getLastModifiedDate().ifPresent(c-> this.lastModifiedDate = Optional.of(c));
        
        // Properties Features
        f.getGroup().ifPresent(g-> this.group = Optional.of(g));
        f.getPermissions().ifPresent(g-> this.permissions = Optional.of(g));
        f.getFlippingStrategy().ifPresent(fs -> 
            this.flippingStrategy = Optional.of(FlippingStrategy.instanciate(uid, 
                    fs.getClass().getName(), 
                    fs.getInitParams())));
        f.getCustomProperties().ifPresent(cp ->
            this.customProperties = Optional.of(cp.entrySet().stream().collect(
                    // 1st type Witness ever 0_0
                    Collectors.<Map.Entry<String, Property<?>>, String, Property<?>>toMap(Map.Entry::getKey, entry -> {
                        Property<?> val = entry.getValue();
                        Property<?> targetProp = PropertyFactory.createProperty(val.getUid(), val.getType(), val.asString());
                        val.getDescription().ifPresent(targetProp::setDescription);
                        val.getFixedValues().ifPresent(v -> v.stream().forEach(t -> targetProp.add2FixedValueFromString(t.toString())));
                        return targetProp;
                    }))));
    }
    
    public Feature setGroup(String groupName) {
        this.group = Optional.ofNullable(groupName);
        return this;
    }
    
    public Feature setFlippingStrategy(FlippingStrategy flipStrategy) {
        this.flippingStrategy = Optional.ofNullable(flipStrategy);
        return this;
    }
    
    public Feature setPermissions(String... perms) {
        return setPermissions(FF4jUtils.setOf(perms));
    }
    
    public Feature setPermissions(Set<String> perms) {
        permissions = Optional.ofNullable(perms);
        return this;
    }
    
    public Feature setCustomProperties(Map<String, Property<?>> custom) {
        customProperties = Optional.ofNullable(custom);
        return this;
    }
    
    public Feature setCustomProperties(Property<?>... properties) {
        if (properties == null) return this;
        return setCustomProperties(Arrays.stream(properties).collect(
                Collectors.toMap(Property::getUid, Function.identity())));
    }
    
    public Feature addCustomProperty(Property<?> property) {
        return addCustomProperties(property);
    }
    
    public Feature addCustomProperties(Property<?>... properties) {
        if (properties != null) {
            Map < String, Property<?>> mapOfProperties = Arrays.stream(properties)
                    .collect(Collectors.toMap(Property::getUid, Function.identity()));

            customProperties.map(Map::size);
            if (customProperties.isPresent()) {
                customProperties.get().putAll(mapOfProperties);
            } else {
                customProperties = Optional.of(mapOfProperties);
            }
        }
        return this;
    }
    
    public Feature addPermission(String permission) {
        return addPermissions(permission);
    }
    
    public Feature removePermission(String permission) {
        return removePermissions(permission);
    }
    
    public Feature removePermissions(String... perms) {
        if (perms != null && permissions.isPresent()) {
            permissions.get().removeAll(FF4jUtils.setOf(perms));
        }
        return this;
    }
    
    public Feature addPermissions(String... perms) {
        if (perms != null) {
            Set<String> setPermission = FF4jUtils.setOf(perms);
            if (permissions.isPresent()) {
                permissions.get().addAll(setPermission);
            } else {
                permissions = Optional.of(setPermission);
            }
        }
        return this;
    }
    
    public Feature setEnable(boolean status) {
        this.enable = status;
        return this;
    }
    
    public Feature toggleOn() {
        return setEnable(true);
    }
    
    public Feature toggleOff() {
        return setEnable(false);
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toJson();
    }

    /**
     * Convert Feature to JSON.
     * 
     * @return target json
     */
    public String toJson() {
        StringBuilder json = new StringBuilder("{");
        json.append(super.baseJson());
        json.append(attributeAsJson("enable", enable));
        group.ifPresent(g -> attributeAsJson("group", g));
        permissions.ifPresent(perm -> 
                json.append(",\"permissions\": " + collectionAsJson(perm)));
        flippingStrategy.ifPresent(fs ->
                json.append(",\"flippingStrategy\":" + flippingStrategyAsJson(fs)));
        customProperties.ifPresent(cp ->
                json.append(",\"customProperties\":" + customPropertiesAsJson(cp)));
        json.append("}");
        return json.toString();
    }
    
    public static Feature fromJson(String jsonString) {
        return null;
    }

    /**
     * Getter accessor for attribute 'enable'.
     *
     * @return
     *       current value of 'enable'
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Getter accessor for attribute 'group'.
     *
     * @return
     *       current value of 'group'
     */
    public Optional<String> getGroup() {
        return group;
    }

    /**
     * Getter accessor for attribute 'permissions'.
     *
     * @return
     *       current value of 'permissions'
     */
    public Optional<Set<String>> getPermissions() {
        return permissions;
    }
    
    public Stream<String> getPermissionsStream() {
        return permissions.orElse(new HashSet<String>()).stream();
    }

    /**
     * Getter accessor for attribute 'flippingStrategy'.
     *
     * @return
     *       current value of 'flippingStrategy'
     */
    public Optional<FlippingStrategy> getFlippingStrategy() {
        return flippingStrategy;
    }

    /**
     * Getter accessor for attribute 'customProperties'.
     *
     * @return
     *       current value of 'customProperties'
     */
    public Optional<Map<String, Property<?>>> getCustomProperties() {
        return customProperties;
    }

    /**
     * Accessor to read a custom property from Feature.
     *
     * @param propId
     *         property
     * @return
     *         property value (if exist)
     */
    public Optional < Property<?> > getCustomProperty(String propId) {
        Util.assertNotNull(propId);
        if (customProperties.isPresent()) {
            return Optional.ofNullable(customProperties.get().get(propId));
        }
        return Optional.empty();
    }
    
}
