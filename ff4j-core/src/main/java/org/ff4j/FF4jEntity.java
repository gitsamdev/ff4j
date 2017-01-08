package org.ff4j;

import static org.ff4j.utils.JsonUtils.attributeAsJson;
import static org.ff4j.utils.JsonUtils.valueAsJson;

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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.ff4j.property.Property;
import org.ff4j.property.domain.PropertyFactory;
import org.ff4j.security.AccessControlList;
import org.ff4j.security.RestrictedAccessObject;
import org.ff4j.utils.Util;

/**
 * Superclass for FF4J objects.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class FF4jEntity<T extends FF4jEntity<?>> implements Comparable<T>, Serializable, RestrictedAccessObject {

    /** serial number. */
    private static final long serialVersionUID = -6001829116967488353L;
    
    /** formatter for creation date and last modified. */
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /** unique identifier. */
    protected String uid;
    
    /** Permission : by Default everyOne can use the Feature. */
    protected AccessControlList accessControlList = new AccessControlList();
    
    /** Description of the meaning. */
    protected Optional < String > description = Optional.empty();
    
    /** Related people to contact for any relevant question. */
    protected Optional < String > owner = Optional.empty();
    
    /** Creation date if available in the store. */
    protected Optional < LocalDateTime > creationDate = Optional.empty();
    
    /** Last modified date if available in the underlying store. */
    protected Optional < LocalDateTime > lastModifiedDate = Optional.empty();

    /** Add you own attributes to a feature. */
    protected Optional<Map<String, Property<?>>> customProperties = Optional.empty();
    
    /**
     * Json common parts
     * 
     * @return
     *      json expression for the common attributes
     */
    public String baseJson() {
        StringBuilder json = new StringBuilder("\"uid\":" + valueAsJson(uid));
        json.append(attributeAsJson("type", getClass().getName()));
        description.ifPresent(
                d -> json.append(attributeAsJson("description", d)));
        owner.ifPresent(
                d -> json.append(attributeAsJson("owner", d)));
        creationDate.ifPresent(
                d -> json.append(attributeAsJson("creationDate", d.format(FORMATTER))));
        lastModifiedDate.ifPresent(
                d -> json.append(attributeAsJson("lastModifiedDate", d.format(FORMATTER))));
        customProperties.ifPresent( cp -> {
            json.append(", \"customProperties\":[");
            boolean first = true;
            for (Property<?> customProperty : cp.values()) {
                json.append(first ? "" : ",");
                json.append(customProperty.toJson());
                first = false;
            }
            json.append("]");
        });
        json.append(", \"accessControlList\":" + getAccessControlList().toJson());
        return json.toString();   
    }
    
    /**
     * Parameterized constructor.
     *
     * @param uid
     */
    protected FF4jEntity(String uid) {
        Util.requireHasLength(uid);
        this.uid         = uid;
        creationDate     = Optional.of(LocalDateTime.now());
        lastModifiedDate = creationDate;
    }
    
    protected FF4jEntity(String uid, FF4jEntity<?> e) {
        this(uid);
        Util.requireNotNull(e);
        this.accessControlList = e.getAccessControlList();
        e.getOwner().ifPresent(o -> this.owner = Optional.of(o));
        e.getDescription().ifPresent(d -> this.description = Optional.of(d));
        e.getCreationDate().ifPresent(c -> this.creationDate = Optional.of(c));
        e.getLastModifiedDate().ifPresent(c -> this.lastModifiedDate = Optional.of(c));
        e.getCustomProperties().ifPresent(
                cp -> cp.values().stream().forEach(
                        p -> addCustomProperty(PropertyFactory.createProperty(p))));
    }
    
    /** {@inheritDoc} */
    @Override
    public int compareTo(T otherObject) {
        return this.uid.compareTo(otherObject.uid);
    }
    
    @SuppressWarnings("unchecked")
    public T setDescription(String description) {
        this.description = Optional.ofNullable(description);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T setOwner(String owner) {
        this.owner = Optional.ofNullable(owner);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T setCreationDate(LocalDateTime currentDate) {
        this.creationDate = Optional.of(currentDate);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T setLastModified(LocalDateTime currentDate) {
        this.lastModifiedDate = Optional.of(currentDate);
        return (T) this;
    }
    
    @SuppressWarnings("unchecked")
    public T setCustomProperties(Map<String, Property<?>> custom) {
        customProperties = Optional.ofNullable(custom);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T setCustomProperties(Property<?>... properties) {
        if (properties == null) return (T) this;;
        return setCustomProperties(Arrays.stream(properties).
                collect(Collectors.toMap(Property::getUid, Function.identity())));
    }
    
    /**
     * Add to custom properties.
     *
     * @param properties
     *      target properties to add
     * @return
     *      the new value for current eneityt
     */
    @SuppressWarnings("unchecked")
    public T addCustomProperties(Property<?>... properties) {
        if (properties != null) {
            // Convert as Map
            Map<String, Property<?>> mapOfProperties = Arrays.stream(properties)
                    .collect(Collectors.toMap(Property::getUid, Function.identity()));
            if (customProperties.isPresent()) {
                customProperties.get().putAll(mapOfProperties);
            } else {
                customProperties = Optional.of(mapOfProperties);
            }
        }
        return (T) this;
    }
    
    /**
     * Getter accessor for attribute 'description'.
     *
     * @return
     *       current value of 'description'
     */
    public Optional<String> getDescription() {
        return description;
    }

    /**
     * Getter accessor for attribute 'creationDate'.
     *
     * @return
     *       current value of 'creationDate'
     */
    public Optional<LocalDateTime> getCreationDate() {
        return creationDate;
    }

    /**
     * Getter accessor for attribute 'lastModifiedDate'.
     *
     * @return
     *       current value of 'lastModifiedDate'
     */
    public Optional<LocalDateTime> getLastModifiedDate() {
        return lastModifiedDate;
    }

    /**
     * Getter accessor for attribute 'owner'.
     *
     * @return
     *       current value of 'owner'
     */
    public Optional<String> getOwner() {
        return owner;
    }
    
    /**
     * Getter accessor for attribute 'uid'.
     *
     * @return current value of 'uid'
     */
    public String getUid() {
        return uid;
    }
    
    /**
     * Getter accessor for attribute 'customProperties'.
     *
     * @return current value of 'customProperties'
     */
    public Optional<Map<String, Property<?>>> getCustomProperties() {
        return customProperties;
    }
    
    /**
     * Accessor to read a custom property from Feature.
     *
     * @param propId
     *            property
     * @return property value (if exist)
     */
    public Optional<Property<?>> getCustomProperty(String propId) {
        Util.requireNotNull(propId);
        if (customProperties.isPresent()) {
            return Optional.ofNullable(customProperties.get().get(propId));
        }
        return Optional.empty();
    }
    
    /**
     * Create new custom property.
     * 
     * @param property
     *      target property
     * @return
     *      current object
     */
    public T addCustomProperty(Property<?> property) {
        return addCustomProperties(property);
    }

    /**
     * Getter accessor for attribute 'accessControlList'.
     *
     * @return
     *       current value of 'accessControlList'
     */
    @Override
    public AccessControlList getAccessControlList() {
        return accessControlList;
    }

    /**
     * Setter accessor for attribute 'accessControlList'.
     * @param accessControlList
     * 		new value for 'accessControlList '
     */
    public void setAccessControlList(AccessControlList accessControlList) {
        this.accessControlList = accessControlList;
    } 
    
}
