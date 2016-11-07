package org.ff4j.jdbc;

import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_ACTION;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_DURATION;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_HOSTNAME;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_KEYS;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_NAME;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_SOURCE;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_TIME;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_TYPE;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_USER;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_UUID;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_EVENT_VALUE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ff4j.audit.Event;
import org.ff4j.exception.AuditAccessException;
import org.ff4j.mapper.EventMapper;
import org.ff4j.utils.MappingUtil;
import org.ff4j.utils.Util;

/**
 * Map resultset into {@link Event}
 *
 * @author Cedrick Lunven (@clunven)
 */
public class JdbcEventMapper implements EventMapper < PreparedStatement, ResultSet> {
  
    private Connection sqlConn = null;
            
    private JdbcQueryBuilder queryBuilder = null;
    
    public JdbcEventMapper(Connection sqlConn, JdbcQueryBuilder qbd) {
        this.sqlConn      = sqlConn;
        this.queryBuilder = qbd;
    }
    
    /** {@inheritDoc} */
    @Override
    public PreparedStatement toStore(Event evt) {
        PreparedStatement stmt = null;
        try {
            int idx = 9;
            Map < Integer, String > statementParams = new HashMap<Integer, String>();
            
            StringBuilder sb = new StringBuilder("INSERT INTO " + queryBuilder.getTableNameAudit() + 
                    "(EVT_UUID,EVT_TIME,EVT_TYPE,EVT_NAME,EVT_ACTION,EVT_HOSTNAME,EVT_SOURCE,EVT_DURATION");
            if (Util.hasLength(evt.getUser())) {
                sb.append(", EVT_USER");
                statementParams.put(idx, evt.getUser());
                idx++;
            }
            if (Util.hasLength(evt.getValue())) {
                sb.append(", EVT_VALUE");
                statementParams.put(idx, evt.getValue());
                idx++;
            }
            if (!evt.getCustomKeys().isEmpty()) {
                sb.append(", EVT_KEYS");
                statementParams.put(idx, MappingUtil.fromMap(evt.getCustomKeys()));
                idx++;
            }            
            sb.append(") VALUES (?");
            for(int offset = 1; offset < idx-1;offset++) {
                sb.append(",?");
            }
            sb.append(")");
            stmt = sqlConn.prepareStatement(sb.toString());
            stmt.setString(1, evt.getUuid());
            stmt.setTimestamp(2, new java.sql.Timestamp(evt.getTimestamp()));
            stmt.setString(3, evt.getType());
            stmt.setString(4, evt.getName());
            stmt.setString(5, evt.getAction());
            stmt.setString(6, evt.getHostName());
            stmt.setString(7, evt.getSource());
            stmt.setLong(8, evt.getDuration());
            for (int id = 9;id < idx;id++) {
                stmt.setString(id, statementParams.get(id));
            }
        } catch(SQLException sqlEx) {
            throw new AuditAccessException("Cannot create statement to create event", sqlEx);
        }
        return stmt;
    }

    /**
     * Unmarshall a resultset to Event.
     *
     * @param rs
     *      current line
     * @return
     *      bean populated
     * @throws SQLException
     *      cannot read SQL result
     */
    @Override
    public Event fromStore(ResultSet rs) {
        try {
            Event evt = new Event();
            evt.setUuid(rs.getString(COL_EVENT_UUID));
            evt.setTimestamp(rs.getTimestamp(COL_EVENT_TIME).getTime());
            evt.setType(rs.getString(COL_EVENT_TYPE));
            evt.setName(rs.getString(COL_EVENT_NAME));
            evt.setAction(rs.getString(COL_EVENT_ACTION));
            evt.setHostName(rs.getString(COL_EVENT_HOSTNAME));
            evt.setSource(rs.getString(COL_EVENT_SOURCE));
            evt.setDuration(rs.getLong(COL_EVENT_DURATION));
            evt.setUser(rs.getString(COL_EVENT_USER));
            evt.setValue(rs.getString(COL_EVENT_VALUE));
            evt.setCustomKeys(MappingUtil.toMap(rs.getString(COL_EVENT_KEYS)));
            return evt;
        } catch(SQLException sqlEx) {
            throw new AuditAccessException("Cannot map result to Event", sqlEx);
        }
        
    }

}
