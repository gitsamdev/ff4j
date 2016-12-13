package org.ff4j.mapper;

import org.ff4j.event.Event;

/**
 * Specialization of mapper for Events
 * 
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <STORE_OBJ>
 */
public interface EventMapper < REQ, RES > extends Mapper<Event, REQ, RES> {}
