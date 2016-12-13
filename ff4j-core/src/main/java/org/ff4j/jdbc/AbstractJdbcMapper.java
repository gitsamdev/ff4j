package org.ff4j.jdbc;

import java.sql.Connection;

/**
 * Mapper for JDBC object.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public abstract class AbstractJdbcMapper {
    
    /** sql Connection. */
    protected Connection sqlConn = null;
    
    /** helper to access JDBC queries and constants. */
    protected JdbcQueryBuilder queryBuilder = null;
    
    public AbstractJdbcMapper(Connection sqlConn, JdbcQueryBuilder qbd) {
        this.sqlConn      = sqlConn;
        this.queryBuilder = qbd;
    }

}
