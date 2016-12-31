package org.ff4j.test.store;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;

import org.ff4j.FF4j;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyStore;
import org.ff4j.property.domain.PropertyDate;
import org.ff4j.property.domain.PropertyString;

/*
 * #%L
 * ff4j-test
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

import org.ff4j.test.propertystore.PropertyStoreTestSupport;
import org.junit.Assert;
import org.junit.Test;

public class InMemoryPropertyStoreTest extends PropertyStoreTestSupport {

    @Override
    protected PropertyStore initPropertyStore() {
        return  new PropertyStoreInMemory("test-ff4j-features.xml");
    }
    

    /** TDD. */
    @Test
    @Override
    public void existfilled() {
        // When-Then
        Assert.assertTrue(testedStore.exists("a"));
        Assert.assertFalse(testedStore.exists("koala"));
    }
    
    /** TDD. */
    @Test
    @Override
    public void valueFixed() {
        // When-Then
        Assert.assertTrue(testedStore.exists("a"));
        Assert.assertEquals("AMER", testedStore.findById("a").getValue());
    }
    
    public void testProperty() {
        FF4j ff4j = new FF4j("ff4j.xml");
        ff4j.getPropertiesStore().create(new PropertyDate("property_3", new Date()));
        Property<?> ap = ff4j.getPropertiesStore().findById("property_3");
        PropertyDate pDate = (PropertyDate) ap;
        pDate.setValue(new Date());
        ff4j.getPropertiesStore().updateProperty(pDate);
        ff4j.getPropertiesStore().delete("property_3");
        Assert.assertFalse(testedStore.exists("property_3"));
    }
    
    @Test
    public void testInheritMethods() {
        PropertyStoreInMemory ip = new PropertyStoreInMemory();
        ip.importPropertiesFromXmlFile("test-ff4j-features.xml");
        Assert.assertNotNull(ip.toJson());
        ip.isEmpty();
    }
    
    @Test
    public void testInitStores() {
        new PropertyStoreInMemory(new HashMap<String, Property<?>>());
        InputStream in =  getClass().getClassLoader().getResourceAsStream("test-ff4j-features.xml");
        new PropertyStoreInMemory(in);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithInvalidFileFailed() {
        new PropertyStoreInMemory("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithInvalidFileFailed2() {
        new PropertyStoreInMemory((String) null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidXML() {
        new PropertyStoreInMemory(new HashMap<String, Property<?>>());
        InputStream in =  getClass().getClassLoader().getResourceAsStream("invalid.xml");
        new PropertyStoreInMemory(in);
    }

    @Test
    public void testListProperties() {
        PropertyStoreInMemory ips = new PropertyStoreInMemory();
        ips.setProperties(null);
        Assert.assertNull(ips.listPropertyNames());
    }
    
    @Test
    public void testGetters() {
        PropertyStoreInMemory ips = new PropertyStoreInMemory();
        ips.setLocation("test-ff4j-features.xml");
        ips.setFileName("invalid.xml");
        Assert.assertEquals("invalid.xml", ips.getFileName());
    }
    
    @Test
    public void testEmpty() {
        // Given
        PropertyStoreInMemory ips = new PropertyStoreInMemory();
        Assert.assertTrue(ips.isEmpty());
    }
    
    @Test
    public void testEmpty2() {
        // Given
        PropertyStoreInMemory ips = new PropertyStoreInMemory();
        ips.setProperties(null);
        Assert.assertTrue(ips.isEmpty());
    }
    
    @Test
    public void testEmpty3() {
        // Given
        PropertyStoreInMemory ips = new PropertyStoreInMemory();
        ips.create(new PropertyString("P1", "v1"));
        Assert.assertFalse(ips.isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDonotImportNull() {
        PropertyStoreInMemory f = new PropertyStoreInMemory();
        f.importPropertiesFromXmlFile(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDonotImportInvalid() {
        PropertyStoreInMemory f = new PropertyStoreInMemory();
        f.importPropertiesFromXmlFile("invalid.xml");
    }
    
    @Test
    public void testImportTwice() {
        PropertyStoreInMemory f = new PropertyStoreInMemory();
        f.importPropertiesFromXmlFile("test-ff4j-features.xml");
        f.importPropertiesFromXmlFile("test-ff4j-features.xml");
    }
    
    

}
