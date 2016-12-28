package org.ff4j.lab;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.ff4j.feature.Feature;
import org.ff4j.security.FF4jPermission;
import org.ff4j.store.AbstractFeatureStore;
import org.ff4j.store.PropertyStore;
import org.hsqldb.rights.Grantee;

/**
 * Leverage on {@link PropertyStore} to handle features. Feature will
 * be represented through its JSON representation.
 *  
 * @author Cedrick LUNVEN (@clunven)
 */
public class FeatureStoreOverPropertyStore extends AbstractFeatureStore {
    
    /** serialVersionUID. */
    private static final long serialVersionUID = -1866729081216457698L;
    
    /** Emebbeded property store. */
    private PropertyStore propertyStore = null;
    
    /**
     * Default public constructor.
     */
    public FeatureStoreOverPropertyStore() {
    }
    
    /**
     * Recopy constructor
     *
     * @param pStore
     *      target store
     */
    public FeatureStoreOverPropertyStore(PropertyStore pStore) {
        this.propertyStore = pStore;
    }

    /**
     * Getter accessor for attribute 'propertyStore'.
     *
     * @return
     *       current value of 'propertyStore'
     */
    public PropertyStore getPropertyStore() {
        if (this.propertyStore == null) {
            throw new IllegalStateException("Cannot read feature as propertystore has not been provided");
        }
        return propertyStore;
    }

    /**
     * Setter accessor for attribute 'propertyStore'.
     * @param propertyStore
     * 		new value for 'propertyStore '
     */
    public void setPropertyStore(PropertyStore propertyStore) {
        this.propertyStore = propertyStore;
    }

    /** {@inheritDoc} */
    @Override
    public boolean exists(String uid) {
        return propertyStore.exists(uid);
    }

    /** {@inheritDoc} */
    @Override
    public void create(Feature feature) {
        assertFeatureNotNull(feature);
        assertFeatureNotExist(feature.getUid());
        propertyStore.create(new PropertyFeature(feature));
    }

    @Override
    public void toggleOnGroup(String groupName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void toggleOffGroup(String groupName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean existGroup(String groupName) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Stream<Feature> readGroup(String groupName) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToGroup(String uid, String groupName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void removeFromGroup(String uid, String groupName) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Stream<String> readAllGroups() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long count() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void delete(String entityId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteAll() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Stream<Feature> findAll() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Optional<Feature> findById(String id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void update(Feature entity) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void createFeature(Feature feature) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void updateFeature(Feature feature) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void deleteFeature(String uid) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void deleteAllFeatures() {
        // TODO Auto-generated method stub
        
    }
    
}
