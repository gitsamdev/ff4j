package org.ff4j.security;

import java.util.HashSet;
import java.util.Set;

import org.ff4j.FF4jEntity;

/**
 * Represent a user in FF4J.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class FF4jUser extends FF4jEntity < FF4jUser > {
   
    /** serialVersionUID. */
    private static final long serialVersionUID = 7083552676589401961L;
    
    /** first Name. */
    private String firstName;
    
    /** Last Name. */
    private String lastName;
    
    /** Extra permissions if relevant. */
    private Set < FF4jPermission > permissions = new HashSet<>();
    
    /** User groups. */
    private Set < String > roles = new HashSet<>();
    
    /**
     * Create a user by its userName.
     *
     * @param uid
     *      user unique identifier
     */
    public FF4jUser(String uid) {
        super(uid);
    }
    
    /**
     * Getter accessor for attribute 'firstName'.
     *
     * @return
     *       current value of 'firstName'
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Setter accessor for attribute 'firstName'.
     * @param firstName
     * 		new value for 'firstName '
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Getter accessor for attribute 'lastName'.
     *
     * @return
     *       current value of 'lastName'
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Setter accessor for attribute 'lastName'.
     * @param lastName
     * 		new value for 'lastName '
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Getter accessor for attribute 'permissions'.
     *
     * @return
     *       current value of 'permissions'
     */
    public Set<FF4jPermission> getPermissions() {
        return permissions;
    }

    /**
     * Setter accessor for attribute 'permissions'.
     * @param permissions
     * 		new value for 'permissions '
     */
    public void setPermissions(Set<FF4jPermission> permissions) {
        this.permissions = permissions;
    }

    /**
     * Getter accessor for attribute 'roles'.
     *
     * @return
     *       current value of 'roles'
     */
    public Set<String> getRoles() {
        return roles;
    }

    /**
     * Setter accessor for attribute 'roles'.
     * @param roles
     * 		new value for 'roles '
     */
    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
    
}
