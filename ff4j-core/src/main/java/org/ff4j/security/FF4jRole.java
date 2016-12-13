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

import java.util.Set;

import org.ff4j.utils.Util;

/**
 * Prebuilt Role on the FF4J solution.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public enum FF4jRole {
    
    // Get all privileges
    ADMINISTRATOR(FF4jPermission.values()),
    
    // Administrator for the features
    FEATURES_ADMINISTRATOR(CREATE_FEATURE, UPDATE_FEATURE, DELETE_FEATURE, TOGGLE_FEATURE, EDIT_PERMISSIONS),
    
    // Administrator for the properties
    PROPERTIES_ADMINISTRATOR(CREATE_PROPERTY, UPDATE_PROPERTY, DELETE_PROPERTY, EDIT_PERMISSIONS),
    
    // Update elements through console but cannot create
    SUPER_USER(UPDATE_FEATURE, UPDATE_PROPERTY, TOGGLE_FEATURE),
    
    // See status but cannot change
    VIEWER(VIEW_AUDIT);
    
    private FF4jPermission[] permissions = null;
    
    /**
     * Default constructor.
     *
     * @param perms
     *      target ff4j parameters
     */
    private FF4jRole(FF4jPermission... perms) {
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
