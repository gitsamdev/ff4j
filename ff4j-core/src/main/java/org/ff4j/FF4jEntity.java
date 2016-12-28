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
import org.ff4j.security.FF4jEveryOne;
import org.ff4j.security.FF4jGrantees;
import org.ff4j.security.FF4jPermission;
import org.ff4j.security.FF4jUser;
import org.ff4j.utils.JsonUtils;
import org.ff4j.utils.Util;

/**
 * Superclass for FF4J objects.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class FF4jEntity<T extends FF4jEntity<?>> implements Comparable<T>, Serializable {

    /** serial number. */
    private static final long serialVersionUID = -6001829116967488353L;
    
    /** formatter for creation date and last modified. */
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /** unique identifier. */
    protected String uid;
    
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
    
    /** Permission : by Default everyOne can use the Feature. */
    protected Map < FF4jPermission, FF4jGrantees > accessControlList = Util.mapOf(FF4jPermission.USE, new FF4jEveryOne());
    
    /**
     * Json common parts
     * 
     * @return
     *      json expression for the common attributes
     */
    public String baseJson() {
        StringBuilder json = new StringBuilder("\"uid\":" + valueAsJson(uid));
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
        if (accessControlList != null && !accessControlList.isEmpty()) {
            json.append(", \"accessControlList\":" + JsonUtils.mapAsJson(accessControlList));
        }
        return json.toString();   
    }
    
    /**
     * Parameterized constructor.
     *
     * @param uid
     */
    protected FF4jEntity(String uid) {
        this.uid         = uid;
        creationDate     = Optional.of(LocalDateTime.now());
        lastModifiedDate = creationDate;
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
     * Getter accessor for attribute 'accessControlList'.
     *
     * @return
     *       current value of 'accessControlList'
     */
    public Map<FF4jPermission, FF4jGrantees> getAccessControlList() {
        return accessControlList;
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
     * Check if target user isGranted for a permission.
     *
     * @param user
     *      current user
     * @param permission
     *      expected permission
     * @return
     *      if current user has expected permission
     */
    public boolean isGranted(FF4jUser user, FF4jPermission permission) {
        return getAccessControlList().get(permission).isUserGranted(user);
    }
    
    /**
     * Check if current user can use the entity (ACL).
     *
     * @param user
     *      current user
     * @return
     *      is the user can use the entity
     */
    public boolean canUse(FF4jUser user) {
        return isGranted(user, FF4jPermission.USE);
    }
    
    /**
     * Grant a set of userNames to the permission.
     *
     * @param permission
     *          the right to work with
     * @param users
     *          the users to allow on this permission
     */
    public void grantUsers(FF4jPermission permission, String... users)  {
        if (null == getAccessControlList().get(permission)) {
            getAccessControlList().put(permission, new FF4jGrantees());
        }
        getAccessControlList().get(permission).getUsers().addAll(Arrays.asList(users));
    }
    
    /**
     * Grant a set of groupNames to the permission.
     *
     * @param permission
     *          the right to work with
     * @param groups
     *          the groups to allow on this permission
     */
    public void grantGroups(FF4jPermission permission, String... groups)  {
        if (null == getAccessControlList().get(permission)) {
            getAccessControlList().put(permission, new FF4jGrantees());
        }
        getAccessControlList().get(permission).getGroups().addAll(Arrays.asList(groups));
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
    
}
