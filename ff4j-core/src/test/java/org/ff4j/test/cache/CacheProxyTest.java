package org.ff4j.test.cache;

public class CacheProxyTest {
    
    /*
    @Test
    public void testCacheProxyManager() {
        FF4jCacheProxy proxy = new FF4jCacheProxy();
        FF4jCacheManager cm = new FF4jCacheManagerssss();
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
        proxy.setCacheManager(new FF4jCacheManagerssss());
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
    */

}
