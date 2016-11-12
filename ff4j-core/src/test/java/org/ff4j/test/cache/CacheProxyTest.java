package org.ff4j.test.cache;

import java.util.HashSet;
import java.util.Set;

import org.ff4j.FF4j;
import org.ff4j.cache.FF4jCacheManager;
import org.ff4j.cache.FF4jCacheProxy;
import org.ff4j.cache.InMemoryCacheManager;
import org.ff4j.feature.Feature;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyLogLevel;
import org.ff4j.property.PropertyLogLevel.LogLevel;
import org.ff4j.property.PropertyString;
import org.junit.Assert;

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


import org.junit.Test;


public class CacheProxyTest {
    
    @Test(expected = IllegalArgumentException.class)
    public void testCacheProxyNullTriggerException() {
        FF4jCacheProxy proxy = new FF4jCacheProxy();
        proxy.getTargetFeatureStore();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCacheProxyNullTriggerException2() {
        FF4jCacheProxy proxy = new FF4jCacheProxy();
        proxy.getTargetPropertyStore();
    }
    
    @Test
    public void testCacheProxyManager() {
        FF4jCacheProxy proxy = new FF4jCacheProxy();
        FF4jCacheManager cm = new InMemoryCacheManager();
        proxy.setCacheManager(cm);
        proxy.isCached();
        Assert.assertNotNull(proxy.getCacheProvider());
        proxy.setTargetPropertyStore(new PropertyStoreInMemory());
        Assert.assertEquals(0, proxy.findAll().size());
        proxy.create(new PropertyString("p1", "v1"));
        Assert.assertTrue(proxy.exists("p1"));
        Assert.assertFalse(proxy.exists("p2"));
        
        proxy.setTargetFeatureStore(new FeatureStoreInMemory());
        Set < Feature> setOfFeatures = new HashSet<Feature>();
        setOfFeatures.add(new Feature("f1"));
        setOfFeatures.add(new Feature("f2"));
        proxy.save(setOfFeatures);
    }
    
    @Test
    public void testCacheProxyManagerProperty() {
        FF4jCacheProxy proxy = new FF4jCacheProxy();
        proxy.setTargetPropertyStore(new PropertyStoreInMemory());
        proxy.setTargetFeatureStore(new FeatureStoreInMemory());
        proxy.setCacheManager(new InMemoryCacheManager());
        Assert.assertTrue(proxy.isEmpty());
        
        proxy.create(new Feature("a"));
        Assert.assertFalse(proxy.isEmpty());
        
        proxy.create(new PropertyString("p1", "v1"));
        Property<?> p1 = proxy.findById("p1");
        proxy.findById("p1");
        proxy.getTargetPropertyStore().create(new PropertyString("p2"));
        proxy.findById("p2");
        
        proxy.updateProperty("p1", "v2");
        proxy.updateProperty(p1);
        Assert.assertFalse(proxy.isEmpty());
        
        Assert.assertFalse(proxy.listPropertyNames().isEmpty());
        proxy.delete("p1");
        proxy.clear();
        
        Set < Property<?>> setOfProperty = new HashSet<Property<?>>();
        setOfProperty.add(new PropertyLogLevel("a", LogLevel.INFO));
        setOfProperty.add(new PropertyLogLevel("titi1", LogLevel.INFO));
        proxy.importProperties(setOfProperty);
        
        // Already in cache, but not same value
        proxy.create(new PropertyString("cacheNStore", "cacheNStore"));
        proxy.read("cacheNStore", p1);
        
        // Not in cache, but in store, but not same default value
        proxy.getTargetPropertyStore().create(new PropertyString("p4", "v4"));
        proxy.read("p1", p1);
        
        proxy.read("p1", p1);
        // Nowhere, return default
        proxy.read("p2", new PropertyString("p2"));
        proxy.read("p1", new PropertyString("p3"));
    }
    
    @Test
    public void testCacheProxy() {
        FF4j myFF4J = new FF4j();
        Assert.assertNull(myFF4J.getCacheProxy());
        myFF4J.setEnableAudit(true);
        Assert.assertNull(myFF4J.getCacheProxy());
        Assert.assertNotNull(myFF4J.getConcreteFeatureStore());
        
        
    }

}
