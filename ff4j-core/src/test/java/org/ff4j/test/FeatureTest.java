package org.ff4j.test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.ff4j.feature.Feature;
import org.ff4j.feature.FlippingExecutionContext;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyString;
import org.ff4j.strategy.PonderationStrategy;
import org.ff4j.utils.FF4jUtils;
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
        Assert.assertNotNull(empty.getPermissions());

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
        f.addCustomProperties(new PropertyString("p2","v1", FF4jUtils.setOf("v1", "v2")));
        
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
        
        FlippingExecutionContext fec = new FlippingExecutionContext(parameters);
        fec.getDouble("a");
        fec.getInt("b");
        fec.getBoolean("c");
        fec.getDate("d");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext2() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FlippingExecutionContext fec = new FlippingExecutionContext();
        parameters.put("b", new Double(1));        
        fec.getInt("b");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext3() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FlippingExecutionContext fec = new FlippingExecutionContext();
        parameters.put("b", new Integer(1));        
        fec.getDouble("b");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext4() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FlippingExecutionContext fec = new FlippingExecutionContext();
        parameters.put("b", new Integer(1));        
        fec.getDate("b");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext5() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FlippingExecutionContext fec = new FlippingExecutionContext();
        parameters.put("b", new Integer(1));        
        fec.getBoolean("b");
        
        fec.getValue("DONOT", true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext6() {
        FlippingExecutionContext fec = new FlippingExecutionContext();
        fec.getValue("DONOT", true);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testFlipExecContext7() {
        Map < String, Object > parameters = new HashMap<String, Object>();
        FlippingExecutionContext fec = new FlippingExecutionContext();
        parameters.put("b", new Integer(1));        
        fec.getString("b");
    }
    
    @Test
    public void testFlipExecContext8() {        
        FlippingExecutionContext fec = new FlippingExecutionContext();
        fec.putBoolean("a", new Boolean(true));
        fec.putDate("b", new Date());
        fec.putDate("c", new Date());
        fec.putInt("d", new Integer(1));
        fec.putDouble("e", new Double(1D));
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

