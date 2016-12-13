package org.ff4j.jdbc;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.ff4j.jdbc.JdbcConstants.AuditColumns;
import org.ff4j.jdbc.JdbcConstants.CustomPropertyColumns;
import org.ff4j.jdbc.JdbcConstants.FeaturesColumns;
import org.ff4j.jdbc.JdbcConstants.MetricsColumns;
import org.ff4j.jdbc.JdbcConstants.PropertyColumns;
import org.ff4j.jdbc.JdbcConstants.RolesColumns;
import org.ff4j.jdbc.JdbcConstants.SQLTypes;
import org.ff4j.jdbc.JdbcConstants.SqlTableColumns;
import org.ff4j.utils.Util;

/**
 * Create JDBC queries for FF4J with capabilities to 
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class JdbcQueryBuilder {
	
    /** table prefix. */
	public String tablePrefix = "FF4J_";
	
	/** table suffix. */
	public String tableSuffix = "";

	/** 
	 * Default constructor. 
	 **/
	public JdbcQueryBuilder() {
	}
	
	/**
	 * Overriding Builder.
	 *
	 * @param prefix
	 * 		table prefix
	 * @param suffix
	 * 		table suffix
	 */
	public JdbcQueryBuilder(String prefix, String suffix) {
		this.tablePrefix = prefix;
		this.tableSuffix = suffix;
	}
	
    // ---------------------------------
    // ---------- TABLES  --------------
    // ---------------------------------
	
	/**
	 * Prefix and suffix table Names.
	 * 
	 * @param coreName
	 *         current name
	 * @return
	 *         new table name
	 */
	protected String getTableName(String coreName) {
		return tablePrefix + coreName + tableSuffix;
	}
	
	/**
     * Table name for features.
     *
     * @return
     *     Table name for features
     */
    public String getTableNameFeatures() {
        return getTableName(FeaturesColumns.UID.tableName());
    }
    
    /**
     * Table name for audit.
     *
     * @return
     *     Table name for audit
     */
    public String getTableNameAudit() {
        return getTableName(AuditColumns.UID.tableName());
    }
    
    /**
     * Table name for audit.
     *
     * @return
     *     Table name for audit
     */
    public String getTableNameMetrics() {
        return getTableName(MetricsColumns.UID.tableName());
    }
    
    /**
     * Table name for roles.
     *
     * @return
     *     Table name for roles
     */
    public String getTableNameRoles() {
        return getTableName(RolesColumns.FEATURE_UID.tableName());
    }

    /**
     * Table name for custom properties.
     *
     * @return
     *     Table name for custom properties.
     */
    public String getTableNameCustomProperties() {
        return getTableName(CustomPropertyColumns.UID.tableName());
    }
    
    /**
     * Table name for properties.
     *
     * @return
     *     Table name for properties.
     */
    public String getTableNameProperties() {
        return getTableName(PropertyColumns.UID.tableName());
    }
    
    /**
     * Create the SQL Query to produce table from a set of {@link SqlTableColumns}.
     * 
     * @param columns
     *      columns of the table
     * @return
     *      the SQL query
     */
    private String sqlCreateTable(SqlTableColumns... columns) {
        Util.assertNotEmpty(columns);
        SqlTableColumns tableColumn = columns[0];
        StringBuilder sb = new StringBuilder("CREATE TABLE ");
        sb.append(getTableName(tableColumn.tableName()));
        sb.append(" ( \n");
        Arrays.stream(columns).forEach(col -> { 
            sb.append(" " + col.colname() + " \t" + col.type().name());
            if (col.size() !=0 ) {
                sb.append("(" + col.size() + ")");
            }
            if (!col.nullable()) {
                sb.append(" NOT NULL");
            }
            sb.append(",\n");
        });
        sb.append(" PRIMARY KEY ");
        sb.append(tableColumn.primaryKey()
                    .stream().map(SqlTableColumns::colname)
                    .collect(Collectors.joining(",","(",")")));
        
        tableColumn.foreignKey().ifPresent(map -> {
            map.entrySet().stream().forEach(entry -> {
                sb.append(",\n FOREIGN KEY (");
                sb.append(entry.getKey().colname());
                sb.append(") REFERENCES ");
                sb.append(getTableName(entry.getValue().tableName()));
                sb.append("(" + entry.getValue().colname() + ")");
            });
        });
        sb.append("\n);");
        return sb.toString();
    }
    
    /** Create table features. */
    public String sqlCreateTableFeature() {
        return sqlCreateTable(FeaturesColumns.values());
    }
    
    /** Create table role. */
    public String sqlCreateTableRole() {
        return sqlCreateTable(RolesColumns.values());
    }
    
    /** Create table custom properties. */
    public String sqlCreateTableCustomProperties() {
        return sqlCreateTable(CustomPropertyColumns.values());
    }
    
    /** Create table custom properties. */
    public String sqlCreateTableProperties() {
        return sqlCreateTable(PropertyColumns.values());
    }
    
    /** Create table audit. */
    public String sqlCreateTableAudit() {
        return sqlCreateTable(AuditColumns.values());
    }
    
    /** Create table metrics. */
    public String sqlCreateTableMetrics() {
        return sqlCreateTable(MetricsColumns.values());
    }
    
    /** All SQL Script. */
    public String sqlCreateSchema() {
        return new StringBuilder()
                .append(sqlCreateTableFeature())
                .append("\n")
                .append(sqlCreateTableRole())
                .append("\n")
                .append(sqlCreateTableCustomProperties())
                .append("\n")
                .append(sqlCreateTableProperties())
                .append("\n")
                .append(sqlCreateTableAudit())
                .append("\n")
                .append(sqlCreateTableMetrics())
                .append("\n")
                .toString();
    }

    // ---------------------------------
    // ---------   CREATE    -----------
    // ---------------------------------
    
    /**
     * Utility to initiate an INSERT query.
     *
     * @param tableName
     *      current table name
     * @param columns
     *      list of columns
     * @return
     *      the sql statement
     */
    private String sqlInsert(SqlTableColumns... columns) {
        Util.assertNotEmpty(columns);
        StringBuilder sb = new StringBuilder().append("INSERT INTO ");
        sb.append(getTableName(columns[0].tableName()));
        sb.append(Arrays.stream(columns)
                    .map(SqlTableColumns::colname)
                    .collect(Collectors.joining(",","(\n",")\n")));
        sb.append(" VALUES");
        sb.append(IntStream.range(0, columns.length)
                    .mapToObj(i-> '?').map(o-> o.toString())
                    .collect(Collectors.joining(",","(",")")));
        return sb.toString();
    }
    
    public String sqlInsertFeature() {
        return sqlInsert(FeaturesColumns.values());
    }
    
    public String sqlInsertRoles() {
        return sqlInsert(RolesColumns.values());
    }
    
    public String sqlInsertAudit() {
        return sqlInsert(AuditColumns.values());
    }
    
    public String sqlInsertMetrics() {
        return sqlInsert(MetricsColumns.values());
    }
    
    public String sqlInsertProperty() {
        return sqlInsert(PropertyColumns.values());
    }
    
    public String sqlInsertCustomProperties() {
        return sqlInsert(CustomPropertyColumns.values());
    }
    
    // ---------------------------------
    // -------     READ     -----------
    // ---------------------------------
    
    /** Select all element for a table. */
    private String sqlSelect(boolean distinct, SqlTableColumns... columns) {
        Util.assertNotEmpty(columns);
        SqlTableColumns tableColumn = columns[0];
        return new StringBuilder("SELECT ")
                .append(distinct ? "DISTINCT (" : "")
                .append(Arrays.stream(columns).map(SqlTableColumns::colname).collect(Collectors.joining(",")))
                .append(distinct ? ")" : "")
                .append(" FROM ")
                .append(getTableName(tableColumn.tableName())).toString();
    }
    
    private String sqlWhere(SqlTableColumns... condition) {
        if (condition == null || condition.length == 0) return "";
        StringBuilder sb =  new StringBuilder(" WHERE " + sqlWhereCondition(condition[0]));
        for (int i = 1; i < condition.length; i++) {
            sb.append(" AND ").append(sqlWhereCondition(condition[i]));
        }
        return sb.toString();
    }
    
    private String sqlWhereCondition(SqlTableColumns condition) {
        StringBuilder sb = new StringBuilder("(");
        sb.append(condition.colname());
        sb.append(condition.type().equals(SQLTypes.VARCHAR) ? " LIKE ?" : " = ?");
        return sb.append(")").toString();
    }
    
    /** Select all elements with where condition. */
    private String sqlSelectWhere(boolean distinct, SqlTableColumns condition, SqlTableColumns... columns) {
        return sqlSelect(distinct, columns) + sqlWhere(condition);
    }
    
    // ----- Features -----
    
    /** Get all features. */
    public String sqlSelectAllFeatures() {
        return sqlSelect(false, FeaturesColumns.values());
    }
    
    /** Get all features. */
    public String sqlSelectAllCustomProperties() {
        return sqlSelect(false, CustomPropertyColumns.values());
    }
    
    /** Get all features. */
    public String sqlSelectFeaturesOfGroup() {
        return sqlSelectWhere(false, FeaturesColumns.GROUPNAME, FeaturesColumns.values());
    }
    
    /** Get a feature by its id. */
    public String sqlSelectFeatureById() {
       return sqlSelectWhere(false, FeaturesColumns.UID, FeaturesColumns.values());
    }
    
    /** Get all groups. */
    public String sqlSelectAllGroups() {
        return sqlSelect(true, FeaturesColumns.GROUPNAME);
    }
    
    /** Roles for a feature. */
    public String sqlSelectRolesOfFeature() {
        return sqlSelectWhere(true, RolesColumns.FEATURE_UID, RolesColumns.ROLE);
    }
    
    /** Get all groups. */
    public String sqlSelectAllRoles() {
        return sqlSelect(false, RolesColumns.values());
    }
    
    /** Roles for a feature. */
    public String sqlSelectCustomPropertiesOfFeature() {
        return sqlSelectWhere(false, CustomPropertyColumns.FEATURE_UID, CustomPropertyColumns.values());
    }
    
    /** Roles for a feature. */
    public String sqlSelectCustomPropertyOfFeature() {
        return sqlSelect(false, CustomPropertyColumns.values()) + 
               sqlWhere(CustomPropertyColumns.UID) + " AND " +
               CustomPropertyColumns.FEATURE_UID + " = ?";
    }
    
    // ----- Properties -----
    
    /** Get all properties. */
    public String sqlSelectAllProperties() {
        return sqlSelect(false, PropertyColumns.values());
    }
    
    /** Check if property exist. */
    public String sqlExistProperty() {
        return sqlCountWhere(PropertyColumns.UID, PropertyColumns.UID);
    }
    
    /** Get an event by its id. */
    public String sqlSelectPropertyById() {
        return sqlSelectWhere(false, PropertyColumns.UID, PropertyColumns.values());
    }
    
    /** Get all property names. */
    public String sqlSelectAllPropertyNames() {
        return sqlSelect(true, PropertyColumns.UID);
    }
    
    /** Get an event by its id. */
    public String sqlSelectAuditById() {
        return sqlSelectWhere(false, AuditColumns.UID, AuditColumns.values());
    }
    
    /** Count element of a table. */
    private String sqlCount(SqlTableColumns column) {
        return "SELECT COUNT(" + column.colname() + ") FROM " + getTableName(column.tableName());
    }
    
    /** Count element of a table. */
    private String sqlCountWhere(SqlTableColumns column, SqlTableColumns condition) {
        return sqlCount(column) + sqlWhere(condition);
    }
    
    // ----- Features -----
    
    /** Count Features. */
    public String sqlCountFeatures() {
        return sqlCount(FeaturesColumns.UID);
    }
    
    /** Check if feature exist. */
    public String sqlExistFeature() {
        return sqlCountWhere(FeaturesColumns.UID, FeaturesColumns.UID);
    }
    
    /** Check if feature exist. */
    public String sqlExistGroup() {
        return sqlCountWhere(FeaturesColumns.UID, FeaturesColumns.GROUPNAME);
    }
    
    // ---------------------------------
    // -------     UPDATE    -----------
    // ---------------------------------
    
    /** Update a table . */
    private String sqlUpdate(SqlTableColumns condition, SqlTableColumns... tobeUpdated) {
        Util.assertNotEmpty(tobeUpdated);
        StringBuilder fields = new StringBuilder();
        Arrays.stream(tobeUpdated).map(SqlTableColumns::colname).forEach(colname -> {
            fields.append(" AND " + colname + " = ?");
        });
        return "UPDATE " + getTableName(condition.tableName()) + " SET " + fields.substring(4) + sqlWhere(condition);
    }
    
    public String updateProperty() {
        StringBuilder sb = new StringBuilder();
        sb.append("UPDATE ");
        sb.append(getTableNameProperties());
        sb.append(" SET CURRENTVALUE = ? WHERE PROPERTY_ID = ?");
        return sb.toString();
    }
    
    // ----- Features -----
    
    /** Enable a feature. */
    public String sqlEditFeatureStatus() {
        return sqlUpdate(FeaturesColumns.UID, FeaturesColumns.ENABLE);
	}
    
    /** Enable a group. */
    public String sqlEditGroupStatus() {
        return sqlUpdate(FeaturesColumns.GROUPNAME, FeaturesColumns.ENABLE);
    }
    
    /** Update group name for dedicated feature uid. */
	public String sqlEditFeatureToGroup() {
	    return sqlUpdate(FeaturesColumns.UID, FeaturesColumns.GROUPNAME);
	}
	
	// ---------------------------------
    // -------     DELETE    -----------
    // ---------------------------------
 
    private String sqlDeleteAll(SqlTableColumns column) {
        Util.assertNotNull(column);
        StringBuilder sb =  new StringBuilder().append("DELETE FROM ");
        sb.append(getTableName(column.tableName()));
        return sb.toString();
    }
    
    private String sqlDeleteWhere(SqlTableColumns... condition) {
        return sqlDeleteAll(condition[0]) + sqlWhere(condition);
    }

    public String sqlDeleteFeature() {
        return sqlDeleteWhere(FeaturesColumns.UID);
    }
    
    public String sqlDeleteAllFeatures() {
        return sqlDeleteAll(FeaturesColumns.UID);
    }
    
    public String sqlDeleteRoleOfFeature() {
        return sqlDeleteWhere(RolesColumns.FEATURE_UID, RolesColumns.ROLE);
    }
    
    public String sqlDeleteAllRolesOfFeature() {
        return sqlDeleteWhere(RolesColumns.FEATURE_UID);
    }
    
    public String sqlDeleteAllRoles() {
        return sqlDeleteAll(RolesColumns.FEATURE_UID);
    }
    
    public String sqlDeleteAllCustomPropertiesOfFeature() {
        return sqlDeleteWhere(CustomPropertyColumns.FEATURE_UID);
    }
    
    public String sqlDeletePropertyOfFeature() {
        return sqlDeleteWhere(CustomPropertyColumns.UID, CustomPropertyColumns.FEATURE_UID);
    }
    
    public String sqlDeleteAllCustomProperties() {
        return sqlDeleteAll(CustomPropertyColumns.FEATURE_UID);
    }
    
    public String sqlDeleteProperty() {
        return sqlDeleteWhere(PropertyColumns.UID);
    }
    
    public String sqlDeleteAllProperties() {
        return sqlDeleteAll(PropertyColumns.UID);
    }
    
    public String sqlDeleteAuditEvent() {
        return sqlDeleteWhere(AuditColumns.UID);
    }
	
    /* ------- AUDIT -------------
	
	public String sqlStartCreateEvent() {
	    StringBuilder sb = new StringBuilder("INSERT INTO " + getTableNameAudit());
	    sb.append("(" + COL_EVENT_UID + "," + COL_EVENT_TIME   + "," + COL_EVENT_TYPE);
	    sb.append("," + COL_EVENT_NAME + "," + COL_EVENT_ACTION + "," + COL_EVENT_HOSTNAME);
	    sb.append("," + COL_EVENT_SOURCE + "," + COL_EVENT_DURATION);
	    return sb.toString();
	}
	
	public String sqlFindAllEvents() {
        return "SELECT * FROM " + getTableNameAudit();
    }
	
	public String sqlExistEvent() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT COUNT(" + COL_EVENT_UID + ") FROM ");
        sb.append(getTableNameAudit());
        sb.append(" WHERE " + COL_EVENT_UID + " = ?");
        return sb.toString();
    }
    
   
	
	public String getEventByUuidQuery() {
	     StringBuilder sb = new StringBuilder();
	     sb.append("SELECT * FROM ");
	     sb.append(getTableNameAudit());
	     sb.append(" WHERE " + COL_EVENT_UID + " LIKE ?");
	     return sb.toString();
	}
	
	public String getPurgeFeatureUsageQuery(EventQueryDefinition eqd) {
	    StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(getTableNameAudit());
        sb.append(buildWhereClause(eqd, true, false));
        return sb.toString();
	}
	
	public String getSelectFeatureUsageQuery(EventQueryDefinition eqd) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(getTableNameAudit());
        sb.append(buildWhereClause(eqd, true, false));
        return sb.toString();
    }
	
    public String getPurgeAuditTrailQuery(EventQueryDefinition eqd) {
        StringBuilder sb = new StringBuilder();
        sb.append("DELETE FROM ");
        sb.append(getTableNameAudit());
        sb.append(buildWhereClause(eqd, false, true));
        return sb.toString();
    }
	
	public String getSelectAuditTrailQuery(EventQueryDefinition eqd) {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT * FROM ");
        sb.append(getTableNameAudit());
        sb.append(buildWhereClause(eqd, false, true));
        return sb.toString();
    }
	
	public String getHitCount(String columName) {
	    StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(" + COL_EVENT_UID + ") as NB, " + columName + " FROM ");
        sb.append(getTableNameAudit());
        sb.append(" WHERE (" + COL_EVENT_TYPE   + " LIKE '" + EventConstants.TARGET_FEATURE  + "') ");
        sb.append(" AND   (" + COL_EVENT_ACTION + " LIKE '" + EventConstants.ACTION_CHECK_OK + "') ");
        sb.append(" AND   (" + COL_EVENT_TIME + "> ?) ");
        sb.append(" AND   (" + COL_EVENT_TIME + "< ?)");
        sb.append(" GROUP BY " + columName);
        return sb.toString();
	}
	
	public String getFeaturesHitCount() {
	    return getHitCount(COL_EVENT_NAME);
    }
	
	public String getHostHitCount() {
	    return getHitCount(COL_EVENT_HOSTNAME);
    }
	
	public String getUserHitCount() {
	    return getHitCount(COL_OWNER);
    }
	
	public String getSourceHitCount() {
        return getHitCount(COL_EVENT_SOURCE);
    }
	
	// -------
   
    public String getFeatureDistributionAudit() {
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT count(" + COL_EVENT_UID + ") as NB, " + COL_EVENT_ACTION + " FROM ");
        sb.append(getTableNameAudit());
        sb.append(" WHERE (" + COL_EVENT_TYPE + " LIKE '" + EventConstants.TARGET_FEATURE  + "') ");
        sb.append(" AND   (" + COL_EVENT_NAME + " LIKE ?) ");
        sb.append(" AND   (" + COL_EVENT_TIME + "> ?) ");
        sb.append(" AND   (" + COL_EVENT_TIME + "< ?)");
        sb.append(" GROUP BY " + COL_EVENT_ACTION);
        return sb.toString();
    }
    
	private String buildClauseIn(Collection < String> elements) {
	    boolean first = true;
	    StringBuilder sb = new StringBuilder("(");
	    for (String el : elements) {
	        if (!first) {
	            sb.append(",");
	        }
	        sb.append("'");
	        sb.append(el);
	        sb.append("'");
            first = false;
        }
	    sb.append(")");
	    return sb.toString();
	}
	
    public String buildWhereClause(EventQueryDefinition qDef, boolean filterForCheck, boolean filterAuditTrail) {
        StringBuilder sb = new StringBuilder();
        sb.append(" WHERE (" + COL_EVENT_TIME + "> ?) ");
        sb.append(" AND   (" + COL_EVENT_TIME + "< ?) ");
        // If a dedicated filter is there use it
        if (qDef.getActionFilters().isEmpty()) {
            if (filterForCheck) {
                qDef.getActionFilters().add(ACTION_CHECK_OK);
            }
            if (filterAuditTrail) {
                qDef.getActionFilters().add(ACTION_CONNECT);
                qDef.getActionFilters().add(ACTION_DISCONNECT);
                qDef.getActionFilters().add(ACTION_TOGGLE_ON);
                qDef.getActionFilters().add(ACTION_TOGGLE_OFF);
                qDef.getActionFilters().add(ACTION_CREATE);
                qDef.getActionFilters().add(ACTION_DELETE);
                qDef.getActionFilters().add(ACTION_UPDATE);
                qDef.getActionFilters().add(ACTION_CLEAR);
            }
        }
        if (qDef.getActionFilters() != null && !qDef.getActionFilters().isEmpty()) {
            sb.append(" AND (" + COL_EVENT_ACTION + " IN ");
            sb.append(buildClauseIn(qDef.getActionFilters()));
            sb.append(")");
        }
        if (qDef.getHostFilters() != null && !qDef.getHostFilters().isEmpty()) {
            sb.append(" AND (" + COL_EVENT_HOSTNAME + " IN ");
            sb.append(buildClauseIn(qDef.getHostFilters()));
            sb.append(")");
            
        }
        if (qDef.getNamesFilter() != null && !qDef.getNamesFilter().isEmpty()) {
            sb.append(" AND (" + COL_EVENT_NAME + " IN ");
            sb.append(buildClauseIn(qDef.getNamesFilter()));
            sb.append(")");
        }
        if (qDef.getSourceFilters() != null && !qDef.getSourceFilters().isEmpty()) {
            sb.append(" AND (" + COL_EVENT_SOURCE + " IN ");
            sb.append(buildClauseIn(qDef.getSourceFilters()));
            sb.append(")");
        }
        return sb.toString();
    }*/
	
}
