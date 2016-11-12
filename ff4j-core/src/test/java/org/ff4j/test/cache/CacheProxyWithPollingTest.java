package org.ff4j.test.cache;

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

import org.ff4j.cache.FF4jCacheManager;
import org.ff4j.cache.FF4jCacheProxy;
import org.ff4j.cache.InMemoryCacheManager;
import org.ff4j.cache.FF4jCachePollingScheduler;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.ff4j.store.FeatureStore;
import org.ff4j.store.PropertyStore;
import org.junit.Assert;
import org.junit.Test;

public class CacheProxyWithPollingTest {
    
    @Test
    public void testCacheProxyManagerProperty() throws InterruptedException {
        // When
        FeatureStore  fs     = new FeatureStoreInMemory("ff4j.xml");
        PropertyStore ps     = new PropertyStoreInMemory("ff4j.xml");
        FF4jCacheManager cm  = new InMemoryCacheManager();
        FF4jCacheProxy proxy = new FF4jCacheProxy(fs, ps, cm);

        // Start polling on 100ms basis
        proxy.startPolling(100);
        proxy.createSchema();
        Thread.sleep(200);
        
        // When (Remove something)
        fs.delete("AwesomeFeature");
        // Then (Proxy is not yet refresh)
        Assert.assertTrue(proxy.exists("AwesomeFeature"));
        
        // When (wait for cache refresh)
        Thread.sleep(200);
        // Then (also delete in cache si Cache is refreshed)
        Assert.assertFalse(proxy.exists("AwesomeFeature"));
        
        FF4jCachePollingScheduler scheduler = proxy.getStore2CachePoller();
        scheduler.setInitialDelay(scheduler.getInitialDelay());
        scheduler.setPollingDelay(scheduler.getPollingDelay());
        proxy.stopPolling();
        
        proxy.setStore2CachePoller(new FF4jCachePollingScheduler(fs, ps, cm));
    }
    
    
    @Test(expected = IllegalStateException.class)
    public void testStartCacheProxy() {
        FF4jCacheProxy proxy = new FF4jCacheProxy();
        proxy.startPolling(100);
    }
    
    @Test(expected = IllegalStateException.class)
    public void testStopCacheProxy() {
        FF4jCacheProxy proxy = new FF4jCacheProxy();
        proxy.stopPolling();
    }
    
    
    

}
