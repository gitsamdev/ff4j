package org.ff4j.property;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Creatoin of property.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class PropertyInstant extends Property< Instant > {

    /** serialVersionUID. */
    private static final long serialVersionUID = -620523134883483837L;
    
    /** formatter for creation date and last modified. */
    protected static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    /** zone offset. */
    protected ZoneOffset zone = ZoneOffset.UTC;

    /**
     * Constructor by property name.
     *
     * @param name
     *      property name
     */
    public PropertyInstant(String name) {
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
    public PropertyInstant(String uid, String value) {
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
    public PropertyInstant(String uid, Instant date) {
       super(uid, date);
    }    

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.ofInstant(value, zone);
    }
    
    /** 
     * Serialized value as String
     *
     * @return
     *      current value as a string or null
     */
    public String asString() {
        if (value == null) return null;
        return toLocalDateTime().format(FORMATTER);
    }
    
    /** {@inheritDoc} */
    @Override
    public Instant fromString(String v) {
        return LocalDateTime.parse(v, FORMATTER).toInstant(zone);
    }

    /**
     * Getter accessor for attribute 'zone'.
     *
     * @return
     *       current value of 'zone'
     */
    public ZoneOffset getZone() {
        return zone;
    }

    /**
     * Setter accessor for attribute 'zone'.
     * @param zone
     * 		new value for 'zone '
     */
    public void setZone(ZoneOffset zone) {
        this.zone = zone;
    }

}

