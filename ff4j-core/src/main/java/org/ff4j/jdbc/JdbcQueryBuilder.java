package org.ff4j.jdbc;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.ff4j.jdbc.JdbcConstants.AuditTrailColumns;
import org.ff4j.jdbc.JdbcConstants.FeaturePropertyColumns;
import org.ff4j.jdbc.JdbcConstants.FeatureStrategyColumns;
import org.ff4j.jdbc.JdbcConstants.FeaturePermissionsColumns;
import org.ff4j.jdbc.JdbcConstants.FeaturesColumns;
import org.ff4j.jdbc.JdbcConstants.GlobalPermissionsColumns;
import org.ff4j.jdbc.JdbcConstants.FeatureUsageColumns;
import org.ff4j.jdbc.JdbcConstants.PropertyColumns;
import org.ff4j.jdbc.JdbcConstants.PropertyPermissionsColumns;
import org.ff4j.jdbc.JdbcConstants.PropertyPropertyColumns;
import org.ff4j.jdbc.JdbcConstants.SQLTypes;
import org.ff4j.jdbc.JdbcConstants.SqlTableColumns;
import org.ff4j.jdbc.JdbcConstants.UserColumns;
import org.ff4j.jdbc.JdbcConstants.UserGroupColumns;
import org.ff4j.jdbc.JdbcConstants.UserMemberOfColumns;
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
    public String getTableNameAuditTrail() {
        return getTableName(AuditTrailColumns.UID.tableName());
    }
    
    /**
     * Table name for audit.
     *
     * @return
     *     Table name for audit
     */
    public String getTableNameFeatureUsage() {
        return getTableName(FeatureUsageColumns.UID.tableName());
    }
    
    /**
     * Table name for roles.
     *
     * @return
     *     Table name for roles
     */
    public String getTableNameFeaturePermissions() {
        return getTableName(FeaturePermissionsColumns.RIGHTS.tableName());
    }
    
    /**
     * Table name for roles.
     *
     * @return
     *     Table name for roles
     */
    public String getTableNamePropertyPermissions() {
        return getTableName(PropertyPermissionsColumns.RIGHTS.tableName());
    }
    
    /**
     * Table name for roles.
     *
     * @return
     *     Table name for roles
     */
    public String getTableNamePermissions() {
        return getTableName(GlobalPermissionsColumns.RIGHTS.tableName());
    }

    /**
     * Table name for custom properties.
     *
     * @return
     *     Table name for custom properties.
     */
    public String getTableNameFeatureProperties() {
        return getTableName(FeaturePropertyColumns.UID.tableName());
    }
    
    public String sqlSelectFeatureAccessControlList() {
        return null;
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
    
    private String sqlDropTable(String tableName) {
        StringBuilder sb = new StringBuilder("DROP TABLE ");
        sb.append(getTableName(tableName));
        sb.append(";\n");
        return sb.toString();
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
    public String sqlCreateTablePermission() {
        return sqlCreateTable(GlobalPermissionsColumns.values());
    }
    
    /** Create table role. */
    public String sqlCreateTableFeaturePermission() {
        return sqlCreateTable(FeaturePermissionsColumns.values());
    }
    
    /** Create table role. */
    public String sqlCreateTablePropertyPermission() {
        return sqlCreateTable(PropertyPermissionsColumns.values());
    }
    
    /** Create table custom properties. */
    public String sqlCreateTableFeatureProperties() {
        return sqlCreateTable(FeaturePropertyColumns.values());
    }
    
    /** Create table custom properties. */
    public String sqlCreateTablePropertyProperties() {
        return sqlCreateTable(PropertyPropertyColumns.values());
    }
    
    /** Create table custom properties. */
    public String sqlCreateTableFeatureStrategy() {
        return sqlCreateTable(FeatureStrategyColumns.values());
    }
    
    /** Create table custom properties. */
    public String sqlCreateTableProperties() {
        return sqlCreateTable(PropertyColumns.values());
    }
    
    /** Create table audit. */
    public String sqlCreateTableAuditTrail() {
        return sqlCreateTable(AuditTrailColumns.values());
    }
    
    /** Create table metrics. */
    public String sqlCreateTableFeatureUsage() {
        return sqlCreateTable(FeatureUsageColumns.values());
    }
    
    /** Create table metrics. */
    public String sqlCreateTableUsers() {
        return sqlCreateTable(UserColumns.values());
    }
    
    /** Create table metrics. */
    public String sqlCreateTableUserGroups() {
        return sqlCreateTable(UserGroupColumns.values());
    }
    
    /** Create table metrics. */
    public String sqlCreateTableUserMemberOf() {
        return sqlCreateTable(UserMemberOfColumns.values());
    }
    
    /** All SQL Script. */
    public String sqlCreateSchema() {
        return new StringBuilder()
                
                .append(sqlCreateTableFeature())
                .append("\n")
                .append(sqlCreateTableFeatureProperties())
                .append("\n")
                .append(sqlCreateTableFeaturePermission())
                .append("\n")
                .append(sqlCreateTableFeatureStrategy())
                .append("\n")
                
                .append(sqlCreateTableProperties())
                .append("\n")
                .append(sqlCreateTablePropertyProperties())
                .append("\n")
                .append(sqlCreateTablePropertyPermission())
                .append("\n")
                
                .append(sqlCreateTableAuditTrail())
                .append("\n")
                .append(sqlCreateTableFeatureUsage())
                .append("\n")
                .append(sqlCreateTablePermission())
                .append("\n")
                
                .append(sqlCreateTableUsers())
                .append("\n")
                .append(sqlCreateTableUserGroups())
                .append("\n")
                .append(sqlCreateTableUserMemberOf())
                .append("\n")
                
                .toString();
    }
    
    /** All SQL Script. */
    public String sqlDropSchema() {
        return new StringBuilder()
                // Security tables
                .append(sqlDropTable(UserMemberOfColumns.REF_USER.tableName()))
                .append(sqlDropTable(UserGroupColumns.NAME.tableName()))
                .append(sqlDropTable(UserColumns.UID.tableName()))
                .append(sqlDropTable(GlobalPermissionsColumns.RIGHTS.tableName()))
                // Features Tables
                .append(sqlDropTable(FeatureStrategyColumns.FEATURE.tableName()))
                .append(sqlDropTable(FeaturePropertyColumns.FEATURE.tableName()))
                .append(sqlDropTable(FeaturePermissionsColumns.RIGHTS.tableName()))
                .append(sqlDropTable(FeaturesColumns.UID.tableName()))
                // Properties Tables (single strategy included in Property table)
                .append(sqlDropTable(PropertyPropertyColumns.PROPERTY.tableName()))
                .append(sqlDropTable(PropertyPermissionsColumns.PROPERTY.tableName()))
                .append(sqlDropTable(PropertyColumns.UID.tableName()))
                // Audit Tables
                .append(sqlDropTable(FeatureUsageColumns.UID.tableName()))
                .append(sqlDropTable(AuditTrailColumns.UID.tableName()))
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
    
    public String sqlInsertAuditTrail() {
        return sqlInsert(AuditTrailColumns.values());
    }
    
    public String sqlInsertMetrics() {
        return sqlInsert(FeatureUsageColumns.values());
    }
    
    public String sqlInsertProperty() {
        return sqlInsert(PropertyColumns.values());
    }
    
    public String sqlInsertCustomProperties() {
        return sqlInsert(FeaturePropertyColumns.values());
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
        return sqlSelect(false, FeaturePropertyColumns.values());
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
    public String sqlSelectPermissionOfFeature() {
        return sqlSelectWhere(true, FeaturePermissionsColumns.FEATURE, FeaturePermissionsColumns.values());
    }
    
    /** Roles for a feature. */
    public String sqlSelectPermissionOfProperty() {
        return sqlSelectWhere(true, PropertyPermissionsColumns.PROPERTY, PropertyPermissionsColumns.values());
    }
    
    /** Get all groups. */
    public String sqlSelectglobalPermissions() {
        return sqlSelect(false, GlobalPermissionsColumns.values());
    }
    
    /** Roles for a feature. */
    public String sqlSelectCustomPropertiesOfFeature() {
        return sqlSelectWhere(false, FeaturePropertyColumns.UID, FeaturePropertyColumns.values());
    }
    
    public String sqlStrategyOfFeature() {
        return sqlSelectWhere(false, FeatureStrategyColumns.FEATURE, FeatureStrategyColumns.values());
    }
    
    /** Roles for a feature. */
    public String sqlSelectCustomPropertyOfFeature() {
        return sqlSelect(false, FeaturePropertyColumns.values()) + 
               sqlWhere(FeaturePropertyColumns.UID) + " AND " +
               FeaturePropertyColumns.UID + " = ?";
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
        return sqlSelectWhere(false, AuditTrailColumns.UID, AuditTrailColumns.values());
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
        Util.requireNotNull(column);
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
    
    public String sqlDeleteAllRolesOfFeature() {
        return sqlDeleteWhere(FeaturePermissionsColumns.FEATURE);
    }
    
    public String sqlDeleteAllCustomPropertiesOfFeature() {
        return sqlDeleteWhere(FeaturePropertyColumns.UID);
    }
    
    public String sqlDeletePropertyOfFeature() {
        return sqlDeleteWhere(FeaturePropertyColumns.UID, FeaturePropertyColumns.UID);
    }
    
    public String sqlDeleteAllCustomProperties() {
        return sqlDeleteAll(FeaturePropertyColumns.UID);
    }
    
    public String sqlDeleteProperty() {
        return sqlDeleteWhere(PropertyColumns.UID);
    }
    
    public String sqlDeleteAllProperties() {
        return sqlDeleteAll(PropertyColumns.UID);
    }
    
    public String sqlDeleteAuditEvent() {
        return sqlDeleteWhere(AuditTrailColumns.UID);
    }
}
