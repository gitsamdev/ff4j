package org.ff4j.lab.v2;

import java.util.stream.Stream;

import org.ff4j.audit.log.AbstractAuditTrailFeatures;
import org.ff4j.audit.log.AuditTrailQuery;
import org.ff4j.event.Event;

public class AuditTrailFeatureLogger extends AbstractAuditTrailFeatures {

    /** {@inheritDoc} */
    @Override
    public void log(Event evt) {
        System.out.println(evt.toJson());
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Event> search(AuditTrailQuery query) {
        return Stream.empty();
    }

    /** {@inheritDoc} */
    @Override
    public void purge(AuditTrailQuery query) {
    }

}
