package org.ff4j.security;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Wrapper to manipulate grantees for permissions.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class FF4jGrantees {
    
    /** Usernames (unique identifier) for users. */
    private Set < String > users = new HashSet<>();

    /** GroupNames. */
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
    public FF4jGrantees(Set <String> groups, Set < String > users) {
        this.users  = users;
        this.groups = groups;
    }
    
    /**
     * Add dedicated group.
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
     * User is granted specifically or is member of a specialized group.
     *
     * @param user
     *      current user
     * @return
     *      if the user if part of the grantees
     */
    public boolean isUserGranted(FF4jUser user) {
        return users.contains(user.getUid()) ? true : !groups.stream()
                    .filter(user.getGroups()::contains)
                    .collect(Collectors.toList())
                    .isEmpty();
    }

}
