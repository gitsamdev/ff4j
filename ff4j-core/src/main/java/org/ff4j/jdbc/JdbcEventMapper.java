package org.ff4j.jdbc;

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

import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_ACTION;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_DURATION;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_HOSTNAME;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_KEYS;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_NAME;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_SOURCE;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_TIME;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_TYPE;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_USER;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_UID;
import static org.ff4j.jdbc.JdbcConstants.COL_EVENT_VALUE;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ff4j.audit.Event;
import org.ff4j.audit.MutableHitCount;
import org.ff4j.exception.AuditAccessException;
import org.ff4j.mapper.EventMapper;
import org.ff4j.utils.MappingUtil;

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
            MutableHitCount idx = new MutableHitCount(9);
            Map < Integer, String > statementParams = new HashMap<Integer, String>();
            
            StringBuilder sb = new StringBuilder(queryBuilder.sqlStartCreateEvent());
            evt.getOwner().ifPresent(user -> {
                sb.append(", " + JdbcConstants.COL_EVENT_USER);
                statementParams.put(idx.get(), user);
                idx.inc();
            });
            evt.getValue().ifPresent(value -> {
                sb.append(", " + JdbcConstants.COL_EVENT_VALUE);
                statementParams.put(idx.get(), value);
                idx.inc();
            });
            evt.getCustomKeys().ifPresent(cp -> {
                sb.append(", " + JdbcConstants.COL_EVENT_KEYS);
                statementParams.put(idx.get(), MappingUtil.fromMap(cp));
                idx.inc();
            });       
            sb.append(") VALUES (?");
            for(int offset = 1; offset < idx.get()-1;offset++) {
                sb.append(",?");
            }
            sb.append(")");
            stmt = sqlConn.prepareStatement(sb.toString());
            stmt.setString(1, evt.getUid());
            stmt.setTimestamp(2, new java.sql.Timestamp(evt.getTimestamp()));
            stmt.setString(3, evt.getType());
            stmt.setString(4, evt.getName());
            stmt.setString(5, evt.getAction());
            stmt.setString(6, evt.getHostName());
            stmt.setString(7, evt.getSource());
            stmt.setLong(8, evt.getDuration().orElse(0L));
            for (int id = 9;id < idx.get();id++) {
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
            Event evt = new Event(rs.getString(COL_EVENT_UID));
            evt.setTimestamp(rs.getTimestamp(COL_EVENT_TIME).getTime());
            evt.setType(rs.getString(COL_EVENT_TYPE));
            evt.setName(rs.getString(COL_EVENT_NAME));
            evt.setAction(rs.getString(COL_EVENT_ACTION));
            evt.setHostName(rs.getString(COL_EVENT_HOSTNAME));
            evt.setSource(rs.getString(COL_EVENT_SOURCE));
            evt.setDuration(rs.getLong(COL_EVENT_DURATION));
            evt.setOwner(rs.getString(COL_EVENT_USER));
            evt.setValue(rs.getString(COL_EVENT_VALUE));
            evt.setCustomKeys(MappingUtil.toMap(rs.getString(COL_EVENT_KEYS)));
            return evt;
        } catch(SQLException sqlEx) {
            throw new AuditAccessException("Cannot map result to Event", sqlEx);
        }
        
    }

}
