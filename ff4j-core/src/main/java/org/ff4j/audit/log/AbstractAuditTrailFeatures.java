package org.ff4j.audit.log;

import org.ff4j.event.Event;
import org.ff4j.event.Event.Action;
import org.ff4j.event.Event.Scope;
import org.ff4j.event.Event.Source;
import org.ff4j.feature.FeatureStoreListener;
import org.ff4j.feature.Feature;
import org.ff4j.security.FF4JSecurityManager;

/**
 * Proposition of superclass to allow audit trail trackings.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class AbstractAuditTrailFeatures implements FeatureStoreListener, AuditTrail {

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
        log(createEvent(Action.CREATE_SCHEMA, Scope.FEATURESTORE));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onDeleteAll() {
        log(createEvent(Action.DELETE, Scope.FEATURESTORE));
    }
    
    /** {@inheritDoc} */
    @Override
    public void onCreate(Feature bo) {
        log(createEvent(Action.CREATE, Scope.FEATURE).targetUid(bo.getUid()));
    }

    /** {@inheritDoc} */
    @Override
    public void onDelete(String uid) {
        log(createEvent(Action.DELETE, Scope.FEATURE).targetUid(uid));
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdate(Feature bo) {
        log(createEvent(Action.UPDATE, Scope.FEATURE).targetUid(bo.getUid()));
    }    

    /** {@inheritDoc} */
    @Override
    public void onToggleOnFeature(String uid) {
        log(createEvent(Action.TOGGLE_ON, Scope.FEATURE).targetUid(uid));
    }

    /** {@inheritDoc} */
    @Override
    public void onToggleOffFeature(String uid) {
        log(createEvent(Action.TOGGLE_OFF, Scope.FEATURE).targetUid(uid));
    }

    /** {@inheritDoc} */
    @Override
    public void onToggleOnGroup(String groupName) {
        log(createEvent(Action.TOGGLE_ON, Scope.FEATURE_GROUP).targetUid(groupName));
    }

    /** {@inheritDoc} */
    @Override
    public void onToggleOffGroup(String groupName) {
        log(createEvent(Action.TOGGLE_OFF, Scope.FEATURE_GROUP).targetUid(groupName));
    }

    /** {@inheritDoc} */
    @Override
    public void onAddFeatureToGroup(String uid, String groupName) {
        log(createEvent(Action.ADD_TO_GROUP, Scope.FEATURE)
                .targetUid(groupName)
                .put("targetGroup", groupName));
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoveFeatureFromGroup(String uid, String groupName) {
        log(createEvent(Action.REMOVE_FROM_GROUP, Scope.FEATURE)
                .targetUid(groupName)
                .put("targetGroup", groupName));
    }

}
