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
import java.util.Optional;

/**
 * Superclass for FF4J objects.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public abstract class FF4jBaseObject<T extends FF4jBaseObject<?>> implements Serializable {

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
    
    /**
     * Parameterized constructor.
     *
     * @param uid
     */
    protected FF4jBaseObject(String uid) {
        this.uid = uid;
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
    
    public String baseJson() {
        StringBuilder json = new StringBuilder("\"uid\":" + valueAsJson(uid));
        description.ifPresent(
                d -> attributeAsJson("description", d));
        owner.ifPresent(
                d -> attributeAsJson("owner", d));
        creationDate.ifPresent(
                d -> attributeAsJson("creationDate", d.format(FORMATTER)));
        lastModifiedDate.ifPresent(
                d -> attributeAsJson("lastModifiedDate", d.format(FORMATTER)));
        return json.toString();   
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

}
