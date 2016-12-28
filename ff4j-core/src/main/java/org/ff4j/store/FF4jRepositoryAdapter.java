package org.ff4j.store;

import org.ff4j.FF4jEntity;

/**
 * Adapter Pattern to avoid implementing all methods on each repository.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class FF4jRepositoryAdapter < BO extends FF4jEntity<?> > implements FF4jRepositoryListener<BO> {

    /** {@inheritDoc} */
    @Override
    public void onCreate(BO bo) {}
   
    /** {@inheritDoc} */
    @Override
    public void onUpdate(BO bo) {}
    
    /** {@inheritDoc} */
    @Override
    public void onDelete(String uid) {}

    /** {@inheritDoc} */
    @Override
    public void onCreateSchema() {}

    /** {@inheritDoc} */
    @Override
    public void onDeleteAll() {}

    /** {@inheritDoc} */
    @Override
    public void onToggleOnFeature(String uid) {}

    /** {@inheritDoc} */
    @Override
    public void onToggleOffFeature(String uid) {}

    /** {@inheritDoc} */
    @Override
    public void onToggleOnGroup(String groupName) {}

    /** {@inheritDoc} */
    @Override
    public void onToggleOffGroup(String groupname) {}

    /** {@inheritDoc} */
    @Override
    public void onAddFeatureToGroup(String uid, String groupName) {}

    /** {@inheritDoc} */
    @Override
    public void onRemoveFeatureFromGroup(String uid, String groupName) {}

}
