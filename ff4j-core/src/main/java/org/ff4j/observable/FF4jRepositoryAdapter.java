package org.ff4j.observable;

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
    public void onDelete(BO bo) {}

    /** {@inheritDoc} */
    @Override
    public void onCreateSchema() {}

}
