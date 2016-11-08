package org.ff4j.test.security;

import org.ff4j.utils.FF4jUtils;
import org.junit.Assert;
import org.junit.Test;

public class DefaultAuthorisationTest {
    
    @Test
    public void testDefaultSecurityManager() {
        DefaultAuthorisationManager am = new DefaultAuthorisationManager();
        am.setAllPermissions(FF4jUtils.setOf("a","b","c"));
        am.setCurrentUserPermissions(FF4jUtils.setOf("b","c"));
        Assert.assertNotNull(am.getCurrentUserPermissions());
        Assert.assertNotNull(am.listAllPermissions());   
    }
    
    @Test
    public void testDefaultSecurityManagerBis() {
        DefaultAuthorisationManager am = 
                new DefaultAuthorisationManager(FF4jUtils.setOf("b","c"), FF4jUtils.setOf("a","b","c"));
        Assert.assertNotNull(am.getCurrentUserPermissions());
        Assert.assertNotNull(am.listAllPermissions());
        Assert.assertNotNull(am.toJson());
    }

}
