package org.ff4j.security;

import java.util.Optional;

import org.ff4j.utils.Util;

public interface RestrictedAccessObject {
    
    Optional < AccessControlList > getAccessControlList();
    
    default boolean canExecuteFeature(FF4jUser user) {
        return isUserGranted(user, FF4jPermission.EXECUTE_FEATURE);
    }
    
    default void grantUser(String userName, FF4jPermission... perm) {
        getAccessControlList().ifPresent(acl -> acl.grantUser(userName, perm));
    }
    
    default void grantUsers(FF4jPermission perm, String... users) {
        getAccessControlList().ifPresent(acl -> acl.grantUsers(perm, users));
    }
    
    default void grantGroups(FF4jPermission perm, String... groups) {
        getAccessControlList().ifPresent(acl -> acl.grantGroups(perm, groups));
    }
   
    default boolean isUserGranted(String userName, FF4jPermission perm) {
        Util.assertNotNull(userName);
        Optional <AccessControlList> acl = getAccessControlList();
        if (acl.isPresent()) {
            return acl.get().isUserGranted(userName, perm);
        }
        return true;
    }
    
    default boolean isUserGranted(FF4jUser user, FF4jPermission perm) {
        Util.assertNotNull(user);
        Optional <AccessControlList> acl = getAccessControlList();
        if (acl.isPresent()) {
            return acl.get().isUserGranted(user, perm);
        }
        return true;
    }
    
    default boolean isGroupGranted(String groupName, FF4jPermission perm) {
        Util.assertNotNull(groupName);
        Optional <AccessControlList> acl = getAccessControlList();
        if (acl.isPresent()) {
            return acl.get().isGroupGranted(groupName, perm);
        }
        return true;
    }

}
