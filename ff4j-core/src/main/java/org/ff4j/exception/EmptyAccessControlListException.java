package org.ff4j.exception;

/**
 * Raised when the ACL is empty but you try to access it (grant..)
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class EmptyAccessControlListException extends RuntimeException {

   /** serialVersionUID. */
    private static final long serialVersionUID = 5114735390174323273L;

/**
     * Parameterized constructor.
     * 
     * @param msg
     *            Exception message
     **/
    public EmptyAccessControlListException() {
        super("Cannot update AccessControlList, it's empty");
    }

}
