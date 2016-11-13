package org.ff4j.strategy;

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

import org.ff4j.FF4jExecutionContext;
import org.ff4j.exception.PropertyAccessException;
import org.ff4j.property.Property;

/**
 * Allow to compute properties at runtime through
 * 
 * @author Cedrick LUNVEN  (@clunven)
 *
 * @param <T>
 */
public interface PropertyEvaluationStrategy <T> extends FF4jExecutionStrategy {

    /**
     * Tell if flip should be realized.
     * 
     * @param featureName
     *            target featureName
     * @param executionContext
     *            custom params to make decision
     * @return if flipping should be performed
     */
    T evaluate(Property<?> currentProperty, FF4jExecutionContext executionContext);
    
    /**
     * Instanciate flipping strategy from its class name.
     *
     * @param className
     *      current class name
     * @return
     *      the flipping strategy
     */
    public static PropertyEvaluationStrategy<?> instanciate(String uid, String className,  Map<String, String> initparams) {
        try {
            PropertyEvaluationStrategy<?> evalStrategy = (PropertyEvaluationStrategy<?>) Class.forName(className).newInstance();
            evalStrategy.init(uid, initparams);
            return evalStrategy;
        } catch (Exception ie) {
            throw new PropertyAccessException("Cannot instantiate Strategy, no default constructor available", ie);
        } 
    }
    
}