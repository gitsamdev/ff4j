package org.ff4j.jdbc;

import static org.ff4j.jdbc.JdbcStoreConstants.COL_FEAT_DESCRIPTION;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_FEAT_ENABLE;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_FEAT_EXPRESSION;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_FEAT_GROUPNAME;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_FEAT_STRATEGY;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_FEAT_UID;
import static org.ff4j.utils.MappingUtil.toMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

import org.ff4j.exception.FeatureAccessException;
import org.ff4j.feature.Feature;
import org.ff4j.feature.FlippingStrategy;
import org.ff4j.mapper.FeatureMapper;
import org.ff4j.utils.Util;

/**
 * Map resultset into {@link Feature}
 *
 * @author Cedrick Lunven (@clunven)
 */
public class JdbcFeatureMapper implements FeatureMapper< PreparedStatement, ResultSet > {
    
    private Connection sqlConn = null;
    
    private JdbcQueryBuilder queryBuilder = null;
    
    public JdbcFeatureMapper(Connection sqlConn, JdbcQueryBuilder qbd) {
        this.sqlConn      = sqlConn;
        this.queryBuilder = qbd;
    }
    
    /** {@inheritDoc} */
    @Override
    public PreparedStatement toStore(Feature bean) {
        return null;
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
            boolean enabled = rs.getInt(COL_FEAT_ENABLE) > 0;
            String featUid = rs.getString(COL_FEAT_UID);
            Feature f = new Feature(featUid).setEnable(enabled)
                    .setDescription(rs.getString(COL_FEAT_DESCRIPTION))
                    .setGroup(rs.getString(COL_FEAT_GROUPNAME));
            
            // TODO
            //p.setCreationDate(currentDate);
            //p.setLastModified(currentDate);
            //p.setOwner(owner)
            
            // Strategy
            String strategy = rs.getString(COL_FEAT_STRATEGY);
            if (Util.hasLength(strategy)) {
                Map < String, String > initParams = toMap(rs.getString(COL_FEAT_EXPRESSION));
                f.setFlippingStrategy(FlippingStrategy.instanciate(featUid, strategy, initParams));
            }
            // Role & Custom Properties after
            return f;
        } catch(SQLException sqlEx) {
            throw new FeatureAccessException("Cannot create statement to create event", sqlEx);
        }
    }

}
