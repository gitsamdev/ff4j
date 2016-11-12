package org.ff4j.store;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Super Interface to work with features and properties
 *
 * @author Cedrick LUNVEN  (@clunven)
 * @author Andre Blaszczyk (@AndrBLASZCZYK)
 *
 * @since 2.x
 * @param <TARGET>
 *      current object to work with
 * @param <ID>
 *      unique identifier (String most of the case).
 */
public interface FF4jRepository < ENTITY, ID extends Serializable > {
    
    /**
     * Count number of elements in the repository
     *
     * @return
     *      target 
     */
    long count();
    
    /**
     * Delete target entity by its id.
     *
     * @param entityId
     *      target entity
     */
    void delete(ID entityId);
    
    /**
     * Delete several entities in a single call.
     * 
     * @param entities
     *      target entites to remove
     */
    void delete(Iterable<? extends ENTITY> entities);

    /**
     * Delete an entity.
     * 
     * @param entities
     *      target entites to remove
     */
    void delete(ENTITY entity);
    
    /**
     * Empty the target repository.
     */
    void deleteAll();
    
    /**
     * Check if an entity exist or not.
     * 
     * @param id
     *      unique identifier of the id
     * @return
     */
    boolean exists(ID id);
    
    /**
     * Retrieve all entities of the stores as a collection.
     *
     * @return
     *      entities as an {@link Iterable}
     */
    Stream < ENTITY > findAll();
    
    /**
     * Retrieve a subset of store.
     *
     * @param ids
     *      unique identifier
     * @return
     *      subset of elements
     */
    Stream <ENTITY> findAll(Iterable<ID> ids);
    
    /**
     * Find One entity by its id.
     * 
     * @param id
     *      target identifier
     * @return
     *      entity if exist
     */
    Optional <ENTITY> findById(ID id);
    
    /**
     * Find One entity by its id.
     * 
     * @param id
     *      target identifier
     * @return
     *      entity if exist
     */
   ENTITY read(ID id);
    
    /**
     * Saves a given entity.
     *
     * @param entity
     * @return
     */
    void create(ENTITY entity);
    
    /**
     * Saves a given entity.
     *
     * @param entity
     * @return
     */
    void update(ENTITY entity);
    
    /**
     * Import Item into target repository (override if exist).
     *
     * @param entities
     *      target entities
     */
    void save(Collection<ENTITY> entities);
    
}
