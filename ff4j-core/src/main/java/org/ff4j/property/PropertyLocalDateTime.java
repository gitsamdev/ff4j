package org.ff4j.property;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Creatoin of property.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class PropertyLocalDateTime extends Property< LocalDateTime > {

    /** serialVersionUID. */
    private static final long serialVersionUID = -620523134883483837L;
    
    /** formatter for creation date and last modified. */
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    /**
     * Constructor by property name.
     *
     * @param name
     *      property name
     */
    public PropertyLocalDateTime(String name) {
        super(name);
    }
    
    /**
     * Constructor by string expression.
     *
     * @param uid
     *      unique name
     * @param lvl
     *      current log level
     */
    public PropertyLocalDateTime(String uid, String value) {
       super(uid, value);
    }
    
    /**
     * Constructor by string expression.
     *
     * @param uid
     *      unique name
     * @param lvl
     *      current log level
     */
    public PropertyLocalDateTime(String uid, LocalDateTime date) {
       super(uid, date);
    }    

    /** 
     * Serialized value as String
     *
     * @return
     *      current value as a string or null
     */
    public String asString() {
        if (value == null) return null;
        return value.format(FORMATTER);
    }
    
    /** {@inheritDoc} */
    @Override
    public LocalDateTime fromString(String v) {
        return LocalDateTime.parse(v, FORMATTER);
    }

}

