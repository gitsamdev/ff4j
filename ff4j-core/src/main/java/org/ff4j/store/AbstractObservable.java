package org.ff4j.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;

/**
 * Common Observable pattern to be reused in AuditTrail and FeatureUsage Tracking.
 *
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <LISTENER>
 *      target listener type
 */
public class AbstractObservable < LISTENER > {
    
    /** My list of listener to test. */
    protected Map < String, LISTENER > listeners = new HashMap<>();
    
    /** Optimize Thread-Safe. */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    
    /** Read element. */
    protected final Lock readLock = readWriteLock.readLock();
    
    /** Write element. */
    protected final Lock writeLock = readWriteLock.writeLock();
    
    public boolean isExistListener(String uid) {
        return listeners.containsKey(uid);
    }
    
    public LISTENER getListener(String uid) {
        return listeners.get(uid);
    }
    
    /**
     * Register a listener in my observable strategy.
     *
     * @param listener
     *      
     * @return
     */
    public void registerListener(String name, LISTENER listener) {
        try {
            this.writeLock.lock();
            this.listeners.put(name, listener);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    public void unregisterListener(String name) {
        try {
            this.writeLock.lock();
            this.listeners.remove(name);
        }
        finally {
            this.writeLock.unlock();
        }
    }
    
    /**
     * Execute target algo for each listener.
     *
     * @param lambda
     *      target lambda
     */
    public void notify(Consumer<? super LISTENER> lambda) {
        this.listeners.values().forEach(listener -> {
            CompletableFuture.runAsync((Runnable) (() -> lambda.accept(listener)));
        });
    }
    
}
