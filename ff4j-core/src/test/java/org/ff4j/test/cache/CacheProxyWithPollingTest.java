package org.ff4j.test.cache;

public class CacheProxyWithPollingTest {
    
    /*
    @Test
    public void testCacheProxyManagerProperty() throws InterruptedException {
        // When
        FeatureStore  fs     = new FeatureStoreInMemory("ff4j.xml");
        PropertyStore ps     = new PropertyStoreInMemory("ff4j.xml");
        FF4jCacheManager cm  = new FF4jCacheManagerssss();
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
    }*/
    
    
    

}
