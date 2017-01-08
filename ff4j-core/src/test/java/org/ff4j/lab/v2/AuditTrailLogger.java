package org.ff4j.lab.v2;

import java.util.stream.Stream;

import org.ff4j.audit.AuditTrail;
import org.ff4j.audit.AuditTrailQuery;
import org.ff4j.event.Event;

/**
 * AuditTrail.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class AuditTrailLogger implements AuditTrail {

    /** {@inheritDoc} */
    @Override
    public void log(Event evt) {
       System.out.println("Audit:" + evt.toJson());
    }

    /** {@inheritDoc} */
    @Override
    public void createSchema() {
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream<Event> search(AuditTrailQuery query) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void purge(AuditTrailQuery query) {
    }

}
