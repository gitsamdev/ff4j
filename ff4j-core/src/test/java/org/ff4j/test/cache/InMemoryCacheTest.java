package org.ff4j.test.cache;

import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.store.FeatureStore;
import org.ff4j.test.store.CoreFeatureStoreTestSupport;

/**
 * Testing class of {@link FF4jCacheManagerssss} class.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class InMemoryCacheTest extends CoreFeatureStoreTestSupport {

    /** {@inheritDoc} */
    @Override
    public FeatureStore initStore() {
        return new FeatureStoreInMemory("ff4j.xml");
        /*
        return new FF4jCacheProxy(
                new FeatureStoreInMemory("ff4j.xml"), 
                new PropertyStoreInMemory("ff4j.xml"),
                new FF4jCacheManagerssss());*/
    }

/*
    @Test
    public void testInitializations() {
        FF4jCacheManagerssss fcm = new FF4jCacheManagerssss();        
        Assert.assertNotNull(fcm.getFeatureNativeCache());
        Assert.assertNotNull(fcm.getPropertyNativeCache());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullisIlegal() {
        new FF4jCacheManagerssss().putFeature(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPutNullisIlegal2() {
        new FF4jCacheManagerssss().putFeature(null, 1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPutNullPropertyisIlegal() {
        new FF4jCacheManagerssss().putProperty(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRequiredArgumentCacheManager() {
        new FF4jCacheProxy().getCacheManager();
    }

    @Test
    public void testExistBis() {
        FF4jCacheProxy fscp = new FF4jCacheProxy(
                new FeatureStoreInMemory("ff4j.xml"), null,  
                new FF4jCacheManagerssss());
        Assert.assertFalse(fscp.exists("toto"));
        Assert.assertFalse(fscp.exists("toto"));
        Assert.assertTrue(fscp.exists("first"));
        Assert.assertTrue(fscp.exists("first"));
    }
    
    @Test
    public void testClear() {
        // Given
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putProperty(new PropertyString("p1"));
        Assert.assertFalse(imcm.listCachedPropertyNames().isEmpty());
        // When
        imcm.clearProperties();
        // Then
        Assert.assertTrue(imcm.listCachedPropertyNames().isEmpty());
    }
    
    @Test
    public void testEvictProperty1() {
        // Given
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putProperty(new PropertyString("p1"));
        Assert.assertFalse(imcm.listCachedPropertyNames().isEmpty());
        // When
        imcm.evictProperty("p1");
        // Then
        Assert.assertTrue(imcm.listCachedPropertyNames().isEmpty());
    }
    
    @Test
    public void testEvictProperty2() {
        // Given
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putProperty(new PropertyString("p2"));
        Assert.assertFalse(imcm.listCachedPropertyNames().isEmpty());
        // When
        imcm.evictProperty("p1");
        // Then
        Assert.assertFalse(imcm.listCachedPropertyNames().isEmpty());
    }
    
    @Test
    public void testReadFeature() {
        // Given
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putFeature(new Feature("f1"), 100);
        // When
        Feature f = imcm.getFeature("f1");
        // Then
        Assert.assertNotNull(f);
        // When
        imcm.putFeature(new Feature("f1"), 1);
        
    }
    
    @Test
    public void testAccessors() {
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        Assert.assertNotNull(imcm.getCacheProviderName());
        Assert.assertTrue(imcm.listCachedFeatureNames().isEmpty());
        Assert.assertTrue(imcm.listCachedPropertyNames().isEmpty());
    }
    
    @Test
    public void testGetProperty() throws InterruptedException {
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putProperty(new PropertyString("p1"));
        Assert.assertNull(imcm.getProperty("p2"));
        Assert.assertNotNull(imcm.getProperty("p1"));
    }

    @Test
    public void testGetFeatureTimeout() throws InterruptedException {
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putFeature(new Feature("f2"), 1);
        Thread.sleep(1100);
        Assert.assertNull(imcm.getFeature("f2"));
    }
    
    @Test
    public void testGetPropertyTimeout() throws InterruptedException {
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putProperty(new PropertyString("p1"), 1);
        imcm.putProperty(new PropertyString("p2"), 10);
        Thread.sleep(1100);
        Assert.assertNull(imcm.getProperty("p1"));
        Assert.assertNotNull(imcm.getProperty("p2"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGetProperty2()  {
        FF4jCacheManagerssss imcm = new FF4jCacheManagerssss();
        imcm.putProperty(null, 1);
    }*/
   
}
