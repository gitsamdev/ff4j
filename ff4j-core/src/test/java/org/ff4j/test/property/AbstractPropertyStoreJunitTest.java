package org.ff4j.test.property;

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


import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.ff4j.exception.PropertyAlreadyExistException;
import org.ff4j.exception.PropertyNotFoundException;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyDate;
import org.ff4j.property.PropertyLogLevel;
import org.ff4j.property.PropertyLogLevel.LogLevel;
import org.ff4j.property.PropertyString;
import org.ff4j.store.FeatureStore;
import org.ff4j.store.PropertyStore;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * SuperClass to test stores within core project
 *
 * @author Cedrick Lunven (@clunven)
 */
public abstract class AbstractPropertyStoreJunitTest {

    /** Tested Store. */
    protected PropertyStore testedStore;

    /** Default InMemoryStore for test purposes. */
    protected FeatureStore defaultStore = new FeatureStoreInMemory();
    
    /** {@inheritDoc} */
    @Before
    public void setUp() throws Exception {
        testedStore = initPropertyStore();
    }

    /**
     * Any store test will declare its store through this callback.
     * 
     * @return working feature store
     * @throws Exception
     *             error during building feature store
     */
    protected abstract PropertyStore initPropertyStore();
    
    
    // --------------- exist -----------
    
