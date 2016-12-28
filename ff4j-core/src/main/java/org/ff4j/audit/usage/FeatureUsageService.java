package org.ff4j.audit.usage;

import java.text.SimpleDateFormat;
import java.util.Date;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2016 FF4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.ff4j.chart.TimeSeriesChart;
import org.ff4j.event.Event;
import org.ff4j.event.Event.Scope;
import org.ff4j.event.EventQueryDefinition;
import org.ff4j.event.EventSeries;
import org.ff4j.feature.Feature;
import org.ff4j.store.FF4jRepository;
import org.ff4j.utils.MutableHitCount;

/**
 * Persistence store for {@link Event} messages.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public interface FeatureUsageService extends FF4jRepository < String, Event > {
    
    /** Create key. */
    SimpleDateFormat KDF = new SimpleDateFormat("yyyyMMdd");
    
    /**
     * Search over events.
     *
     * @return
     *      a list of events
     */
    EventSeries search(EventQueryDefinition query);
    
    /**
     * Purge feature usage.
     *
     * @param starTime
     *      begin date
     * @param endTime
     *      end time
     */
    void purge(EventQueryDefinition query);
    
    /**
     * Count hit ratio of features between 2 dates. This will be used for different charts.
     *
     * @param startTime
     *          start date
     * @param endTime
     *          end time
     * @return
     */
    Map < String, MutableHitCount > getHitCount(EventQueryDefinition query);
    
    /**
     * Count hit for each host.
     *
     * @param starTime
     *      begin date
     * @param endTime
     *      end time
     * return the hitcount
     */
    Map < String, MutableHitCount > getHostHitCount(EventQueryDefinition query);
    
    /**
     * Count hit for each host.
     *
     * @param starTime
     *      begin date
     * @param endTime
     *      end time
     * return the hitcount
     */
    Map < String, MutableHitCount > getUserHitCount(EventQueryDefinition query);
    
    /**
     * Count hit for each source (api...).
     *
     * @param starTime
     *      begin date
     * @param endTime
     *      end time
     * return the hitcount
     */
    Map < String, MutableHitCount > getSourceHitCount(EventQueryDefinition query);
    
    /**
     * All Data will be generated in the structure and overriden in the chart.
     *
     * @param query
     *      target query
     * @param units
     *      current time unit
     * @return
     *      object structure
     */
    TimeSeriesChart getFeatureUsageHistory(EventQueryDefinition query, TimeUnit units);
    
    /**
     * Get all events.
     * 
     * @return all event in the repository
     */
    default int getTotalHitCount(EventQueryDefinition query) {
        Objects.requireNonNull(query);
        MutableHitCount total = new MutableHitCount();
        getHitCount(query).values().stream().map(MutableHitCount::get).forEach(total::incBy);
        return total.get();
    }
    
    /**
     * Create a new event and push to DB.
     *
     * @param f
     *      creation new elements.
     */
    default void featureUsageHit(Feature f) {
        Objects.requireNonNull(f);
        create(new Event().scope(Scope.FEATURE)
                .targetUid(f.getUid())
                .action(Event.Action.HIT));
    }
    
    /**
     * Format a timestamp to create a Key.
     *
     * @param time
     *      current tick
     * @return
     *      date as Key
     */
    default String getKeyDate(long time) {
        return KDF.format(new Date(time));
    }
    
    /**
     * Will get a list of all days between 2 dates.
     *
     * @param startTime
     *      tip start
     * @param endTime
     *      tip end
     * @return
     *      list of days
     */
    default Set < String > getCandidateDays(long startTime, long endTime) {
       Set < String > resultKeys = new TreeSet<String>();
       String endKey = getKeyDate(endTime);
       resultKeys.add(endKey);
       long time = startTime;
       while (!endKey.equals(getKeyDate(time))) {
           resultKeys.add(getKeyDate(time));
           time += 3600 * 1000 * 24;
       }
       return resultKeys;
   } 
    
}
