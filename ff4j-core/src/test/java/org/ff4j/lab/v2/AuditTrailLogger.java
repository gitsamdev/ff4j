package org.ff4j.lab.v2;

import java.util.stream.Stream;

import org.ff4j.audit.AuditTrail;
import org.ff4j.audit.AuditTrailQuery;
import org.ff4j.event.Event;

public class AuditTrailLogger implements AuditTrail {

    @Override
    public void log(Event evt) {
       System.out.println("Audit:" + evt.toJson());
    }

    @Override
    public Stream<Event> search(AuditTrailQuery query) {
        return null;
    }

    @Override
    public void purge(AuditTrailQuery query) {
    }

}
