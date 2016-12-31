package org.ff4j.property;

import org.ff4j.FF4jContext;
import org.ff4j.strategy.FF4jStrategy;

/**
 * Allow to compute properties at runtime through
 * 
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <T>
 */
public interface DynamicValueStrategy <T> extends FF4jStrategy {

    /**
     * Tell if flip should be realized.
     * 
     * @param featureName
     *            target featureName
     * @param executionContext
     *            custom params to make decision
     * @return if flipping should be performed
     */
    T getValue(Property<T> currentProperty, FF4jContext executionContext);
    
}