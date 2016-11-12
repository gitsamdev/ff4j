package org.ff4j.archaius;

/*
 * #%L
 * ff4j-archaius
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

import org.ff4j.commonsconf.FF4jConfiguration;
import org.ff4j.commonsconf.PropertyStoreCommonsConfig;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyLogLevel;
import org.ff4j.property.PropertyLogLevel.LogLevel;
import org.ff4j.store.PropertyStore;
import org.ff4j.test.propertystore.PropertyStoreTestSupport;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Initialized Archaius with a commons config (no dynamic config)
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class PropertyStoreArchaiusCommonsConfTest extends PropertyStoreTestSupport {
    
    /** {@inheritDoc} */
    @Override
    protected PropertyStore initPropertyStore() {
        PropertyStore ff4jStore = new PropertyStoreInMemory("ff4j-properties.xml");
        return new PropertyStoreArchaius(new FF4jConfiguration(ff4jStore));
    }
    
    /** {@inheritDoc} */
    @Test
    public void testInitPropertyStore2() {
        PropertyStore sourceStore   = new PropertyStoreInMemory("ff4j-properties.xml");
        PropertyStore archaiusStore = new PropertyStoreArchaius(sourceStore);
        Assert.assertTrue(archaiusStore.exists("a"));
    }

    @Test(expected = IllegalStateException.class)
    public void initializationKO() {
        PropertyStoreCommonsConfig psConf = new PropertyStoreCommonsConfig();
        psConf.exists("toto");
    }
    
    /** TDD. */
    @Override
    @Test
    @Ignore
    public void updateKOInvalidValue() {
        // Cannot through error as FixedValue are not in Commons-config.
        System.out.println("Not Supported as fixedValues are ignored");
    }
    
    /** TDD. */
    @Override
    @Test
    @Ignore
    public void updateOKProperties() {
        System.out.println("Not Supported as all properties are String");
    }
    
    /** TDD. */
    @Override
    @Test
    public void readOKFixed() {
      // Given
        testedStore.create(new PropertyLogLevel(READ_OK_FIXED, LogLevel.ERROR));
        // When
        Property<?> log = testedStore.findById(READ_OK_FIXED);
        // Then
        Assert.assertNotNull(log);
        Assert.assertNotNull(log.getUid());
        Assert.assertEquals(READ_OK_FIXED, log.getUid());
        Assert.assertEquals(LogLevel.ERROR.name(), log.getValue());
    }
    
    /** TDD. */
    @Override
    @Test
    public void updateOK() {
        // Given
        testedStore.create(new PropertyLogLevel(UPDATE_OK, LogLevel.ERROR));
        // When
        testedStore.updateProperty(UPDATE_OK, "INFO");
        // Then
        Assert.assertEquals("INFO", testedStore.findById(UPDATE_OK).getValue());
    }
    
}
