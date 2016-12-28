package org.ff4j.utils;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.lang.model.type.NullType;

import org.ff4j.FF4jEntity;
import org.ff4j.event.Event;

/**
 * Tips and tricks to be less verbose.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class Util {
    
    private Util() {
    }

    /**
     * Find a feature or property in a stream
     * @param stream
     * @param uid
     * @return
     */
    public static < T extends FF4jEntity<?> > Optional<T> find(Stream <T> stream, String uid) {
        if (stream == null || uid == null) return Optional.empty();
        return stream.filter(t -> uid.equals(t.getUid())).findFirst();
    }
    
    public static LocalDateTime asLocalDateTime(java.sql.Timestamp sqlTimeStamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(sqlTimeStamp.getTime()), ZoneId.systemDefault());
    }
    
    public static java.sql.Timestamp asSqlTimeStamp(LocalDateTime jdk8Date) {
        ZoneOffset zof = ZoneId.systemDefault().getRules().getOffset(jdk8Date);
        return new java.sql.Timestamp(jdk8Date.toEpochSecond(zof));
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
     * Creation of a map from a single Value.
     * @param key
     *      map key
     * @param value
     *      map value
     * @return
     *      the populated map
     */
    public static <K,V> Map < K, V > mapOf(K key, V value) {
        Map <K, V> mapOfValues = new HashMap<>();
        mapOfValues.put(key, value);
        return mapOfValues;
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
        assertTrue(expression, "this expression must be true");
    }
    
    /**
     * Check that expression is true.
     * 
     * @param expression
     *            expression to evaluate
     */
    public static void assertTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException("[Assertion failed] - " + message);
        }
    }

    /**
     * Check that object is null.
     * 
     * @param object
     *            target object
     */
    public static void requireNull(Object object) {
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
    public static void requireNotNull(Object object) {
        requireNotNull(object, "Object ");
    }
    
    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void requireNotNull(Object object, String objectName) {
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
    public static void requireHasLength(String string) {
        requireHasLength(string, "String ");
    }
    
    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void requireHasLength(String string, String objectName) {
        if (!hasLength(string)) {
            throw new IllegalArgumentException(objectName + " must not be null nor empty");
        }
    }
    
    public static void validateEvent(Event evt) {
        requireNotNull(evt);
        requireNotNull(evt.getScope());
        requireHasLength(evt.getTargetUid());
        requireHasLength(evt.getAction());
    }

    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void assertNotNull(Object... params) {
        assertNotNull("parameter", params);
    }
    
    /**
     * Check that object is not null.
     * 
     * @param object
     *            target object
     */
    public static void assertNotNull(String objectName, Object... params) {
        if (params == null) {
            throw new IllegalArgumentException("[Assertion failed] - " + objectName + " must not be null");
        }
        for (int idx = 0; idx < params.length ;idx++) {
            Object currentparam = params[idx];
            if (null == currentparam) {
                throw new IllegalArgumentException("[Assertion failed] - " + objectName + " must not be null");
            }
        }
    }

    /**
     * Check that string is not null
     * 
     * @param object
     *            target object
     */
    public static void assertHasLength(String... params) {
        if (params == null) {
            throw new IllegalArgumentException("[Assertion failed] - Parameter #0 (string)  must not be null nor empty");
        }
        if (params != null) {
            for (int idx = 0; idx < params.length ;idx++) {
                String currentparam = params[idx];
                if (null == currentparam || currentparam.isEmpty()) {
                    throw new IllegalArgumentException("[Assertion failed] - Parameter #" + idx + "(string)  must not be null nor empty");
                }
            }
        }
    }
    
    /**
     * Check that string is not null
     * 
     * @param object
     *            target object
     */
    public static void assertNotEmpty(Collection<?> collec) {
        if (null == collec || collec.isEmpty()) {
            throw new IllegalArgumentException("[Assertion failed] - Target COLLECTION must not be null nor empty");
        }
    }
    
    /**
     * Check that string is not null
     * 
     * @param object
     *            target object
     */
    public static <T> void assertNotEmpty(T[] collec) {
        if (null == collec || collec.length == 0) {
            throw new IllegalArgumentException("[Assertion failed] - Target ARRAY must not be null nor empty");
        }
    }
    
    /**
     * Parameter validation.
     *
     * @param param
     *      current parameter
     * @param paramName
     *      current parameter name
     */
    public static void assertParamHasLength(String param, String paramName) {
        if (param == null || param.isEmpty()) {
            throw new IllegalArgumentException("Missing Parameter '" + paramName + "' must not be null nor empty");
        }
    }
    
    /**
     * Parameter validation.
     *
     * @param param
     *      current parameter
     * @param paramName
     *      current parameter name
     */
    public static void assertParamHasNotNull(Object param, String paramName) {
        if (param == null) {
            throw new IllegalArgumentException("Missing Parameter '" + paramName + "' must not be null nor empty");
        }
    }
    
    /**
     * Read hostName from JDK.
     * 
     * @return
     *      current hostname
     */
    public static String inetAddressHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new IllegalArgumentException("Cannot find the target host by itself", e);
        }
    }


   
    
    /**
     * Serialize collection elements with a delimiter.
     *
     * @param collec
     *      collection (a,b,c)
     * @param delimiter
     *      delimiter char (e.g : ",")
     * @return
     *      the list : a,b,c
     */
    public static <T> String join(Collection < T > collec, String delimiter) {
        requireNotNull(delimiter);
        if (collec == null) return null;
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T t : collec) {
            if (!first) {
                sb.append(",");
            }
            sb.append(t.toString());
            first = false;
        }
        return sb.toString();
    }
    
    /**
     * Check if a current class can be cast to collection.
     * 
     * @param c
     *      current class
     * @return
     *      flag if it's a collection
     */
    public static boolean isClassCollection(Class<?> c) {
        return Collection.class.isAssignableFrom(c) || Map.class.isAssignableFrom(c);
    }
    
    /**
     * Check if a current object can be cast to collection.
     * 
     * @param ob
     *      current object
     * @return
     *      flag if it's a collection
     */
    public static boolean isCollection(Object ob) {
        return ob != null && isClassCollection(ob.getClass());
    }
    
    /**
     * Check if a current object is empty
     * 
     * @param ob
     *      current collection
     * @return
     *      flag if it's an empty collection
     */
    public static boolean isEmpty(Collection<?> ob) {
        return (ob == null) || ob.isEmpty();
    }
    
    /**
     * Downcast as collection or return error.
     *
     * @param ob
     *      target object
     * @return
     *      if can ve converted to collection.
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection < T > asCollection(Object ob) {
        if (ob == null) return null;
        if (ob.getClass().isArray()) {
            return Arrays.asList((T[]) ob);
        }
        if (!isCollection(ob)) {
            throw new IllegalArgumentException("Target Object is not collection");
        }
        return (Collection<T>) ob;
    }
    
    /**
     * Get a random offset within map.
     *
     * @param size
     *      target list size
     * @return
     *      a random positive integer below size
     */
    public static int getRandomOffset(int size) {
        return (int) (Math.random() * Math.abs(size));
    }
    
    /**
     * Get a random element from a list.
     *
     * @param myList
     *      current list
     */
    public static < T > T getRandomElement(List<T> myList) {
        return myList.get(getRandomOffset(myList.size()));
    }
     
    /**
     * Allow to instanciate utility class.
     *
     * @param utilityClass
     *      current utility
     * @return
     *      instance
     * @throws Exception
     */
    public static <T> T instanciatePrivate(Class<T> utilityClass) throws Exception {
        Constructor<T> ce = utilityClass.getDeclaredConstructor();
        ce.setAccessible(true);
        return ce.newInstance();
    }
    
    /**
     * Get key listfrom value.
     *
     * @param map
     *      current MAP
     * @param value
     *      value of MAP
     * @return
     */
    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        if (map == null) return null;
        Set<T> keys = new HashSet<T>();
        for (Entry<T, E> entry : map.entrySet()) {
            if (value != null && value.equals(entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }
    
    /**
     * Get a first key matching from value.
     *
     * @param map
     *      current MAP
     * @param value
     *      value of MAP
     * @return
     */
    public static <T, E> T getFirstKeyByValue(Map<T, E> map, E value) {
        if (map == null) return null;
        for (Entry<T, E> entry : map.entrySet()) {
            if (value != null && value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

}
