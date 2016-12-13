package org.ff4j.store;

import static org.ff4j.utils.Util.assertHasLength;
import static org.ff4j.utils.Util.assertNotNull;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.ff4j.FF4jEntity;
import org.ff4j.exception.ItemAlreadyExistException;
import org.ff4j.exception.ItemNotFoundException;
import org.ff4j.observable.AbstractObservableMixin;
import org.ff4j.observable.FF4jRepositoryListener;

/**
 * Support implementations for CRUD.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <V>
 */
public abstract class AbstractFF4jRepository < V extends FF4jEntity<?>> 
                    extends AbstractObservableMixin < FF4jRepositoryListener < V > > 
                    implements FF4jRepository<String, V>, Serializable {

    /** serial number. */
    private static final long serialVersionUID = -2865266843791651125L;
    
    // ---------------------------------------------------------------
    // ---         Adapters to reduce boiler plate code            ---
    // ---------------------------------------------------------------
    
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
        this.delete(entity.getUid());
        
        // Notify all listeners registered (like AuditTrail)
        this.notify(l -> l.onDelete(entity));
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends V> entities) {
        if (null != entities) entities.forEach(this::delete);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        findAll().forEach(this::delete);
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream<V> findAll(Iterable<String> ids) {
        List<V> targetElements = new ArrayList<>();
        ids.forEach(id -> this.findById(id).ifPresent(targetElements::add));
        return targetElements.stream();
    }
    
    /** {@inheritDoc} */
    @Override
    public V read(String id) {
        assertItemExist(id);
        return findById(id).get();
    }
    
    /** {@inheritDoc} */
    @Override
    public void update(V entity) {
        assertNotNull(entity);
        assertHasLength(entity.getUid());
        assertItemExist(entity.getUid());
        entity.setLastModified(LocalDateTime.now());
        entity.setCreationDate(entity.getCreationDate().orElse(entity.getLastModifiedDate().get()));
        delete(entity);
        create(entity);
        // Notify all listeners registered (like AuditTrail)
        this.notify(l -> l.onUpdate(entity));
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<V> entities) {
        if (entities != null) {
            entities.stream().forEach(entity -> {
                if (exists(entity.getUid())) {
                    delete(entity.getUid());
                }
                create(entity);
            });
        }
    }
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        /* Most of the time there is nothing to do. The feature and properties are createdat runtime.
         * But not always (JDBC, Mongo, Cassandra)... this is the reason why the dedicated store must 
         * override this method. It a default implementation (Pattern Adapter).
         */
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
        assertHasLength(uid);
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
        assertHasLength(uid);
        if (exists(uid)) {
            throw new ItemAlreadyExistException(uid);
        }
    }
}
