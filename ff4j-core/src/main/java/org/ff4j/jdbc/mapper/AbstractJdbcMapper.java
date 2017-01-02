package org.ff4j.jdbc.mapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.ff4j.FF4jEntity;
import org.ff4j.jdbc.JdbcConstants;
import org.ff4j.jdbc.JdbcQueryBuilder;
import org.ff4j.utils.TimeUtils;

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
    
    /**
     * Constructor with connection.
     *
     * @param sqlConn
     *      sql conenction
     * @param qbd
     *      sql query builder
     */
    public AbstractJdbcMapper(Connection sqlConn, JdbcQueryBuilder qbd) {
        this.sqlConn      = sqlConn;
        this.queryBuilder = qbd;
    }
    
    /**
     * Utility to retrieve a {@link LocalDateTime} from SQl {@link Timestamp}.
     * 
     * @param rs
     *      current resultset
     * @param colName
     *      current column name
     * @return
     *      the local time
     */
    protected LocalDateTime getLocalDateTime(ResultSet rs, String colName) {
        try {
            return TimeUtils.asLocalDateTime(rs.getTimestamp(colName));
        } catch (SQLException sqlEx) {
            throw new IllegalArgumentException("Cannot retrieve localdate time", sqlEx);
        }
    }
    
    void mapEntity(ResultSet rs, FF4jEntity<?> e) {
        try {
            e.setCreationDate(getLocalDateTime(rs, JdbcConstants.COLUMN_CREATED));
            e.setLastModified(getLocalDateTime(rs, JdbcConstants.COLUMN_LASTMODIFIED));
            e.setOwner(rs.getString(JdbcConstants.COLUMN_OWNER));
            e.setDescription(rs.getString(JdbcConstants.COLUMN_DESCRIPTION));
        } catch (SQLException sqlEx) {
            throw new IllegalArgumentException("Cannot map entity ", sqlEx);
        }
    }
    
    void populateEntity(PreparedStatement stmt, FF4jEntity<?> ent) {
        try {
            // Feature uid
            stmt.setString(1, ent.getUid());
            // Creation Date
            stmt.setTimestamp(2, TimeUtils.asSqlTimeStamp(ent.getCreationDate().get()));
            // Last Modified Date
            stmt.setTimestamp(3, TimeUtils.asSqlTimeStamp(ent.getLastModifiedDate().get()));
            // Owner
            stmt.setString(4, ent.getOwner().orElse(null));
            // Description
            stmt.setString(5, ent.getDescription().orElse(null));
        } catch (SQLException sqlEx) {
            throw new IllegalArgumentException("Cannot populate entity ", sqlEx);
        }
    }

}
