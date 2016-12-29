package org.ff4j.audit.log;

import org.ff4j.event.Event;
import org.ff4j.event.Event.Action;
import org.ff4j.event.Event.Scope;
import org.ff4j.event.Event.Source;
import org.ff4j.property.Property;
import org.ff4j.security.FF4JSecurityManager;
import org.ff4j.store.FF4jRepositoryPropertyListener;

/**
 * Proposition of superclass to allow audit trail trackings.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class AbstractAuditTrailProperties implements FF4jRepositoryPropertyListener, AuditTrail {

    /** Current source from ff4j. */
    private Source source = Source.JAVA_API;
    
    /** Current source from ff4j. */
    private FF4JSecurityManager secMng;
    
    /**
     * Instanciate new event and populate automatically : uuid, creationDate,
     * hostName, timestamp, lastmodified.
     * 
     * @param action
     *      event action
     * @param scope
     *      event scope
     * @return
     *      new event
     */
    private Event createEvent(Action action, Scope scope) {
        return new Event().source(source)
                .action(action).scope(scope)
                .setOwner(secMng != null ? secMng.getCurrentUserName() : null);
    }
    
    /** {@inheritDoc} */
    @Override
    public void onCreateSchema() {
        log(createEvent(Action.CREATE_SCHEMA, Scope.PROPERTYSTORE));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onDeleteAll() {
        log(createEvent(Action.DELETE, Scope.PROPERTYSTORE));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onCreate(Property<?> bo) {
        log(createEvent(Action.CREATE, Scope.PROPERTY).targetUid(bo.getUid()));
    }

    /** {@inheritDoc} */
    @Override
    public void onDelete(String uid) {
        log(createEvent(Action.DELETE, Scope.PROPERTY).targetUid(uid));
    }   

    /** {@inheritDoc} */
    @Override
    public void onUpdate(Property<?> bo) {
        log(createEvent(Action.UPDATE, Scope.PROPERTY).targetUid(bo.getUid()));
    }

}
