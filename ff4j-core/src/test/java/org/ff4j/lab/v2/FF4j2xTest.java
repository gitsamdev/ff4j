package org.ff4j.lab.v2;

import org.ff4j.FF4j;
import org.ff4j.feature.Feature;
import org.ff4j.feature.FeatureStoreListenerAudit;
import org.ff4j.security.FF4jPermission;
import org.junit.Assert;
import org.junit.Test;

public class FF4j2xTest {
    
    @Test
    public void testInMemory() throws InterruptedException {
        
        // Given
        FF4j ff4j = new FF4j("ff4j.xml");
        
        // Feature Usage
        ff4j.enableFeatureUsageTracking();
        ff4j.registerListener("FeatureUsageConsole", 
                new FeatureUsageLogger());
        
        // Audit for Features
        ff4j.enableAuditTrail();
        ff4j.getFeatureStore().registerListener("AuditTrailConsole", 
                new FeatureStoreListenerAudit(new AuditTrailLogger()));
        
        // Security
        ff4j.grantUsers(FF4jPermission.ADMIN_FEATURES, "Pierre");
        ff4j.saveAccessControlList();
        
        // Feature
        Feature fx = new Feature("fx").setGroup("G1").toggleOn();
        fx.grantUsers(FF4jPermission.TOGGLE_FEATURE, "Pierre", "Paul", "Jacques");
        ff4j.createFeature(fx);
        
        // expect to fire event to feature usage
        Assert.assertTrue(ff4j.check("fx"));
        
        // Update Feature
        // ff4j.getFeature("fx").getAccessControlList().get().
        
        // Async
        Thread.sleep(2000);
    }

}
