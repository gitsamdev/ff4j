package org.ff4j.property;

import java.util.stream.Stream;

import org.ff4j.store.FF4jRepository;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2015 Ff4J
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

/**
 * CRUD repository to perform operation on properties.
 *
 * @author Cedrick Lunven (@clunven)
 */
public interface PropertyStore extends FF4jRepository < String, Property<?> > {
    
    /**
     * Read property value and if not found return the default value.
     * 
     * @param name
     *      target property name
     * @return
     *      property of exist
     */
    Property<?> read(String name);
            
    /**
     * Read property value and if not found return the default value.
     * 
     * @param name
     *      target property name
     * @return
     *      property of exist
     */
    Property<?> read(String name, Property < ? > defaultValue);
    
    /**
     * Update existing property.
     *
     * @param name
     *      target name
     * @param newValue
     *      new value
     */
    void update(String name, String newValue);
    
    /**
     * List all property names.
     *
     * @return
     */
    Stream < String > listPropertyNames();
    
    /**
     * Initialize target database with expected schema if needed.
     */
    void createSchema();
}
