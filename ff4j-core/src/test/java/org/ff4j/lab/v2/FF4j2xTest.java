package org.ff4j.lab.v2;

import org.ff4j.FF4j;
import org.ff4j.feature.Feature;
import org.ff4j.security.FF4jPermission;
import org.junit.Assert;
import org.junit.Test;

public class FF4j2xTest {
    
    @Test
    public void testInMemory() throws InterruptedException {
        // Given
        FF4j ff4j = new FF4j("ff4j.xml");
        
        // (NEW!) Register listener to get Hits
        ff4j.registerListener("FeatureUsage", new FeatureUsageLogger());
        // Register Listener to log operation on the featureStore
        ff4j.getFeatureStore().registerListener("AuditTrailFeature",        new AuditTrailFeatureLogger());
        ff4j.getPropertiesStore().registerListener("AuditTrailProperties",  new AuditTrailPropertyLogger());
        
        Feature fx = new Feature("fx").setGroup("G1").toggleOn();
        fx.grantUsers(FF4jPermission.USE, "Pierre", "Paul", "Jacques");
        ff4j.createFeature(fx);
        // Fire event auditTrail
        
        Assert.assertTrue(ff4j.check("fx"));
        // FireEvent FeatureIsage
        Thread.sleep(2000);
    }

}
