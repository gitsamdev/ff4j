package org.ff4j.jdbc;

import static org.ff4j.utils.Util.asLocalDateTime;

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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.ff4j.event.Event;
import org.ff4j.exception.AuditAccessException;
import org.ff4j.jdbc.JdbcConstants.MetricsColumns;
import org.ff4j.mapper.EventMapper;
import org.ff4j.utils.JsonUtils;
import org.ff4j.utils.MutableHitCount;

/**
 * Map resultset into {@link Event}
 *
 * @author Cedrick Lunven (@clunven)
 */
public class JdbcEventMetricsMapper extends AbstractJdbcMapper implements EventMapper < PreparedStatement, ResultSet> {
    
    public JdbcEventMetricsMapper(Connection sqlConn, JdbcQueryBuilder qbd) {
       super(sqlConn, qbd);
    }
    
    /** {@inheritDoc} */
    @Override
    public PreparedStatement toStore(Event evt) {
        PreparedStatement stmt = null;
        try {
            MutableHitCount idx = new MutableHitCount(9);
            Map < Integer, String > statementParams = new HashMap<Integer, String>();
            // Audit ou Metrics ?
            StringBuilder sb = new StringBuilder(queryBuilder.sqlInsertAudit());
            evt.getOwner().ifPresent(user -> {
                sb.append(", " + MetricsColumns.OWNER.colname());
                statementParams.put(idx.get(), user);
                idx.inc();
            });
            evt.getValue().ifPresent(value -> {
                sb.append(", " +  MetricsColumns.VALUE.colname());
                statementParams.put(idx.get(), value);
                idx.inc();
            });
            evt.getCustomKeys().ifPresent(cp -> {
                sb.append(", " + MetricsColumns.KEYS.colname());
                statementParams.put(idx.get(), JsonUtils.mapAsJson(cp));
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
            Event evt = new Event(rs.getString(MetricsColumns.UID.colname()));
            evt.setTimestamp(rs.getTimestamp(MetricsColumns.TIMESTAMP.colname()).getTime());
            evt.setType(rs.getString(MetricsColumns.TYPE.colname()));
            evt.setName(rs.getString(MetricsColumns.NAME.colname()));
            evt.setAction(rs.getString(MetricsColumns.ACTION.colname()));
            evt.setHostName(rs.getString(MetricsColumns.HOSTNAME.colname()));
            evt.setSource(rs.getString(MetricsColumns.SOURCE.colname()));
            evt.setDuration(rs.getLong(MetricsColumns.DURATION.colname()));
            evt.setOwner(rs.getString(MetricsColumns.OWNER.colname()));
            evt.setValue(rs.getString(MetricsColumns.VALUE.colname()));
            evt.setCustomKeys(
                    JsonUtils.jsonAsMap(rs.getString(MetricsColumns.KEYS.colname())));
            evt.setCreationDate(
                    asLocalDateTime(rs.getTimestamp(MetricsColumns.CREATED.colname())));
            evt.setLastModified(
                    asLocalDateTime(rs.getTimestamp(MetricsColumns.LASTMODIFIED.colname())));
            return evt;
        } catch(SQLException sqlEx) {
            throw new AuditAccessException("Cannot map result to Event", sqlEx);
        }
        
    }

}
