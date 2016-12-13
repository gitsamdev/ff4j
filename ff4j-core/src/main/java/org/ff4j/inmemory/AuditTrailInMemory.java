package org.ff4j.inmemory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import org.ff4j.audit.AuditTrail;
import org.ff4j.audit.AuditTrailQuery;
import org.ff4j.event.Event;
import org.ff4j.event.EventScope;
import org.ff4j.event.EventSeries;

/**
 * Store AuditLog for all informations related to features and properties.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class AuditTrailInMemory implements AuditTrail {

    /** default retention. */
    private static final int DEFAULT_QUEUE_CAPACITY = 100000;

    /** current capacity. */
    private int queueCapacity = DEFAULT_QUEUE_CAPACITY;
    
    /** Event <SCOPE> -> <ID> -> List Event related to user action in console (not featureUsage, not check OFF). */
    private Map<EventScope, Map < String, EventSeries>> auditTrail = new ConcurrentHashMap<>();
    
    /** {@inheritDoc} */
    @Override
    public void log(Event evt) {
        EventScope scope = evt.getScope();
        String     uid   = evt.getName();
        if (!auditTrail.containsKey(scope)) {
            auditTrail.put(scope, new ConcurrentHashMap<>());
        }
        if (!auditTrail.get(scope).containsKey(uid)) {
            auditTrail.get(scope).put(uid, new EventSeries(this.queueCapacity));
        }
        auditTrail.get(scope).get(uid).add(evt);
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Event> search(AuditTrailQuery query) {
        if (query == null) return null;
        
        if (query.getScope().isPresent()) {
            // use only the target scope to perform filters
            return filterEvents(query, auditTrail.get(query.getScope().get())).stream();
        } else {
            // loop on all scopes (no filter here on scope)
            
            //Collection < Event > results = new ArrayList<>();
            //auditTrail.values().stream().forEach(map -> results.addAll(filterEvents(query, map)));
            //return results.stream();
            
            /*.reduce(new ArrayList<>(), (list, newElements) -> {
            list.addAll(newElements);
            return list;
            })*/
            return auditTrail.values().stream()
                    .map(map -> filterEvents(query, map))
                    .collect(ArrayList<Event>::new, ArrayList::addAll, ArrayList::addAll)
                    .stream();
                    
        }
    }
    
    /**
     * Match query against event series.
     */
    private Collection<Event> filterEvents(AuditTrailQuery query, Map < String, EventSeries> events) {
        Collection < Event > results = new ArrayList<>();
        if (query.getUid().isPresent()) {
            results.addAll(query.filter(events.get(query.getUid().get())));
        } else {
            events.values().forEach(es -> results.addAll(query.filter(es)));
        }
        return results;
    }    

    /** {@inheritDoc} */
    @Override
    public void purge(AuditTrailQuery query) {
        search(query).forEach(evt -> {
            auditTrail.get(evt.getScope())
                      .get(evt.getName())
                      .remove(evt);
        });
    }

}
