package org.ff4j.security;

/**
 * Access Control List on FF4j Capabilities.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public enum FF4jPermission {
    
    // You can use this feature
    USE,
    
    // You can create your features
    CREATE_FEATURE,
    
    // You can edit features
    UPDATE_FEATURE,
    
    // You can delete features
    DELETE_FEATURE, 
    
    // You can only connect and toggle
    TOGGLE_FEATURE,
    
    // You can create a property
    CREATE_PROPERTY,
    
    // You can edit properties
    UPDATE_PROPERTY,
    
    // You can delete properties
    DELETE_PROPERTY,
    
    // You can change the privileges
    EDIT_PERMISSIONS,
    
    // Can access and search audit
    VIEW_AUDIT;
}
