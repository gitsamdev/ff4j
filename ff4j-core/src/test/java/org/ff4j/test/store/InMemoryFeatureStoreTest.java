package org.ff4j.test.store;

/*
 * #%L ff4j-core $Id:$ $HeadURL:$ %% Copyright (C) 2013 Ff4J %% Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. #L%
 */

import java.util.LinkedHashMap;

import org.ff4j.feature.Feature;
import org.ff4j.feature.FeatureStore;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.strategy.PonderationStrategy;
import org.junit.Assert;
import org.junit.Test;

/**
 * All TEST LOGIC is in super class to be processed on EACH STORE.
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class InMemoryFeatureStoreTest extends CoreFeatureStoreTestSupport {

    /** {@inheritDoc} */
    @Override
    public FeatureStore initStore() {
        FeatureStoreInMemory imfs = new FeatureStoreInMemory();
        imfs.setLocation("ff4j.xml");
        return imfs;
    }

    @Test
    public void testUnitFeatureInitialization() {
        FeatureStoreInMemory imfs = new FeatureStoreInMemory();
        imfs.create(new Feature("default")
                .toggleOn().setGroup("grp1")
                .setDescription("desc")
                .setFlippingStrategy(new PonderationStrategy()));
        Assert.assertEquals(1, imfs.findAll().count());
    }

    @Test
    public void testUnitFeatureInitialization2() {
        LinkedHashMap<String, Feature> map1 = new LinkedHashMap<String, Feature>();
        map1.put("new", new Feature("new").toggleOn().setDescription("description"));
        map1.put("old", new Feature("old").toggleOn().setDescription("description"));
        FeatureStoreInMemory imfs = new FeatureStoreInMemory(map1.values());
        Assert.assertEquals(2, imfs.findAll().count());
        Assert.assertNotNull(imfs.findById("old"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testUnitFeatureInitialization3() {
        new FeatureStoreInMemory("invalid.xml");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUnitFeatureInitialization5() {
        new FeatureStoreInMemory((String) null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUnitFeatureInitialization6() {
        new FeatureStoreInMemory("");
    }
    
    @Test
    public void testUnitFeatureInitialization4() {
        FeatureStoreInMemory f = new FeatureStoreInMemory();
        f.toJson();
        f.toString();
        f.getFileName();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDonotImportEmpty() {
        FeatureStoreInMemory f = new FeatureStoreInMemory();
        f.loadConfFile("");
    }
    
    
    @Test(expected = IllegalArgumentException.class)
    public void testDonotImportNull() {
        FeatureStoreInMemory f = new FeatureStoreInMemory();
        f.loadConfFile(null);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testDonotImportInvalid() {
        FeatureStoreInMemory f = new FeatureStoreInMemory();
        f.loadConfFile("invalid.xml");
    }
    
    @Test
    public void testImportTwice() {
        FeatureStoreInMemory f = new FeatureStoreInMemory();
        f.loadConfFile("ff4j.xml");
        f.loadConfFile("ff4j.xml");
        Assert.assertFalse(f.findAll().count() == 0);
    }
    
}
