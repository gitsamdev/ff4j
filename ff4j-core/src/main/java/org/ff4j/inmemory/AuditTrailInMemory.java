package org.ff4j.inmemory;

import static org.ff4j.utils.Util.validateEvent;
import static org.ff4j.utils.Util.requireNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.ff4j.audit.log.AbstractAuditTrailFeatures;
import org.ff4j.audit.log.AuditTrailQuery;
import org.ff4j.event.Event;
import org.ff4j.event.EventSeries;
import org.ff4j.utils.Util;

/**
 * Store AuditLog for all informations related to features and properties.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class AuditTrailInMemory extends AbstractAuditTrailFeatures {

    /** default retention. */
    private static final int DEFAULT_QUEUE_CAPACITY = 100000;

    /** current capacity. */
    private int queueCapacity = DEFAULT_QUEUE_CAPACITY;
    
    /** Event <SCOPE> -> <ID> -> List Event related to user action in console (not featureUsage, not check OFF). */
    private Map< String , Map < String, EventSeries>> auditTrail = new ConcurrentHashMap<>();
    
    /** {@inheritDoc} */
    @Override
    public void log(Event evt) {
        validateEvent(evt);
        String scope = evt.getScope();
        if (!auditTrail.containsKey(scope)) {
            auditTrail.put(scope, new ConcurrentHashMap<>());
        }
        // Some event do not point a specific feature (featureStore..) so reuse the scope
        String uid = Util.hasLength(evt.getTargetUid()) ? evt.getTargetUid() : scope;
        if (!auditTrail.get(scope).containsKey(uid)) {
            auditTrail.get(scope).put(uid, new EventSeries(this.queueCapacity));
        }
        auditTrail.get(scope).get(uid).add(evt);
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Event> search(AuditTrailQuery query) {
        requireNotNull(query);
        if (query.getScope().isPresent()) {
            // Filter event to get only
            Event.Scope queryScope = query.getScope().get();
            return searchInMapOfEventSeries(query, auditTrail.get(queryScope.toString())).stream();
        }
        Collection < Event > results = new ArrayList<>();
        auditTrail.values().stream().forEach(map -> results.addAll(searchInMapOfEventSeries(query, map)));
        return results.stream();
    }
    
    /**
     * Utility to fetch a map of eventSeries.
     *
     * @param query
     *      current audit query (date, scope)
     * @param mapOfEventSeries
     *      map of events to perform filter
     * @return
     *      the events matching query in the target event series
     */
    private Collection < Event > searchInMapOfEventSeries(AuditTrailQuery query, Map < String, EventSeries > mapOfEventSeries) {
        // Map of EventSeries
        if (query.getUid().isPresent()) {
            // Single EventSerie to search
            EventSeries targetSerie = mapOfEventSeries.get(query.getUid().get());
            return query.filter(targetSerie);
        }
        // No single EventSerie so will have to loop on each key
        Collection < Event > results = new ArrayList<>();
        mapOfEventSeries.values().forEach(es -> results.addAll(query.filter(es)));
        return results;
    }

    /** {@inheritDoc} */
    @Override
    public void purge(AuditTrailQuery query) {
        // Will get a stream of event to remove
        search(query).forEach(evt -> {
            // Get correct scope (Feature, Properties, ...)
            auditTrail.get(evt.getScope())
                      // Get correct event series
                      .get(evt.getTargetUid())
                      // Remove from the Event Series
                      .remove(evt);
        });
    }
    
}
