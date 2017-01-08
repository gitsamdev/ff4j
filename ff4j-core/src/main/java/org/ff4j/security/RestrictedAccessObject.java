package org.ff4j.security;

import org.ff4j.utils.Util;

/**
 * Entities protected by permissions should implement this interface.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public interface RestrictedAccessObject {
    
    /** Get Access to control List of this object. */
    AccessControlList getAccessControlList();
    
    default void grantUser(String userName, FF4jPermission... perm) {
        AccessControlList acl = getAccessControlList();
        Util.requireNotNull(acl);
        acl.grantUser(userName, perm);
    }
    
    default void grantUsers(FF4jPermission perm, String... users) {
        AccessControlList acl = getAccessControlList();
        Util.requireNotNull(acl);
        acl.grantUsers(perm, users);
    }
    
    default void grantRoles(FF4jPermission perm, String... roles) {
        AccessControlList acl = getAccessControlList();
        Util.requireNotNull(acl);
        acl.grantRoles(perm, roles);
    }
   
    default boolean isUserGranted(String userName, FF4jPermission perm) {
        AccessControlList acl = getAccessControlList();
        Util.requireNotNull(acl);
        return acl.isUserGranted(userName, perm);
    }
    
    default boolean isUserGranted(FF4jUser user, FF4jPermission perm) {
        AccessControlList acl = getAccessControlList();
        Util.requireNotNull(acl);
        return acl.isUserGranted(user, perm);
    }
    
    default boolean isRoleGranted(String roleName, FF4jPermission perm) {
        AccessControlList acl = getAccessControlList();
        Util.requireNotNull(acl);
        return acl.isRoleGranted(roleName, perm);
    }
    
}
