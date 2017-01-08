package org.ff4j.security;

/**
 * Operations related to security in FF4j. This class will work with ff4j itself but also
 * the different stores, the audittrail or the Administration Servlet.
 *  
 * @author Cedrick LUNVEN  (@clunven)
 */
public interface AccessControlListStore {
    
    /**
     * Create Tables related to security.
     */
    void createSchema();
    
    /**
     * Access Control list of Target.
     *
     * @return
     *      get the list of permission
     */
    AccessControlList getAccessControlList(String targetUid);
    
    /**
     * Will save access control list to the DB.
     *
     * @param acl
     *      {@link AccessControlList} to be saved
     * @param entityType
     *      relevant entity type (store, ff4j)
     * @param entityUid
     *      
     */
    void saveAccessControlList(AccessControlList acl, String targetUid);
        
}
