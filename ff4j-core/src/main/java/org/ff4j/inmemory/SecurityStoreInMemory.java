package org.ff4j.inmemory;

import java.util.HashMap;
import java.util.Map;

import org.ff4j.security.AccessControlList;
import org.ff4j.security.AccessControlListStore;
import org.ff4j.utils.Util;

/**
 * Default implementation of {@link AccessControlListStore} to work with an inmemory map.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class SecurityStoreInMemory implements AccessControlListStore {
    
    /** Holder for different {@link AccessControlList}. */
    private Map < String , AccessControlList > mapOfAcl = new HashMap<>();
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        // Nothing to do easy
    }

    /** {@inheritDoc} */
    @Override
    public AccessControlList getAccessControlList(String targetUid) {
        Util.requireHasLength(targetUid);
        if (!mapOfAcl.containsKey(targetUid)) {
            // Never return null
            mapOfAcl.put(targetUid, new AccessControlList());
        }
        return mapOfAcl.get(targetUid);
    }

    /** {@inheritDoc} */
    @Override
    public void saveAccessControlList(AccessControlList acl, String targetUid) {
    }

}
