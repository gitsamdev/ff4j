package org.ff4j.security;

import static org.ff4j.utils.JsonUtils.valueAsJson;

import java.io.Serializable;
import java.util.Arrays;
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
    
    void grantUser(String userName, FF4jPermission... perms) {
        Util.assertNotNull(userName);
        Stream.of(perms).forEach(perm -> {
            if (!permissions.containsKey(perm)) {
                permissions.put(perm, new FF4jGrantees());
            }
            permissions.get(perm).addUser(userName);
        });
    }
    
    void grantGroup(String groupName, FF4jPermission... perms) {
        Util.assertNotNull(groupName);
        Stream.of(perms).forEach(perm -> {
            if (!permissions.containsKey(perm)) {
                permissions.put(perm, new FF4jGrantees());
            }
            permissions.get(perm).addGroup(groupName);
        });
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
        Util.assertNotNull(permission);
        if (permissions.containsKey(permission)) {
            permissions.put(permission, new FF4jGrantees());
        }
        permissions.get(permission).getUsers().addAll(Arrays.asList(users));
    }
    
    /**
     * Grant a set of groupNames to the permission.
     *
     * @param permission
     *          the right to work with
     * @param groups
     *          the groups to allow on this permission
     */
    public void grantGroups(FF4jPermission permission, String... groups)  {
        Util.assertNotNull(permission);
        if (permissions.containsKey(permission)) {
            permissions.put(permission, new FF4jGrantees());
        }
        permissions.get(permission).getGroups().addAll(Arrays.asList(groups));
    } 
    
    /**
     * Check if a userName is allow to use it (no groups).
     *
     * @param userName
     * @param permission
     * @return
     */
    public boolean isGroupGranted(String groupName, FF4jPermission permission) {
        return permissions.containsKey(permission) ? permissions.get(permission).isGroupGranted(groupName) : false;
    }
    
    /**
     * Check if a userName is allow to use it (no groups).
     *
     * @param userName
     * @param permission
     * @return
     */
    public boolean isUserGranted(String userName, FF4jPermission permission) {
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
