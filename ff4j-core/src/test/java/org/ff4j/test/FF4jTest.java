package org.ff4j.test;

import static org.ff4j.event.EventConstants.ACTION_CHECK_OK;
import static org.ff4j.event.EventConstants.SOURCE_JAVA;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 Ff4J
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.ff4j.FF4j;
import org.ff4j.FF4jContext;
import org.ff4j.audit.PropertyStoreAuditProxy;
import org.ff4j.cache.CacheManagerInMemory;
import org.ff4j.event.Event;
import org.ff4j.event.EventBuilder;
import org.ff4j.event.EventPublisher;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.inmemory.FeatureUsageInMemory;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.property.Property;
import org.ff4j.property.domain.PropertyString;
import org.ff4j.strategy.PonderationStrategy;
import org.ff4j.utils.Util;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test operations over {@link FF4j}
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class FF4jTest extends AbstractFf4jTest {

    @Override
    public FF4j initFF4j() {
        return new FF4j("ff4j.xml");
    }
    
    @Test(expected = FeatureNotFoundException.class)
    public void readFeatureNotFound() {
        // Given
        FF4j ff4j = new FF4j();
        // When
        ff4j.getFeature("i-dont-exist");
        // Then
        // expect error...
    }
    
    @Test
    public void testDeleteFeature() {
        FF4j ff4j = new FF4j("ff4j.xml");
        ff4j.audit(true);
        Assert.assertTrue(ff4j.getFeatureStore().exists(F1));
        ff4j.getFeatureStore().delete(F1);
        Assert.assertFalse(ff4j.getFeatureStore().exists(F1));
    }
    
    @Test
    public void testDisableWithAudit() {
        // Given
        FF4j ff4j = new FF4j("ff4j.xml");
        ff4j.audit(true);
        Assert.assertTrue(ff4j.getFeatureStore().exists(F1));
        Assert.assertTrue(ff4j.getFeature(F1).isEnable());
        // When
        ff4j.toggleOff(F1);
        // Then
        Assert.assertFalse(ff4j.getFeature(F1).isEnable());
    }
    
    @Test
    public void createDeleteProperty() {
        FF4j ff4j = new FF4j();
        ff4j.createProperty(new PropertyString("p1", "v1"));
        ff4j.audit(true);
        ff4j.createProperty(new PropertyString("p2", "v2"));
        Assert.assertTrue(ff4j.getPropertiesStore().exists("p1"));
        ff4j.getPropertiesStore().delete("p1");
        Assert.assertFalse(ff4j.getPropertiesStore().exists("p1"));
    }
    
    @Test
    public void monitoringAudit() {
        // Given
        FF4j ff4j = new FF4j();
        ff4j.setEventPublisher(new EventPublisher());
        ff4j.setEventRepository(new FeatureUsageInMemory());
        // When
        ff4j.stop();
        
        // When
        ff4j.setEventPublisher(null);
        ff4j.getEventPublisher();
        ff4j.stop();
        
        // When
        Event evt = new EventBuilder().source(SOURCE_JAVA).feature("f2").action(ACTION_CHECK_OK).build();
        Assert.assertNotNull(evt.toJson());
        Assert.assertNotNull(evt.toString());
        
        // When
        EventPublisher ep = new EventPublisher();
        new EventPublisher(ep.getRepository(), null);
        ep.setRepository(new FeatureUsageInMemory());
        // Then
        Assert.requireNotNull(ep.getRepository());
    }
    
    @Test
    public void enableDisableGroups() {
        // Given
        FF4j ff4j = new FF4j();
        ff4j.audit(true);
        ff4j.setFeatureStore(new FeatureStoreInMemory());
        ff4j.createFeature(new Feature("f1").toggleOn());
        ff4j.createFeature(new Feature("f2"));
        ff4j.getFeatureStore().addToGroup("f1", "g1");
        ff4j.getFeatureStore().addToGroup("f2", "g1");
       
        // When
        ff4j.toggleOn("f1");
        ff4j.setFileName(null);
        // Then
        Assert.assertTrue(ff4j.getFeature("f1").isEnable());
    }
    
    @Test
    public void testReadCoreMetadata() {
        FF4j ff4j = new FF4j();
        ff4j.getVersion();
        Assert.assertNotNull(ff4j.getStartTime());
        Assert.assertNotNull(ff4j.getPropertiesStore());
    }
    
    @Test
    public void testToString() {
        FF4j ff4j = new FF4j("ff4j.xml");
        ff4j.toString();
        Assert.assertNotNull(ff4j.getFeatureStore());
        ff4j.setFeatureStore(null);
        ff4j.setPropertiesStore(null);
        ff4j.setEventRepository(null);
        ff4j.setEventPublisher(null);
        ff4j.setAuthorizationsManager(new DefinedPermissionSecurityManager(Util.setOf("val1")));
        ff4j.toString();
    }

    @Test
    public void helloWorldTest() {
        // Default : store = inMemory, load features (5) from ff4j.xml file
        assertEquals(5, ff4j.getFeatureStore().count());

        // Dynamically create feature and add it to the store (tests purpose)
        ff4j.createFeature(new Feature("sayHello"));

        // Enable Feature
        ff4j.toggleOn("sayHello");

        // Assertion
        assertTrue(ff4j.getFeatureStore().exists("sayHello"));
        assertEquals(6, ff4j.getFeatureStore().count());
        assertTrue(ff4j.check("sayHello"));
    }

    @Test
    public void autoCreateFeatureEnableTest() {

        // Default : store = inMemory, load features from ff4j.xml file
        FF4j ff4j = new FF4j("ff4j.xml").autoCreate(true);
        assertFalse(ff4j.getFeatureStore().exists("autoCreatedFeature"));

        // Auto creation by testing its value
        assertFalse(ff4j.check("autoCreatedFeature"));

        // Assertion
        assertTrue(ff4j.getFeatureStore().exists("autoCreatedFeature"));
    }

    @Test
    public void workingWithFeature() {
        // Initialize with empty store
        FF4j ff4j = new FF4j();

        // Dynamically register new features
        ff4j.createFeature(new Feature("f1")).toggleOn("f1");

        // Assertions
        assertTrue(ff4j.getFeatureStore().exists("f1"));
        assertTrue(ff4j.check("f1"));
    }
  
    // enabling...

    @Test
    public void testEnableFeature() {
        FF4j ff4j = new FF4j();
        ff4j.autoCreate(true);
        ff4j.toggleOn("newffff");
        Assert.assertTrue(ff4j.getFeatureStore().exists("newffff"));
        Assert.assertTrue(ff4j.check("newffff"));
    }

    @Test(expected = FeatureNotFoundException.class)
    public void testEnableFeatureNotExist() {
        ff4j.toggleOn("newffff");
    }

    // disabling...

    @Test
    public void testDisableFeature() {
        FF4j ff4j = new FF4j();
        ff4j.autoCreate(true);
        ff4j.toggleOff("newffff");
        Assert.assertTrue(ff4j.getFeatureStore().exists("newffff"));
        Assert.assertFalse(ff4j.check("newffff"));
    }

    @Test(expected = FeatureNotFoundException.class)
    public void testDisableFeatureNotExist() {
        FF4j ff4j = new FF4j();
        ff4j.toggleOff("newffff");
    }

    @Test
    public void testGetFeatures() {
        FF4j ff4j = new FF4j("ff4j.xml");
        Assert.assertEquals(5, ff4j.getFeatureStore().count());
    }

    @Test
    public void testFlipped() {
        FF4j ff4j = new FF4j().autoCreate(true)
                .createFeature(new Feature("coco").toggleOn()
                        .setGroup("grp2")
                        .setPermissions("ROLEA"));
        Assert.assertTrue(ff4j.check("coco"));
        ff4j.setAuthorizationsManager(mockAuthManager);
        Assert.assertTrue(ff4j.check("coco"));
        FF4jContext ex = new FF4jContext();
        ex.put("OK", "OK");
        Assert.assertTrue(ff4j.check("coco", ex));
        Assert.assertTrue(ff4j.checkOveridingStrategy("coco", mockFlipStrategy));
        Assert.assertTrue(ff4j.checkOveridingStrategy("coco", null, null));
        Assert.assertFalse(ff4j.checkOveridingStrategy("cocorico", mockFlipStrategy));
        // Update Coverage
        ff4j.setAuthManager("something");
    }
    
    @Test
    public void testOverrideStrategy() {
        FF4j ff4j = new FF4j().audit(true)
                    .createFeature(new Feature("N1").toggleOn().setDescription("description NEWS"))
                    .createFeature(new Feature("N2").toggleOff().setDescription("description NEWS"));
        
        Assert.assertTrue(ff4j.check("N1"));
        //Assert.assertFalse(ff4j.checkOveridingStrategy("N1", new ExpressionFlipStrategy("N1", "N1 & N2")));
    }
        
    @Test
    public void testToString2() {
        Assert.assertTrue(ff4j.toString().contains(FeatureStoreInMemory.class.getCanonicalName()));
    }
    
    @Test
    public void authorisationManager() {
        FF4j ff4j = new FF4j();
        ff4j.setAuthorizationsManager(new DefinedPermissionSecurityManager(null));
        ff4j.getAuthorizationsManager().toString();
        ff4j.createFeature(new Feature("f1"));
        ff4j.check("f1");
        
        ff4j.setAuthorizationsManager(new DefinedPermissionSecurityManager(new HashSet<String>()));
        ff4j.getAuthorizationsManager().toString();
        
        ff4j.setAuthorizationsManager(new DefinedPermissionSecurityManager(Util.setOf("S1", "S2")));
        ff4j.getAuthorizationsManager().toString();
    }
    
    @Test
    public void testAllowed() {
        FF4j ff4j = new FF4j();
        ff4j.setAuthorizationsManager(new DefinedPermissionSecurityManager(Util.setOf("USER")));
        Feature f1 = new Feature("f1").toggleOn().setPermissions("USER");
        ff4j.createFeature(f1);
        ff4j.check(f1.getUid());
        ff4j.isAllowed(f1);
    }

    @Test
    public void testImportFeatures() {
        FF4j ff4j = new FF4j();
        List < Feature > listOfFeatures = new ArrayList<Feature>();
        listOfFeatures.add(new Feature("f1").toggleOn().setPermissions("USER"));
        ff4j.importFeatures(listOfFeatures);
        Assert.assertTrue(ff4j.getFeatureStore().exists("f1"));
        
        // no Error
        ff4j.importFeatures(null);
    }
    
    @Test
    public void testImportProperties() {
        FF4j ff4j = new FF4j();
        List < Property<?> > listOfProperties = new ArrayList<Property<?>>();
        listOfProperties.add(new PropertyString("p1", "v1"));
        ff4j.importProperties(listOfProperties);
        Assert.assertTrue(ff4j.getPropertiesStore().exists("p1"));
        
        // no Error
        ff4j.importProperties(null);
    }
    
    @Test
    public void testInitWithEventPublisher() {
        Assert.requireNotNull(new FF4j().getEventPublisher());
    }
    
    @Test
    public void testEmptyPermission() {
        FF4j ff4j = new FF4j();
        ff4j.createFeature(new Feature("f1").toggleOn());
        ff4j.setAuthorizationsManager(new DefinedPermissionSecurityManager("a", new HashSet<String>()));
        Assert.assertTrue(ff4j.checkOveridingStrategy("f1", new PonderationStrategy(1d)));
        Assert.assertTrue(ff4j.isAllowed(ff4j.getFeature("f1")));
    }
    
    @Test
    public void testgetProperty() {
        FF4j ff4j = new FF4j();
        ff4j.createProperty(new PropertyString("p1", "v1"));
        Assert.assertNotNull(ff4j.getProperty("p1"));
        Assert.assertNotNull(ff4j.getProperty("p1").asString());
        Assert.assertEquals("v1", ff4j.getProperty("p1").asString());
    }
    
    @Test
    public void testParseXmlConfigOK() {
        Assert.assertNotNull(new FF4j().parseXmlConfig("test-featureXmlParserTest-ok.xml"));        
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testParseXmlConfigKO() {
        Assert.assertNotNull(new FF4j().parseXmlConfig("do-not-ext.xml"));        
    }
    
    @Test
    public void testInitCache() {
        FF4j ff4j = new FF4j();
        ff4j.cache(new CacheManagerInMemory<Feature>(), new CacheManagerInMemory<Property<?>>());;
    }

    @Test
    public void testInitAuditProxy() {
        FF4j ff4j = new FF4j();
        ff4j.setEnableAudit(true);
        ff4j.getFeatureStore();
        ff4j.setEnableAudit(false);
        ff4j.getFeatureStore();
    }
    
    @Test
    public void getConcreteFeatureStore() {
        FF4j ff4j = new FF4j();
        CacheManagerInMemory<Feature>     cmisF = new CacheManagerInMemory<Feature>();
        CacheManagerInMemory<Property<?>> cmisP = new CacheManagerInMemory<Property<?>>();
        ff4j.cache(cmisF, cmisP);
        Assert.assertNotNull(ff4j.getConcreteFeatureStore());
        Assert.assertNotNull(ff4j.getConcretePropertyStore());
        ff4j.setPropertiesStore(new PropertyStoreAuditProxy(ff4j, ff4j.getPropertiesStore()));
        Assert.assertNotNull(ff4j.getConcretePropertyStore());
    }
    
    @Test
    public void testCreateSchema() {
        FF4j ff4j = new FF4j();
        ff4j.createSchema();
        ff4j.setFeatureStore(null);
        ff4j.setPropertiesStore(null);
        ff4j.setEventRepository(null);
        // No error event with null elements
        ff4j.createSchema();
        Assert.assertNotNull(ff4j);
    }

}
