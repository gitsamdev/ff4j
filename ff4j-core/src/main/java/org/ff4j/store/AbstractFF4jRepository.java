package org.ff4j.store;

import static org.ff4j.utils.Util.requireHasLength;
import static org.ff4j.utils.Util.requireNotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.ff4j.FF4jEntity;
import org.ff4j.exception.ItemAlreadyExistException;
import org.ff4j.exception.ItemNotFoundException;
import org.ff4j.utils.Util;

/**
 * Proposition of abstraction to perform operations on entities.
 * 
 * - It has been inspired by <a href="http://docs.spring.io/spring-data/commons/docs/current/api/org/springframework/data/repository/CrudRepository.html">
 * spring data crud repository</a> and <a href="http://static.appfuse.org/appfuse-data/appfuse-ibatis/apidocs/org/appfuse/dao/GenericDao.html">App fuse.</a>
 * - GoF Observable pattern allow to send notifications when entities are modified (audit Trail)
 * 
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <V>
 *      entity manipulated, its unique key is a STRING named 'uid'
 */
public abstract class AbstractFF4jRepository < V extends FF4jEntity<?>> 
                    extends AbstractObservable < FF4jRepositoryListener < V > > 
                    implements FF4jRepository<String, V>, Serializable {

    /** Denerated Serial Number . */
    private static final long serialVersionUID = -2865266843791651125L;
    
    /** {@inheritDoc} */
    @Override
    public long count() {
        return findAll().count();
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return count() == 0;
    }
    
    /** {@inheritDoc} */
    @Override
    public void delete(V entity) {
        Util.requireNotNull(entity);
        this.delete(entity.getUid());
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends V> entities) {
        if (null != entities) entities.forEach(this::delete);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        // This is a default implementation with N+1 select anti-pattern
        // It's meant to be overriden (when possible and relevant)
        findAll().forEach(this::delete);
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream<V> findAll(Iterable<String> ids) {
        if (ids == null) return Stream.empty();
        return StreamSupport
                // Iterable to Stream \_(o^o')_/
                .stream(ids.spliterator(),  false)
                // N+1 Select 'find' 
                .map(this::findById)
                // Get only if found
                .filter(Optional::isPresent)
                // Access data
                .map(Optional::get);
    }
    
    /** {@inheritDoc} */
    @Override
    public V read(String id) {
        assertItemExist(id);
        // As the item exists, the get should not raise exception
        return findById(id).get();
    }
    
    /**
     * Controls before updating and update modified date.
     *
     * @param entity
     *      current entity
     */
    protected void preUpdate(V entity) {
        requireNotNull(entity);
        requireHasLength(entity.getUid());
        assertItemExist(entity.getUid());
        entity.setLastModified(LocalDateTime.now());
        entity.setCreationDate(entity.getCreationDate().orElse(entity.getLastModifiedDate().get()));
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<V> entities) {
        if (entities != null) {
            entities.stream().forEach(this::update);
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        // If not overrided, should notify subscriber anyway.
        this.notify(FF4jRepositoryListener::onCreateSchema);
        return;
    }
    
    // ---------------------------------------------------------------
    // ---         Utility methods to work with Repositories       ---
    // ---------------------------------------------------------------
    
    /**
     * Validate feature uid.
     *
     * @param uid
     *      target uid
     */
    protected void assertItemExist(String uid) {
        requireHasLength(uid);
        if (!exists(uid)) {
            throw new ItemNotFoundException(uid);
        }
    }
    
    /**
     * Check that current feature does not exist.
     *
     * @param uid
     *      current feature identifier.s
     */
    protected void assertItemNotExist(String uid) {
        requireHasLength(uid);
        if (exists(uid)) {
            throw new ItemAlreadyExistException(uid);
        }
    }
}
