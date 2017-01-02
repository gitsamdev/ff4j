package org.ff4j.security;

/**
 * Access Control List on FF4j Capabilities.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public enum FF4jPermission {
    
    // (ff4j) Can edit all features independently of any ACL on the entities
    ADMIN_FEATURES,
    
    // (ff4j) Can edit all features independently of any ACL on the entities
    ADMIN_PROPERTIES,
    
    // Can access and search audit
    VIEW_AUDITTRAIL,

    // Can access and search audit
    VIEW_FEATUREUSAGE,
    
    // --------------- FEATURE -----------------------------------------------
    
    // (feature) Can edit feature (CREATE, DELETE, UPDATE, TOGGLE)
    ADMIN_FEATURE,
    
    // (feature) You cannot edit feature but toggleOn/Off. (Business Users)
    TOGGLE_FEATURE,
    
    // (feature) You can use this feature if has correct roles
    EXECUTE_FEATURE,

    // --------------- PROPERTY -----------------------------------------------
    
    // (property) Can edit feature (CREATE, DELETE, UPDATE, TOGGLE)
    ADMIN_PROPERTY,
    
    // You can edit properties
    READ_PROPERTY,
    
}
