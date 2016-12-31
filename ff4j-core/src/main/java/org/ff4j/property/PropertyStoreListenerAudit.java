package org.ff4j.property;

import org.ff4j.audit.AuditTrail;
import org.ff4j.audit.AuditTrailListenerSupport;
import org.ff4j.event.Event.Scope;

/**
 * Proposition of superclass to allow audit trail trackings.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 */
public class PropertyStoreListenerAudit extends AuditTrailListenerSupport<Property<?>> implements PropertyStoreListener {

    public PropertyStoreListenerAudit(AuditTrail auditTrail) {
        super(auditTrail, Scope.PROPERTY, Scope.PROPERTYSTORE);
    }
    
}
