package org.ff4j.store;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Super Interface to work with features and properties.
 *
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <TARGET>
 *      current object to work with
 * @param <ID>
 *      unique identifier (String most of the case).
 *      
 * @since 2.x
 */
public interface FF4jRepository < ID extends Serializable, ENTITY > {
    
    /**
     * Initialize the target database schema by creating expected structures.
     * 
     * <li> TABLE, INDEX will be created for JDBC, but also COLLECTION and INDEXS for MongoDb, or COLUMN FAMILY for Cassandra.
     * <li> The structures will be created only if they don't exist.
     * <li> In some cases, there is nothing todo (Ehcache, Redis, InMemory), the method won't failed but do nothing (it does not clear the DB) 
     * 
     * @since 1.6
     */
    void createSchema();
    
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
    
    /**
     * Tell if a target store is empty
     *
     * @return
     *      if the store is empty or not
     */
    boolean isEmpty();
    
}
