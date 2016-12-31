package org.ff4j.test;

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
import java.util.HashMap;
import java.util.Map;

import org.ff4j.FF4jContext;
import org.ff4j.exception.ItemNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.property.Property;
import org.ff4j.property.domain.PropertyString;
import org.ff4j.strategy.PonderationStrategy;
import org.ff4j.utils.Util;
import org.junit.Assert;
import org.junit.Test;

/**
 * Testing Bean {@link Feature} initialization.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class FeatureTest {
   
    @Test
    public void testBuildFromScratchFeature() {
        Feature empty = new Feature("abc");

        // Flipping strategy
        empty.setFlippingStrategy(new PonderationStrategy());
        Assert.assertNotNull(empty.getFlippingStrategy());

        // Authorization filling
        empty.setPermissions("something");
        Assert.requireNotNull(empty.getPermissions());

        // Description setter
        empty.setDescription("OK");
        Assert.assertNotNull(empty.getDescription());

        // Toggle to change value
        empty.toggleOff();
        Assert.assertFalse(empty.isEnable());
        
        // GROUP
        empty.setGroup("sampleGroup");
        Assert.assertFalse(empty.getGroup() == null);

        // To String with a whole object
        Assert.assertTrue(empty.toString().contains("OK"));
    }
    
    @Test
    public void testCopyConstructorFeature() {
        Feature f = new Feature("abc").toggleOn()
                        .setDescription("samething")
                        .setGroup("groupA").setPermissions("a", "b");
        
        f.addPermissions("USER");
        f.setFlippingStrategy(new PonderationStrategy(0.5d));
        f.addCustomProperties(new PropertyString("p1","v1"));
        f.addCustomProperties(new PropertyString("p2","v1", Util.setOf("v1", "v2")));
        
        Feature f2 = new Feature(f);
        Assert.assertEquals(f2.getUid(),  f.getUid());
        Assert.assertEquals(f2.getPermissions(),  f.getPermissions());
        new Feature(new Feature("f4").toggleOn());
    }
    
    @Test
    public void testProperty() {
        Feature f = new Feature("f1");
        f.toggleOn();
        f.toggleOff();
        f.addCustomProperties(new PropertyString("p1","v1"));
        f.getCustomProperty("p1");
    }
    
    @Test
    public void testFlipExecContext() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        parameters.put("a", new Double(1D));        
        parameters.put("b", new Integer(1));        
        parameters.put("c", new Boolean(true));        
        parameters.put("d", new Date());        
        
        FF4jContext fec = new FF4jContext(parameters);
        fec.getDouble("a");
        fec.getInt("b");
        fec.getBoolean("c");
        fec.getDate("d");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext2() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FF4jContext fec = new FF4jContext();
        parameters.put("b", new Double(1));        
        fec.getInt("b");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext3() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FF4jContext fec = new FF4jContext();
        parameters.put("b", new Integer(1));        
        fec.getDouble("b");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext4() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FF4jContext fec = new FF4jContext();
        parameters.put("b", new Integer(1));        
        fec.getDate("b");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext5() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FF4jContext fec = new FF4jContext();
        parameters.put("b", new Integer(1));        
        fec.getBoolean("b");
        
        fec.getValue("DONOT", true);
    }
    
    @Test(expected = ItemNotFoundException.class)
    public void testFlipExecContext6() {
        FF4jContext fec = new FF4jContext();
        fec.getValue("DONOT", true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext7() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FF4jContext fec = new FF4jContext();
        parameters.put("b", new Integer(1));        
        fec.getString("b");
    }
    
    @Test
    public void testFlipExecContext8() {        
        FF4jContext fec = new FF4jContext();
        fec.put("a", new Boolean(true));
        fec.put("b", new Date());
        fec.put("c", new Date());
        fec.put("d", new Integer(1));
        fec.put("e", new Double(1D));
    }
    
    @Test
    public void testAddPropertyShouldAdd() {
        // Given
        Feature feat = new Feature("abc").toggleOn();
        Assert.assertFalse(feat.getCustomProperty("p1").isPresent());
        // When
        feat.addCustomProperties(new PropertyString("p1", "v1"));
        // Then
        Assert.assertTrue(feat.getCustomProperties().get().containsKey("p1"));
    }
    
    @Test
    public void testAddPropertyWithNullCustomPropertiesIsOK() {
        // Given
        Feature feat = new Feature("abc").toggleOn();
        feat.setCustomProperties((Map<String, Property<?>>)null);
        // When
        feat.addCustomProperties(new PropertyString("p1", "v1"));
        // Then
        Assert.assertTrue(feat.getCustomProperties().get().containsKey("p1"));
    }
}

