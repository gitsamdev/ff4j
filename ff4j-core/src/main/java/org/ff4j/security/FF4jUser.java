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
    
    /** Default permissions. */
    private FF4jProfile profile = FF4jProfile.USER;
    
    /** Extra permissions if relevant. */
    private Set < FF4jPermission > extraPermissions = new HashSet<>();
    
    /** User groups. */
    private Set < String > groups = new HashSet<>();
    
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
     * Getter accessor for attribute 'profile'.
     *
     * @return
     *       current value of 'profile'
     */
    public FF4jProfile getProfile() {
        return profile;
    }

    /**
     * Setter accessor for attribute 'profile'.
     * @param profile
     * 		new value for 'profile '
     */
    public void setProfile(FF4jProfile profile) {
        this.profile = profile;
    }

    /**
     * Getter accessor for attribute 'extraPermissions'.
     *
     * @return
     *       current value of 'extraPermissions'
     */
    public Set<FF4jPermission> getExtraPermissions() {
        return extraPermissions;
    }

    /**
     * Setter accessor for attribute 'extraPermissions'.
     * @param extraPermissions
     * 		new value for 'extraPermissions '
     */
    public void setExtraPermissions(Set<FF4jPermission> extraPermissions) {
        this.extraPermissions = extraPermissions;
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
