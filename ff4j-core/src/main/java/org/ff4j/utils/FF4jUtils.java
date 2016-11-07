package org.ff4j.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        if (els == null) return null;
        return new HashSet<T>(Arrays.asList(els));
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
        if (els == null) return null;
        return new ArrayList<T>(Arrays.asList(els));
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
