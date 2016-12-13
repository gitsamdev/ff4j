package org.ff4j.audit;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

import org.ff4j.event.Event;
import org.ff4j.event.EventScope;
import org.ff4j.event.EventSeries;

public class AuditTrailQuery {
    
    private Long from;
    
    private Long to;
    
    private EventScope scope;
    
    private String uid;
    
    public AuditTrailQuery() {
    }
    
    public AuditTrailQuery from(long from) {
        this.from = new Long(from);
        return this;
    }
    
    public AuditTrailQuery scope(EventScope scope) {
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
    
    public Optional < EventScope > getScope() {
        return Optional.ofNullable(scope);
    }
    
    /**
     * Lisibility, lisibility
     */
    public boolean match(Event evt) {
        boolean okLowerBound = (from == null)  || (from != null && evt.getTimestamp() >= from);
        boolean okUpperBound = (to == null)    || (to != null && evt.getTimestamp() <= to);
        boolean okScope      = (scope == null) || scope.name().equalsIgnoreCase(evt.getScope().name());
        boolean okUid        = (uid == null)   || uid.equalsIgnoreCase(evt.getName());
        return okLowerBound & okUpperBound & okScope & okUid;
    }
    
    public Collection <Event > filter(EventSeries es) {
        return es.stream().filter(this::match).collect(Collectors.toList());
    }

}
