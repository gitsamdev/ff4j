package org.ff4j;

/**
 * Execution context to be used by both features and properties.
 * 
 * @author Cedrick LUNVEN  (@clunven)
 * @since 2.x
 */
public class FF4jExecutionContextHolder {
    
    /** Context Holder. */
    private static final ThreadLocal<FF4jExecutionContext> contextHolder = new ThreadLocal<FF4jExecutionContext>();
    
    /**
     * Initialization of a context.
     */
    static {
        setContext(new FF4jExecutionContext());
    }
    
    /**
     * Explicitly clears the context value from the current thread.
     */
    public static void clearContext() {
        contextHolder.remove();
    }
    
    /**
     * Obtain the current <code>FF4jExecutionContext</code>.
     *
     * @return the security context (never <code>null</code>)
     */
    public static FF4jExecutionContext getContext() {
        return contextHolder.get();
    }
    
    /**
     * Obtain the current <code>FF4jExecutionContext</code>.
     *
     * @return the security context (never <code>null</code>)
     */
    public static void setContext(FF4jExecutionContext context) {
        contextHolder.set(context);
    }
    
    /**
     * Obtain the current <code>FF4jExecutionContext</code>.
     *
     * @return the security context (never <code>null</code>)
     */
    public static void add2Context(FF4jExecutionContext context) {
        getContext().putAll(context);
    }

}
