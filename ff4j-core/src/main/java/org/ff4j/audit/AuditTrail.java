package org.ff4j.audit;

import java.util.stream.Stream;

import org.ff4j.event.Event;

/**
 * Audit Trail is READ ONLY.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public interface AuditTrail {
    
    void log(Event evt);
    
    /**
     * Search events in the audit trail.
     *
     * @param query
     *      target query
     * @return
     */
    Stream < Event > search(AuditTrailQuery query);
    
    /**
     * Will delete log record matching the query. Will create a new record line to notify.
     *
     * @param query
     *      current query
     */
    void purge(AuditTrailQuery query);
}
