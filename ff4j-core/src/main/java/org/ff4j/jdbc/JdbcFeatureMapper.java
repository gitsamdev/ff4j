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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;

import org.ff4j.exception.FeatureAccessException;
import org.ff4j.feature.Feature;
import org.ff4j.feature.ToggleStrategy;
import org.ff4j.jdbc.JdbcConstants.FeaturesColumns;
import org.ff4j.mapper.FeatureMapper;
import org.ff4j.utils.JsonUtils;
import org.ff4j.utils.Util;

/**
 * Map resultset into {@link Feature}
 *
 * @author Cedrick Lunven (@clunven)
 */
public class JdbcFeatureMapper extends AbstractJdbcMapper implements FeatureMapper< PreparedStatement, ResultSet > {
    
    /**
     * Constructor with parameters.
     *
     * @param sqlConn
     *      connection sql
     * @param qbd
     *      query builder
     */
    public JdbcFeatureMapper(Connection sqlConn, JdbcQueryBuilder qbd) {
        super(sqlConn, qbd);
    }
    
    /** {@inheritDoc} */
    @Override
    public PreparedStatement toStore(Feature feature) {
        PreparedStatement ps;
        try {
            ps = sqlConn.prepareStatement(queryBuilder.sqlInsertFeature());
            // Feature uid
            ps.setString(1, feature.getUid());
            // Creation Date
            ps.setTimestamp(2, Util.asSqlTimeStamp(feature.getCreationDate().get()));
            // Last Modified Date
            ps.setTimestamp(3, Util.asSqlTimeStamp(feature.getLastModifiedDate().get()));
            // Owner
            ps.setString(4, feature.getOwner().orElse(null));
            // Description
            ps.setString(5, feature.getDescription().orElse(null));
            // Enable
            ps.setInt(6, feature.isEnable() ? 1 : 0);
            // Flipping Strategy + InitParams
            String strategy  = null;
            String initParam = null;
            if (feature.getFlippingStrategy().isPresent()) {
                ToggleStrategy fs = feature.getFlippingStrategy().get();
                strategy  = fs.getClass().getCanonicalName();
                initParam = JsonUtils.mapAsJson(fs.getInitParams());
            }
            // Classname for flipping strategy
            ps.setString(7, strategy);
            // Init param for flipping strategy
            ps.setString(8, initParam);
            // GroupName
            ps.setString(9, feature.getGroup().orElse(null));
            
        } catch (SQLException sqlEx) {
            throw new FeatureAccessException("Cannot create statement to create feature", sqlEx);
        }
        return ps;
    }

    /**
     * Map feature result to bean.
     * 
     * @param rs
     *            current resultSet
     * @return current Feature without roles
     * @throws SQLException
     *             error accured when parsing resultSet
     */
    @Override
    public Feature fromStore(ResultSet rs) {
        try {
            Feature f = new Feature(rs.getString(FeaturesColumns.UID.colname()))
                    .setEnable(rs.getInt(FeaturesColumns.ENABLE.colname()) > 0)
                    .setOwner(rs.getString(FeaturesColumns.OWNER.colname()))
                    .setDescription(rs.getString(FeaturesColumns.DESCRIPTION.colname()))
                    .setGroup(rs.getString(FeaturesColumns.GROUPNAME.colname()));
            
            // Creation Date
            Timestamp sqlDate = rs.getTimestamp(FeaturesColumns.CREATED.colname());
            f.setCreationDate(Util.asLocalDateTime(sqlDate));
            
            // Last Modified Date
            Timestamp sqlLastDate = rs.getTimestamp(FeaturesColumns.LASTMODIFIED.colname());
            f.setLastModified(Util.asLocalDateTime(sqlLastDate));
           
            // FlippingStrategy
            String strategy = rs.getString(FeaturesColumns.STRATEGY.colname());
            if (Util.hasLength(strategy)) {
                Map < String, String > initParams = JsonUtils.jsonAsMap(rs.getString(FeaturesColumns.INITPARAMS.colname()));
                f.setFlippingStrategy(ToggleStrategy.of(f.getUid(), strategy, initParams));
            }
            return f;
        } catch(SQLException sqlEx) {
            throw new FeatureAccessException("Cannot create statement to create event", sqlEx);
        }
    }

}
