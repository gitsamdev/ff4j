package org.ff4j.feature;

import org.ff4j.audit.AuditTrail;
import org.ff4j.audit.AuditTrailListenerSupport;
import org.ff4j.event.Event.Action;
import org.ff4j.event.Event.Scope;

/**
 * Proposition of superclass to allow audit trail trackings.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 */
public class FeatureStoreListenerAudit extends AuditTrailListenerSupport < Feature > implements FeatureStoreListener {
    
    public FeatureStoreListenerAudit(AuditTrail auditTrail) {
        super(auditTrail, Scope.FEATURE, Scope.FEATURESTORE);
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
