package org.ff4j.security;

import static org.ff4j.utils.JsonUtils.valueAsJson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.ff4j.utils.Util;

/**
 * Implementation of security mechanism.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 */
public class AccessControlList implements Serializable {

    /** serialVersionUID. */
    private static final long serialVersionUID = -7242564334708978726L;
    
    /** Permission : by Default everyOne can use the Feature. */
    private Map < FF4jPermission, FF4jGrantees > permissions = new HashMap<>();

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toJson();
    }
    
    /**
     * If the permissions map is empty;
     *
     * @return
     *      if the permission is empty.
     */
    public boolean isEmpty() {
        return permissions.isEmpty();
    }
    
    void grantUser(String userName, FF4jPermission... perms) {
        Util.requireHasLength(userName);
        Util.requireNotNull(perms);
        Stream.of(perms).forEach(perm -> {
            if (!permissions.containsKey(perm)) {
                permissions.put(perm, new FF4jGrantees());
            }
            permissions.get(perm).grantUser(userName);
        });
    }
    
    void revokeUser(String userName, FF4jPermission... perms) {
        Util.requireHasLength(userName);
        Util.requireNotNull(perms);
        Stream.of(perms).filter(permissions::containsKey)
                        .map(permissions::get)
                        .forEach(grantee -> grantee.revokeUser(userName));
    }
    
    void grantRole(String roleName, FF4jPermission... perms) {
        Util.requireHasLength(roleName);
        Util.requireNotNull(perms);
        Stream.of(perms).forEach(perm -> {
            if (!permissions.containsKey(perm)) {
                permissions.put(perm, new FF4jGrantees());
            }
            permissions.get(perm).grantRole(roleName);
        });
    }
    
    void revokeRole(String roleName, FF4jPermission... perms) {
        Util.requireHasLength(roleName);
        Util.requireNotNull(perms);
        Stream.of(perms).filter(permissions::containsKey)
                        .map(permissions::get)
                        .forEach(grantee -> grantee.revokeRole(roleName));
    }
    
    /**
     * Grant a permission for several users.
     *
     * @param permission
     *      permission or right to set
     * @param users
     *      list of users
     */
    public void grantUsers(FF4jPermission permission, String... users)  {
        Util.requireNotNull(permission);
        Util.requireNotNull((Object[]) users);
        Stream.of(users).forEach(user -> grantUser(user, permission));
    }
    
    /**
     * Grant a set of groupNames to the permission.
     *
     * @param permission
     *          the right to work with
     * @param roles
     *          the groups to allow on this permission
     */
    public void grantRoles(FF4jPermission permission, String... roles)  {
        Util.requireNotNull(permission);
        Util.requireNotNull((Object[]) roles);
        Stream.of(roles).forEach(user -> grantRole(user, permission));
    } 
    
    /**
     * Check if a userName is allow to use it (no groups).
     *
     * @param userName
     * @param permission
     * @return
     */
    public boolean isRoleGranted(String roleName, FF4jPermission permission) {
        Util.requireHasLength(roleName);
        Util.requireNotNull(permission);
        return permissions.containsKey(permission) ? permissions.get(permission).isRoleGranted(roleName) : false;
    }
    
    /**
     * Check if a userName is allow to use it (no groups).
     *
     * @param userName
     * @param permission
     * @return
     */
    public boolean isUserGranted(String userName, FF4jPermission permission) {
        Util.requireHasLength(userName);
        Util.requireNotNull(permission);
        return permissions.containsKey(permission) ? permissions.get(permission).isUserGranted(userName) : false;
    }
    
    /**
     * Check if a userName.
     *
     * @param userName
     *      current userName
     * @param permission
     *      expected permission
     * @return
     *      if user is granted
     */
    public boolean isUserGranted(FF4jUser user, FF4jPermission permission) {
        Util.requireNotNull(user);
        Util.requireNotNull(permission);
        return permissions.containsKey(permission) ? permissions.get(permission).isUserGranted(user) : false;
    }
    
    /**
     * Generate Json expression of rights.
     *
     * @return
     *      list of permissions
     */
    public String toJson() {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry < FF4jPermission, FF4jGrantees > mapEntry : permissions.entrySet()) {
            json.append(first ? "" : ",");
            json.append("\"" + mapEntry.getKey() + "\":");
            json.append(valueAsJson(mapEntry.getValue()));
            first = false;
        }
        json.append("}");
        return json.toString();
    }
    
}
