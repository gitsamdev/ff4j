package org.ff4j.feature;

import java.util.Map;

import org.ff4j.FF4jExecutionContext;
import org.ff4j.exception.InvalidStrategyTypeException;
import org.ff4j.strategy.FF4jExecutionStrategy;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 Ff4J
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

/**
 * Each feature should implement the flipping strategy. (enabling/disabling will be handle by flipper.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public interface FlippingStrategy extends FF4jExecutionStrategy {
    
    /**
     * Tell if flip should be realized.
     * 
     * @param featureName
     *            target featureName
     * @param executionContext
     *            custom params to make decision
     * @return if flipping should be performed
     */
    boolean evaluate(String featureName, FeatureStore store, FF4jExecutionContext executionContext);
    
    /**
     * Instanciate flipping strategy from its class name.
     *
     * @param className
     *      current class name
     * @return
     *      the flipping strategy
     */
    public static FlippingStrategy instanciate(String uid, String className,  Map<String, String> initparams) {
        try {
            FlippingStrategy strategy = (FlippingStrategy) Class.forName(className).newInstance();
            strategy.init(uid, initparams);
            return strategy;
        } catch (Exception ie) {
            throw new InvalidStrategyTypeException(className, ie);
        } 
    }
    
}
