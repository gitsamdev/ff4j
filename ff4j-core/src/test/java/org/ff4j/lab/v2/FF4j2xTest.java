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
        ff4j.enableAuditTrail();
        ff4j.enableFeatureUsageTracking();
        
        AuditTrailLogger atl = new AuditTrailLogger();
        
        // Register Listener to log operation on the featureStore
        ff4j.registerListener("FeatureUsageConsole", new FeatureUsageLogger());
        ff4j.getFeatureStore().registerListener("AuditTrailConsole", new FeatureStoreListenerAudit(atl));
        
        Feature fx = new Feature("fx").setGroup("G1").toggleOn();
        fx.grantUsers(FF4jPermission.USE, "Pierre", "Paul", "Jacques");
        
        // expect to fire event to auditTrails
        ff4j.createFeature(fx);
        
        // expect to fire event to feature usage
        Assert.assertTrue(ff4j.check("fx"));
        
        // Async
        Thread.sleep(2000);
    }

}
