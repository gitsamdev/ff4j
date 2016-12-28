package org.ff4j.security;

import static org.ff4j.security.FF4jPermission.CREATE_FEATURE;
import static org.ff4j.security.FF4jPermission.CREATE_PROPERTY;
import static org.ff4j.security.FF4jPermission.DELETE_FEATURE;
import static org.ff4j.security.FF4jPermission.DELETE_PROPERTY;
import static org.ff4j.security.FF4jPermission.EDIT_PERMISSIONS;
import static org.ff4j.security.FF4jPermission.TOGGLE_FEATURE;
import static org.ff4j.security.FF4jPermission.UPDATE_FEATURE;
import static org.ff4j.security.FF4jPermission.UPDATE_PROPERTY;
import static org.ff4j.security.FF4jPermission.VIEW_AUDIT;
import static org.ff4j.security.FF4jPermission.USE;

import java.util.Set;

import org.ff4j.utils.Util;

/**
 * Prebuilt Role on the FF4J solution.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public enum FF4jProfile {
    
    // See status but cannot change
    VIEWER(VIEW_AUDIT),
    
    // Adding execution
    USER(VIEW_AUDIT, USE),
    
    // Update elements through console but cannot create
    EDITOR(VIEW_AUDIT, USE, TOGGLE_FEATURE, UPDATE_FEATURE, UPDATE_PROPERTY, TOGGLE_FEATURE),
 
    // Administrator for the features
    POWER_USER(VIEW_AUDIT, USE, TOGGLE_FEATURE, 
                  CREATE_FEATURE, UPDATE_FEATURE, DELETE_FEATURE, 
                  CREATE_PROPERTY, UPDATE_PROPERTY, DELETE_PROPERTY,
                  EDIT_PERMISSIONS),
    
    // Get all privileges
    ADMINISTRATOR(FF4jPermission.values());
    
    
    private FF4jPermission[] permissions = null;
    
    /**
     * Default constructor.
     *
     * @param perms
     *      target ff4j parameters
     */
    private FF4jProfile(FF4jPermission... perms) {
        this.permissions = perms;
    }
    
    /**
     * Permissions.
     *
     * @return
     */
    public Set < FF4jPermission > getPermissions() {
        return Util.setOf(permissions);
    }

}
