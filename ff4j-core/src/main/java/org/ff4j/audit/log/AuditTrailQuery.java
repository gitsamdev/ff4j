package org.ff4j.audit.log;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ff4j.event.Event;
import org.ff4j.event.EventSeries;

public class AuditTrailQuery {
    
    private Long from;
    
    private Long to;
    
    private Event.Scope scope;
    
    private String uid;
    
    public AuditTrailQuery() {
    }
    
    public AuditTrailQuery from(long from) {
        this.from = new Long(from);
        return this;
    }
    
    public AuditTrailQuery scope(Event.Scope scope) {
        this.scope = scope;
        return this;
    }
    
    public AuditTrailQuery uid(String uid) {
        this.uid = uid;
        return this;
    }
    
    public Optional < Long > getLowerBound() {
        return Optional.ofNullable(from);
    }
    
    public Optional < Long > getUpperBound() {
        return Optional.ofNullable(to);
    }
    
    public Optional < String > getUid() {
        return Optional.ofNullable(uid);
    }
    
    public Optional < Event.Scope > getScope() {
        return Optional.ofNullable(scope);
    }
    
    /**
     * Lisibility, lisibility
     */
    public boolean match(Event evt) {
               // LowerBound
        return ((from == null) || (from != null && evt.getTimestamp() >= from)) &&
               // UpperBound
               ((to == null) || (to != null && evt.getTimestamp() <= to)) &&
               // Scope
               ((scope == null) || scope.name().equalsIgnoreCase(evt.getScope())) &&
               // Uid
               ((uid == null) || uid.equalsIgnoreCase(evt.getTargetUid()));
    }
    
    public Collection < Event > filter(EventSeries es) {
        return es.stream().filter(this::match).collect(Collectors.toList());
    }

}
