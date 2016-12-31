package org.ff4j.security;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.ff4j.utils.JsonUtils;

/**
 * Wrapper to manipulate grantees for permissions.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class FF4jGrantees {
    
    /** Usernames (unique identifier) for users. */
    private Set < String > users = new HashSet<>();

    /** Roles Names. */
    private Set < String > groups = new HashSet<>();
    
    /**
     * Default constructor
     */
    public FF4jGrantees() {
    }
    
    /**
     * Constructor will all parameters.
     *
     * @param groups
     *      target group name
     * @param users
     *      target user names
     */
    public FF4jGrantees(Set <String> users, Set < String > groups) {
        this.users  = users;
        this.groups = groups;
    }
    
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toJson();
    }
    
    /** {@inheritDoc} */
    public String toJson() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"users\":");
        sb.append(JsonUtils.collectionAsJson(users));
        sb.append(",\"groups\":");
        sb.append(JsonUtils.collectionAsJson(groups));
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Add dedicated user.
     *
     * @param group
     *      target group
     * @return
     */
    public FF4jGrantees addUser(String user) {
        users.add(user);
        return this;
    }
    
    /**
     * Add dedicated group.
     *
     * @param group
     *      target group
     * @return
     */
    public FF4jGrantees addGroup(String group) {
        groups.add(group);
        return this;
    }
    
    /**
     * Group is granted.
     *
     * @param user
     *      current user
     * @return
     *      if the user if part of the grantees
     */
    public boolean isGroupGranted(String groupName) {
        return groups.contains(groupName);
    }
    
    /**
     * User is granted specifically or is member of a specialized group.
     *
     * @param user
     *      current user
     * @return
     *      if the user if part of the grantees
     */
    public boolean isUserGranted(String userName) {
        return users.contains(userName);
    }
    
    /**
     * User is granted specifically or is member of a specialized group.
     *
     * @param user
     *      current user
     * @return
     *      if the user if part of the grantees
     */
    public boolean isUserGranted(FF4jUser user) {
        return users.contains(user.getUid()) ? true :
            !groups.stream()
                    .filter(user.getGroups()::contains)
                    .collect(Collectors.toList())
                    .isEmpty();
    }

    /**
     * Getter accessor for attribute 'users'.
     *
     * @return
     *       current value of 'users'
     */
    public Set<String> getUsers() {
        return users;
    }

    /**
     * Setter accessor for attribute 'users'.
     * @param users
     * 		new value for 'users '
     */
    public void setUsers(Set<String> users) {
        this.users = users;
    }

    /**
     * Getter accessor for attribute 'groups'.
     *
     * @return
     *       current value of 'groups'
     */
    public Set<String> getGroups() {
        return groups;
    }

    /**
     * Setter accessor for attribute 'groups'.
     * @param groups
     * 		new value for 'groups '
     */
    public void setGroups(Set<String> groups) {
        this.groups = groups;
    }
    
}
