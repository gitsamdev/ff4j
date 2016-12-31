package org.ff4j.audit;

import org.ff4j.FF4j;
import org.ff4j.FF4jEntity;
import org.ff4j.event.Event;
import org.ff4j.event.Event.Action;
import org.ff4j.event.Event.Scope;
import org.ff4j.event.Event.Source;
import org.ff4j.store.FF4jRepositoryListener;
import org.ff4j.utils.Util;

/**
 * Audit Trail superClass.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class AuditTrailListenerSupport<E extends FF4jEntity<?>> implements FF4jRepositoryListener<E> {

    /** Audit trali reference. */
    protected AuditTrail auditTrail;
    
    /** Current source from ff4j. */
    protected Source source = Source.JAVA_API;
    
    /** Scope for entity. */
    protected Scope scopeEntity = Scope.UNKNOWN;
    
    /** Scope for store. */
    protected Scope scopeStore = Scope.UNKNOWN;
    
    public AuditTrailListenerSupport(AuditTrail auditTrail, Scope sEntity, Scope sStore) {
        this.scopeEntity = sEntity;
        this.scopeStore = sStore;
        this.auditTrail = auditTrail;
    }
    
    /**
     * Fill the owner if relevant.
     *
     * @param current
     *      current event.
     */
    protected void populateOwner(Event current) {
        if (FF4j.getContext().getCurrentUser().isPresent()) {
            current.setOwner(FF4j.getContext().getCurrentUser().get().getUid());
        }
    }
    
    protected Event createEvent(Action action, Scope scope) {
        Event evt = new Event().source(source).action(action).scope(scope);
        populateOwner(evt);
        return evt;
    }
    
    protected void log(Event evt) {
        Util.requireNotNull(evt);
        if (auditTrail == null) {
            throw new IllegalStateException("Cannot access audit Trail, please init the listener properly");
        }
        auditTrail.log(evt);
    }
    
    protected void logEvent(Action action, Scope scope, String uid) {
        log(createEvent(action, scope).targetUid(uid));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onCreateSchema() {
        log(createEvent(Action.CREATE_SCHEMA, scopeStore));
    }
    
    /** {@inheritDoc} */
    public void onDeleteAll() {
        log(createEvent(Action.DELETE, scopeStore));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onUpdate(E entity) {
        logEvent(Action.UPDATE, scopeEntity, entity.getUid());
    }
    
    /** {@inheritDoc} */
    @Override
    public void onCreate(E entity) {
        logEvent(Action.CREATE, scopeEntity, entity.getUid());
    }

    public void onDelete(String uid) {
        logEvent(Action.DELETE, scopeEntity, uid);
    }   

    protected void onUpdateEntity(E entity) {
        logEvent(Action.UPDATE, scopeEntity, entity.getUid());
    }

}
