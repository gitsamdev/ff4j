package org.ff4j.utils;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.type.NullType;

/**
 * Single utility class for ff4J.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class FF4jUtils {
    
    private FF4jUtils() {
    }
    
    /**
     * Create an HashSet.
     *
     * @param els
     *            enumeration of elements
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> Set<T> setOf(T... els) {
         return (els == null) ? null : new HashSet<T>(Arrays.asList(els));
    }
    
    /**
     * Create an HashSet.
     *
     * @param els
     *            enumeration of elements
     * @return
     */
    public static <T> Set<T> setOf(Stream < T > elements) {
        return (elements == null) ? null : elements.collect(Collectors.toSet());
    }
    
    /**
     * Create an HashSet.
     *
     * @param els
     *            enumeration of elements
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> listOf(T... els) {
        return (els == null) ? null : new ArrayList<T>(Arrays.asList(els));
    }
    
    /**
     * Check that expression is true.
     * 
     * @param expression
     *            expression to evaluate
     */
    public static boolean hasLength(String expression) {
        return expression != null && !"".equals(expression);
    }
    
    /**
     * Check that class is valid.
     * 
     * @param expression
     *            expression to evaluate
     */
    public static boolean isValidClass(Class<?> clazz) {
        return (clazz != null) && (clazz != NullType.class);
    }
    
   /**
     * Check that expression is true.
     * 
     * @param expression
     *            expression to evaluate
     */
    public static void assertTrue(boolean expression) {
        if (!expression) {
            throw new IllegalArgumentException("[Assertion failed] - this expression must be true");
        }
    }

    /**
     * Check that object is null.
     * 
     * @param object
     *            target object
     */
    public static void assertNull(Object object) {
        if (object != null) {
            throw new IllegalArgumentException("[Assertion failed] - the object argument must be null");
        }
    }
    
    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void assertNotNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("Object must not be null");
        }
    }
    
    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void assertNotNull(Object object, String objectName) {
        if (object == null) {
            throw new IllegalArgumentException(objectName + " must not be null");
        }
    }
    
    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void assertHasLength(String string) {
        if (!hasLength(string)) {
            throw new IllegalArgumentException("String must not be null nor empty");
        }
    }
    
    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void assertHasLength(String string, String objectName) {
        if (!hasLength(string)) {
            throw new IllegalArgumentException(objectName + " must not be null nor empty");
        }
    }

}
