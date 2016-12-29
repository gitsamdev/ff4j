package org.ff4j.feature;

import org.ff4j.FF4jEntity;
import org.ff4j.store.FF4jRepositoryListener;

/**
 * Public Interface of a Listener on CRUD repository.
 * @Do not put any onRead() as making not sense in ff4J.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <ENTITY>
 *    {@link FF4jEntity} to be specialized by type of store 
 */
public interface FeatureStoreListener extends FF4jRepositoryListener< Feature > {
    
    void onToggleOnFeature(String uid);
    
    void onToggleOffFeature(String uid);
    
    void onToggleOnGroup(String groupName);
    
    void onToggleOffGroup(String groupname);
    
    void onAddFeatureToGroup(String uid, String groupName);
    
    void onRemoveFeatureFromGroup(String uid, String groupName);
    
}