    @Test
    public void testEmptyStore() {
        Assert.assertFalse(testedStore.isEmpty());
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void existKONull() {
        // given
        testedStore.exists(null);
        // then expect to fail
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void existKOEmpty() {
        // Given
        testedStore.exists("");
        // Then
        // then expect to fail
    }
    
    /** TDD. */
    @Test
    public void existfalse() {
        // When-Then
        Assert.assertFalse(testedStore.exists("toto"));
    }
    
    // --------------- create -----------    
    
    /** TDD. */
    @Test
    public void addPropertyOKsimple() {
        // Given
        Assert.assertFalse(testedStore.exists("toto"));
        // When
        testedStore.create(new PropertyString("toto", "ff4j"));
        // Then
        Assert.assertTrue(testedStore.exists("toto"));
    }
    
    /** TDD. */
    @Test
    public void addPropertyOKLogLevel() {
        // Given
        //Assert.assertFalse(testedStore.exist("log"));
        // When
        testedStore.create(new PropertyLogLevel("log", LogLevel.DEBUG));
        // Then
        Assert.assertTrue(testedStore.exists("log"));
    }
    
    /** TDD. */
    @Test
    public void addPropertyOKDate() {
        // Given
        //Assert.assertFalse(testedStore.exist("log"));
        // When
        testedStore.create(new PropertyDate("ddate", new Date()));
        // Then
        Assert.assertTrue(testedStore.exists("ddate"));
    }
    
    /** TDD. */
    @Test(expected = PropertyAlreadyExistException.class)
    public void addPropertyKOAlreadyExist() {
        // Given
        testedStore.create(new PropertyLogLevel("log", LogLevel.DEBUG));
        Assert.assertTrue(testedStore.exists("log"));
        // When
        testedStore.create(new PropertyLogLevel("log", LogLevel.DEBUG));
        // Then expect to fail
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void addPropertyKONull() {
        // Given
        testedStore.create(null);
        // Then expect to fail
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void addPropertyKONullName() {
        // Given
        testedStore.create(new PropertyString(null, ""));
        // Then expect to fail
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void addPropertyKOEmptyName() {
        // Given
        testedStore.create(new PropertyString("", ""));
        // Then expect to fail
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void addPropertyKOInvalidValue() {
        // Given
        testedStore.create(new PropertyLogLevel("log", "TRUC"));
        // Then No error
    }
    
    
    // ------------------ read --------------------
    
    @Test
    public void readOK() {
        // Given
        testedStore.create(new PropertyString("toto", "ff4j"));
        // When
        Property<?> ap = testedStore.findById("toto");
        // Then
        Assert.assertNotNull(ap);
        Assert.assertNotNull(ap.getUid());
        Assert.assertEquals("toto", ap.getUid());
        Assert.assertEquals("ff4j", ap.getValue());
        Assert.assertEquals("ff4j", ap.asString());
        Assert.assertFalse(ap.getFixedValues().isPresent());
    }
    
    @Test
    public void readOKFixed() {
        // Given
        testedStore.create(new PropertyLogLevel("log", LogLevel.ERROR));
        // When
        Property<?> log = testedStore.findById("log");
        // Then
        Assert.assertNotNull(log);
        Assert.assertNotNull(log.getUid());
        Assert.assertEquals("log", log.getUid());
        Assert.assertEquals(LogLevel.ERROR, log.getValue());
        Assert.assertEquals("ERROR", log.asString());
        Assert.assertNotNull(log.getFixedValues());
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void readKOnull() {
        // Given
        testedStore.findById(null);
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void readKOempty() {
        // Given
        testedStore.findById("");
        // Expected error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = PropertyNotFoundException.class)
    public void readKOnotExist() {
        // Given
        Assert.assertFalse(testedStore.exists("invalid"));
        // When
        testedStore.findById("invalid");
        // Expected error
        Assert.fail();
    }
    
    // ------------------ update --------------------
    
    /** TDD. */
    @Test(expected = PropertyNotFoundException.class)
    public void updateKOdoesnotExist() {
        // Given
        Assert.assertFalse(testedStore.exists("invalid"));
        // When
        testedStore.updateProperty("invalid", "aa");
        // Expected error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = PropertyNotFoundException.class)
    public void updateKOdoesnotExist2() {
        // Given
        Assert.assertFalse(testedStore.exists("invalid"));
        // When
        testedStore.updateProperty(new PropertyString("invalid", "abc"));
        // Expected error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void updateKOnull() {
        // When
        testedStore.updateProperty(null, "aa");
        // Expected error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void updateKOempty() {
        // When
        testedStore.updateProperty("", "aa");
        // Expected error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void updateKoPropertyNull() {
        // When
        testedStore.updateProperty(null);
        // Expected error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void updateKOInvalidValue() {
        // Given
        testedStore.create(new PropertyLogLevel("log", LogLevel.ERROR));
        // When
        testedStore.updateProperty("log", "KO");
    }
    
    /** TDD. */
    @Test
    public void updateOK() {
        // Given
        testedStore.create(new PropertyLogLevel("log", LogLevel.ERROR));
        // When
        testedStore.updateProperty("log", "INFO");
        // Then
        Assert.assertEquals(LogLevel.INFO, testedStore.findById("log").getValue());
    }
    
    /** TDD. */
    @Test
    public void updateOKProperties() {
        // Given
        testedStore.create(new PropertyLogLevel("log", LogLevel.ERROR));
        // When
        PropertyLogLevel pll = new PropertyLogLevel("log", LogLevel.INFO);
        testedStore.updateProperty(pll);
        // Then
        Assert.assertEquals(LogLevel.INFO, testedStore.findById("log").getValue());
    }
    
    // ------------------ delete -------------------- 

    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void deleteKOnull() {
        // When
        testedStore.delete(null);
        // Expected Error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = IllegalArgumentException.class)
    public void deleteKOempty() {
        // When
        testedStore.delete("");
        // Expected Error
        Assert.fail();
    }
    
    /** TDD. */
    @Test(expected = PropertyNotFoundException.class)
    public void deleteKOdoesnotexist() {
        // Given
        Assert.assertFalse(testedStore.exists("invalid"));
        // When
        testedStore.delete("invalid");
        // Expected Error
        Assert.fail();
    }
    
    /** TDD. */
    @Test
    public void deleteOK() {
        // Given
        testedStore.create(new PropertyString("toto", "ff4j"));
        Assert.assertTrue(testedStore.exists("toto"));
        // When
        testedStore.delete("toto");
        // Then
        Assert.assertFalse(testedStore.exists("toto"));
    }
    
    @Test
    public void existfilled() {
        // When-Then
        Assert.assertTrue(testedStore.exists("a"));
        Assert.assertFalse(testedStore.exists("k"));
    }
    
    @Test
    public void valueFixed() {
        // When-Then
        Assert.assertTrue(testedStore.exists("a"));
        Assert.assertEquals("AMER", testedStore.findById("a").getValue());
    }
    
    /** TDD. */
    @Test
    public void listPropertyNames() {
        // Given, When
        Set< String > proNames = testedStore.listPropertyNames();
        // Then
       Assert.assertTrue(proNames.contains("a"));
    }
    
    /** TDD. */
    @Test
    public void readAllProperties() {
        // Given
        Assert.assertNotNull(testedStore);
        // When
        Map <String, Property<?>> mapsOf = testedStore.findAll();
        // When
        Assert.assertTrue(mapsOf.containsKey("a"));
        Assert.assertTrue(mapsOf.containsKey("b"));
    }
    
    /** TDD. */
    @Test
    public void clear() {
        // Given
        Assert.assertNotNull(testedStore);
        Map <String, Property<?>> before = testedStore.findAll();
        Assert.assertFalse(before.isEmpty());
        // When
        testedStore.clear();
        // Then
        Assert.assertTrue(testedStore.findAll().isEmpty());
        
        /// Reinit
        for (String pName : before.keySet()) {
            testedStore.create(before.get(pName));
        }
    }
    
    /** TDD. */
    @Test
    public void importPropertiesNull() {
        // Given
        Assert.assertNotNull(testedStore);
        // When
        testedStore.importProperties(null);
        // Then, no issue
    }
    
    /** TDD. */
    @Test
    public void importPropertiesOK() {
        // Given
        Assert.assertNotNull(testedStore);
        Assert.assertFalse(testedStore.exists("titi1"));
        Assert.assertFalse(testedStore.exists("titi2"));
        Assert.assertTrue(testedStore.exists("a"));
        
        // When
        Set < Property<?>> setOfProperty = new HashSet<Property<?>>();
        setOfProperty.add(new PropertyLogLevel("a", LogLevel.INFO));
        setOfProperty.add(new PropertyLogLevel("titi1", LogLevel.INFO));
        setOfProperty.add(new PropertyLogLevel("titi2", LogLevel.INFO));
        testedStore.importProperties(setOfProperty);
        
        // Then
        Assert.assertTrue(testedStore.exists("titi1"));
        Assert.assertTrue(testedStore.exists("titi2"));
        Assert.assertTrue(testedStore.exists("a"));
    }
    
    /** TDD. */
    @Test
    public void readPropertyDefaultExist() {
        // Given
        Assert.assertTrue(testedStore.exists("a"));
        // When
        Property<?> defaultA = new PropertyString("a", "GLOUGLOU");
        // Then
        Assert.assertEquals("AMER", testedStore.read("a", defaultA).getValue());
    }
    
    /** TDD. */
    @Test
    public void readPropertyDefaultNotExist() {
        Property<?> defaultA = new PropertyString("aaaa", "GLOUGLOU");
        // Given
        Assert.assertFalse(testedStore.exists("aaaa"));
        // Then
        Assert.assertEquals("GLOUGLOU", testedStore.read("aaaa", defaultA).getValue());
    }
    
}
