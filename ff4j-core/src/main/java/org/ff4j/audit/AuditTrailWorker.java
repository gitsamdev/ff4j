package org.ff4j.audit;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2014 Ff4J
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

import java.util.concurrent.Callable;

import org.ff4j.event.Event;

/**
 * Worker to save {@link Event} into {@link FeatureUsageTracking} asynchronously.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class AuditTrailWorker implements Callable<Boolean> {

    /** retry count if not available. */
    private static final int MAX_RETRY = 3;

    /** retry delay. */
    private static final long RETRY_DELAY = 500L;

    /** current thread name if relevant. */
    private String threadName = null;

    /** Target event to insert. */
    private Event event = null;
    
    /** Repository to store event. */
    private AuditTrail auditTrail = null;
    
    /**
     * Worker constructor.
     * 
     * @param e
     *            event
     * @param repo
     *            event repository to store events
     */
    public AuditTrailWorker(AuditTrail auditTrail, Event evt) {
        this.event = evt;
        this.auditTrail = auditTrail;
        if (evt != null) {
            this.threadName = evt.getScope().name() + "-" + evt.getName() + "-" + evt.getAction();
        }
    }
    
    /**
     * Name for this worker.
     *
     * @return
     */
    public String getThreadName() {
        return this.threadName;
    }

    /** {@inheritDoc} */
    @Override
    public Boolean call() throws Exception {
        boolean ok = false;
        int retryCount = 0;
        while (!ok && retryCount < MAX_RETRY) {
            try {
                auditTrail.log(event);
                ok = true;
            } catch(Throwable t) {
                retryCount++;
                Thread.sleep(RETRY_DELAY);
            }
        }
        return ok;
    }
    
}
