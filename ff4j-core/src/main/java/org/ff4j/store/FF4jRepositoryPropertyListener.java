package org.ff4j.store;

import org.ff4j.FF4jEntity;
import org.ff4j.property.Property;

/**
 * Public Interface of a Listener on CRUD repository.
 * @Do not put any onRead() as making not sense in ff4J.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <ENTITY>
 *    {@link FF4jEntity} to be specialized by type of store 
 */
public interface FF4jRepositoryPropertyListener extends FF4jRepositoryListener< Property<?> > {
    
    
}
