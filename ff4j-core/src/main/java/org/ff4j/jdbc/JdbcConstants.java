package org.ff4j.jdbc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.ff4j.utils.Util;

/*
 * #%L ff4j-core %% Copyright (C) 2013 Ff4J %% Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. #L%
 */

/**
 * Specialization of a Feature store to add sql query.
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class JdbcConstants {
    
    public static final String COLUMN_UID = "UID";
    
    public static final String COLUMN_CREATED = "CREATED";
    
    public static final String COLUMN_LASTMODIFIED = "LASTMODIFIED";
    
    public static final String COLUMN_OWNER = "OWNER";
    
    public static final String COLUMN_DESCRIPTION = "DESCRIPTION";
    
    /**
     * Representation of the JDBC Table FEATURES.
     */
    public static enum SQLTypes {
        VARCHAR, DATE, DATETIME, INTEGER, TIMESTAMP;
    }
    
    /**
     * Template to be expected by a colum.
     * 
     * @author Cedrick LUNVEN  (@clunven)
     */
    public interface SqlTableColumns {
        
        /** identifier for the column. */
        String colname();
        
        /** sql type. */
        SQLTypes type();
        
        /** size of column or null. */
        int size();
        
        /** if nullable. */
        boolean nullable();
        
        /** underlying table name. */
        String tableName();
        
        /** underlying table primary. */
        List < SqlTableColumns > primaryKey();
        
        /** underlying foreign keys. */
        Optional <Map < SqlTableColumns,SqlTableColumns >> foreignKey();
    }
    
    // ----------------------------------
    // ------- TABLE FEATURES -----------
    // ----------------------------------
    
    /**
     * Representation of the JDBC Table FEATURES.
     */
    public static enum FeaturesColumns implements SqlTableColumns {
        
        // Columns shared by all entities
        UID(COLUMN_UID, SQLTypes.VARCHAR, 100, true),
        CREATED(COLUMN_CREATED, SQLTypes.DATETIME, 0, true),
        LASTMODIFIED(COLUMN_LASTMODIFIED, SQLTypes.DATETIME, 0, true),
        OWNER(COLUMN_OWNER, SQLTypes.VARCHAR,   100, false),
        DESCRIPTION(COLUMN_DESCRIPTION, SQLTypes.VARCHAR, 255, false),
        
        // Specialization for the Feature
        ENABLE("ENABLE", SQLTypes.INTEGER, 0, true),
        GROUPNAME("GROUPNAME", SQLTypes.VARCHAR, 100, false);
        
        /** Column attribute */
        private final String name;
        
        /** Column attribute */
        private final SQLTypes type;
        
        /** Column attribute */
        private final int size;
        
        /** Column attribute */
        private final boolean required;
        
        /**
         * Private constructor.
         *
         * @param pname
         *      column name
         * @param ptype
         *      column type (depends on underlying JDBC DB NUMBER, INTEGER, but still useful 
         * @param psize
         *      column size
         * @param pnullabz
         */
        private FeaturesColumns(String pname, SQLTypes ptype, int psize, boolean pnullable) {
            name = pname;
            type = ptype;
            size = psize;
            required = pnullable;
        }
        
        /** {@inheritDoc} */
        public String colname() { return name; }
        
        /** {@inheritDoc} */
        public SQLTypes type()  { return type; }
        
        /** {@inheritDoc} */
        public int size()       { return size; }
        
        /** {@inheritDoc} */
        public boolean nullable()  { return !required; }
        
        /** {@inheritDoc} */
        public String tableName() { return "FEATURES"; }
        
        /** {@inheritDoc} */
        public List < SqlTableColumns > primaryKey() { return Util.listOf(UID); }
        
        /** {@inheritDoc} */
        public Optional <Map < SqlTableColumns,SqlTableColumns >> foreignKey() { return Optional.empty(); }
    }
    
    // ---------------------------------
    // ------- TABLE ROLES -------------
    // ---------------------------------
    
    /**
     * Representation of the JDBC Table ROLE.
     */
    public static enum RolesColumns  implements SqlTableColumns {
        
        FEATURE_UID("FEAT_UID",   SQLTypes.VARCHAR, 100, true),
        ROLE("ROLE_NAME", SQLTypes.VARCHAR, 100, true);
        
        /** Column attribute */
        private final String name;
        
        /** Column attribute */
        private final SQLTypes type;
        
        /** Column attribute */
        private final int size;
        
        /** Column attribute */
        private final boolean required;
        
        /**
         * Private constructor.
         *
         * @param pname
         *      column name
         * @param ptype
         *      column type (depends on underlying JDBC DB NUMBER, INTEGER, but still useful 
         * @param psize
         *      column size
         * @param pnullabz
         */
        private RolesColumns(String pname, SQLTypes ptype, int psize, boolean pnullable) {
            name = pname;
            type = ptype;
            size = psize;
            required = pnullable;
        }
        
        /** {@inheritDoc} */
        public String colname() { return name; }
        
        /** {@inheritDoc} */
        public SQLTypes type()  { return type; }
        
        /** {@inheritDoc} */
        public int size()       { return size; }
        
        /** {@inheritDoc} */
        public boolean nullable()  { return !required; }
        
        /** {@inheritDoc} */
        public String tableName() { return "ROLES"; }
        
        /** {@inheritDoc} */
        public List < SqlTableColumns > primaryKey() { 
            return Util.listOf(FEATURE_UID, ROLE); 
        }
        
        /** {@inheritDoc} */
        public Optional <Map < SqlTableColumns, SqlTableColumns >> foreignKey() { 
            return Optional.of(Util.mapOf(FEATURE_UID, FeaturesColumns.UID)); 
        }
    }

    // ---------------------------------
    // ----- TABLE PROPERTIES ----------
    // ---------------------------------
   
    /** Representation of the JDBC Table FEATURES. */
    public static enum PropertyColumns implements SqlTableColumns {
        
        UID("PROPERTY_ID", SQLTypes.VARCHAR, 100, true),
        READONLY("READONLY", SQLTypes.INTEGER, 0, true),
        CREATED("CREATED", SQLTypes.DATETIME, 0, true),
        LASTMODIFIED("LASTMODIFIED", SQLTypes.DATETIME, 0, true),
        OWNER("OWNER", SQLTypes.VARCHAR,   100, false),
        DESCRIPTION("DESCRIPTION", SQLTypes.VARCHAR, 255, false),        
        CLAZZ("CLAZZ", SQLTypes.VARCHAR, 255, true),
        VALUE("CURRENTVALUE", SQLTypes.VARCHAR, 255, true),
        STRATEGY("STRATCLASS", SQLTypes.VARCHAR,1000, false),
        INITPARAMS("STRATPARAM", SQLTypes.VARCHAR, 1000, false),
        FIXEDVALUES("FIXEDVALUES", SQLTypes.VARCHAR, 1000, false);
        
        /** Column attribute */
        private final String name;
        /** Column attribute */
        private final SQLTypes type;
        /** Column attribute */
        private final int size;
        /** Column attribute */
        private final boolean required;
        
        /**
         * Private constructor.
         *
         * @param pname
         *      column name
         * @param ptype
         *      column type (depends on underlying JDBC DB NUMBER, INTEGER, but still useful 
         * @param psize
         *      column size
         * @param pnullabz
         */
        private PropertyColumns(String pname, SQLTypes ptype, int psize, boolean pnullable) {
            name = pname;
            type = ptype;
            size = psize;
            required = pnullable;
        }

        /** {@inheritDoc} */
        public String colname() { return name; }
        
        /** {@inheritDoc} */
        public SQLTypes type()  { return type; }
        
        /** {@inheritDoc} */
        public int size()       { return size; }
        
        /** {@inheritDoc} */
        public boolean nullable()  { return !required; }
        
        /** {@inheritDoc} */
        public String tableName() { return "PROPERTIES"; }
        
        /** {@inheritDoc} */
        public List < SqlTableColumns > primaryKey() { 
            return Util.listOf(UID); 
        }
        
        /** {@inheritDoc} */
        public Optional <Map < SqlTableColumns, SqlTableColumns >> foreignKey() { 
            return Optional.empty(); 
        }
    }
    
    // ---------------------------------
    // --- TABLE CUSTOM PROPERTIES  ----
    // ---------------------------------
    
    /**
     * Representation of the JDBC Table FEATURES.
     */
    public static enum CustomPropertyColumns implements SqlTableColumns {
        
        UID("PROPERTY_ID", SQLTypes.VARCHAR, 100, true),
        READONLY("READONLY", SQLTypes.INTEGER, 0, true),
        CREATED("CREATED", SQLTypes.DATETIME, 0, true),
        LASTMODIFIED("LASTMODIFIED", SQLTypes.DATETIME, 0, true),
        OWNER("OWNER", SQLTypes.VARCHAR,   100, false),
        DESCRIPTION("DESCRIPTION", SQLTypes.VARCHAR, 255, false),        
        CLAZZ("CLAZZ", SQLTypes.VARCHAR, 255, true),
        CURRENTVALUE("CURRENTVALUE", SQLTypes.VARCHAR, 255, true),
        STRATEGY("STRATCLASS", SQLTypes.VARCHAR,1000, false),
        INITPARAMS("STRATPARAM", SQLTypes.VARCHAR, 1000, false),
        FIXEDVALUES("FIXEDVALUES", SQLTypes.VARCHAR, 1000, false),
        FEATURE_UID("FEAT_UID", SQLTypes.VARCHAR, 100, true);
        
        /** Column attribute */
        private final String name;
        /** Column attribute */
        private final SQLTypes type;
        /** Column attribute */
        private final int size;
        /** Column attribute */
        private final boolean required;
        
        /**
         * Private constructor.
         *
         * @param pname
         *      column name
         * @param ptype
         *      column type (depends on underlying JDBC DB NUMBER, INTEGER, but still useful 
         * @param psize
         *      column size
         * @param pnullabz
         */
        private CustomPropertyColumns(String pname, SQLTypes ptype, int psize, boolean pnullable) {
            name = pname;
            type = ptype;
            size = psize;
            required = pnullable;
        }

        /** {@inheritDoc} */
        public String colname() { return name; }
        
        /** {@inheritDoc} */
        public SQLTypes type()  { return type; }
        
        /** {@inheritDoc} */
        public int size()       { return size; }
        
        /** {@inheritDoc} */
        public boolean nullable()  { return !required; }
        
        /** {@inheritDoc} */
        public String tableName() { return "CUSTOM_PROPERTIES"; }
        
        /** {@inheritDoc} */
        public List < SqlTableColumns > primaryKey() { 
            return Util.listOf(UID, FEATURE_UID); 
        }
        
        /** {@inheritDoc} */
        public Optional <Map < SqlTableColumns, SqlTableColumns >> foreignKey() { 
            return Optional.of(Util.mapOf(FEATURE_UID, FeaturesColumns.UID)); 
        }
    }
    
    // ---------------------------------
    // ------- TABLE AUDIT -------------
    // ---------------------------------
    
    /**
     * Representation of the JDBC Table FEATURES.
     */
    public static enum AuditColumns implements SqlTableColumns {
        
        UID("UID", SQLTypes.VARCHAR, 100, true),
        CREATED("CREATED", SQLTypes.DATETIME, 0, true),
        LASTMODIFIED("LASTMODIFIED", SQLTypes.DATETIME, 0, true),
        OWNER("OWNER", SQLTypes.VARCHAR, 100, false),
        DESCRIPTION("DESCRIPTION", SQLTypes.VARCHAR, 255, false),
        TIMESTAMP("EVT_TIME", SQLTypes.TIMESTAMP, 0, true),
        TYPE("EVT_TYPE", SQLTypes.VARCHAR, 30, true),
        NAME("NAME", SQLTypes.VARCHAR, 30, true),
        ACTION("ACTION", SQLTypes.VARCHAR, 30, true),
        HOSTNAME("HOSTNAME", SQLTypes.VARCHAR, 100, false),
        SOURCE("SOURCE", SQLTypes.VARCHAR, 100, false),
        DURATION("DURATION", SQLTypes.INTEGER, 0, true),
        VALUE("EVT_VALUE", SQLTypes.VARCHAR, 100, true),
        KEYS("EVT_KEYS", SQLTypes.VARCHAR, 1000, true);
        
        /** Column attribute */
        private final String name;
        
        /** Column attribute */
        private final SQLTypes type;
        
        /** Column attribute */
        private final int size;
        
        /** Column attribute */
        private final boolean required;
        
        /**
         * Private constructor.
         *
         * @param pname
         *      column name
         * @param ptype
         *      column type (depends on underlying JDBC DB NUMBER, INTEGER, but still useful 
         * @param psize
         *      column size
         * @param pnullabz
         */
        private AuditColumns(String pname, SQLTypes ptype, int psize, boolean pnullable) {
            name = pname;
            type = ptype;
            size = psize;
            required = pnullable;
        }
        
        /** {@inheritDoc} */
        public String colname() { return name; }
        
        /** {@inheritDoc} */
        public SQLTypes type()  { return type; }
        
        /** {@inheritDoc} */
        public int size()       { return size; }
        
        /** {@inheritDoc} */
        public boolean nullable()  { return !required; }
        
        /** {@inheritDoc} */
        public String tableName() { return "AUDIT"; }
        
        /** {@inheritDoc} */
        public List < SqlTableColumns > primaryKey() { 
            return Util.listOf(UID, TIMESTAMP); 
        }
        
        /** {@inheritDoc} */
        public Optional <Map < SqlTableColumns, SqlTableColumns >> foreignKey() { 
            return Optional.empty(); 
        }
    }

    // ---------------------------------
    // ------- TABLE METRICS -----------
    // ---------------------------------
    
    /**
     * Representation of the JDBC Table FEATURES.
     */
    public static enum MetricsColumns implements SqlTableColumns {
        
        UID("UID", SQLTypes.VARCHAR, 100, true),
        CREATED("CREATED", SQLTypes.DATETIME, 0, true),
        LASTMODIFIED("LASTMODIFIED", SQLTypes.DATETIME, 0, true),
        OWNER("OWNER", SQLTypes.VARCHAR, 100, false),
        DESCRIPTION("DESCRIPTION", SQLTypes.VARCHAR, 255, false),
        TIMESTAMP("EVT_TIME", SQLTypes.TIMESTAMP, 0, true),
        TYPE("EVT_TYPE", SQLTypes.VARCHAR, 30, true),
        NAME("NAME", SQLTypes.VARCHAR, 30, true),
        ACTION("ACTION", SQLTypes.VARCHAR, 30, true),
        HOSTNAME("HOSTNAME", SQLTypes.VARCHAR, 100, false),
        SOURCE("SOURCE", SQLTypes.VARCHAR, 100, false),
        DURATION("DURATION", SQLTypes.INTEGER, 0, true),
        VALUE("EVT_VALUE", SQLTypes.VARCHAR, 100, true),
        KEYS("EVT_KEYS", SQLTypes.VARCHAR, 1000, true);
        
        /** Column attribute */
        private final String name;
        
        /** Column attribute */
        private final SQLTypes type;
        
        /** Column attribute */
        private final int size;
        
        /** Column attribute */
        private final boolean required;
        
        /**
         * Private constructor.
         *
         * @param pname
         *      column name
         * @param ptype
         *      column type (depends on underlying JDBC DB NUMBER, INTEGER, but still useful 
         * @param psize
         *      column size
         * @param pnullabz
         */
        private MetricsColumns(String pname, SQLTypes ptype, int psize, boolean pnullable) {
            name = pname;
            type = ptype;
            size = psize;
            required = pnullable;
        }
        
        /** {@inheritDoc} */
        public String colname() { return name; }
        
        /** {@inheritDoc} */
        public SQLTypes type()  { return type; }
        
        /** {@inheritDoc} */
        public int size()       { return size; }
        
        /** {@inheritDoc} */
        public boolean nullable()  { return !required; }
        
        /** {@inheritDoc} */
        public String tableName() { return "METRICS"; }
        
        /** {@inheritDoc} */
        public List < SqlTableColumns > primaryKey() { 
            return Util.listOf(UID, TIMESTAMP); 
        }
        
        /** {@inheritDoc} */
        public Optional <Map < SqlTableColumns, SqlTableColumns >> foreignKey() { 
            return Optional.empty(); 
        }
    }
    
    /**
     * Representation of the JDBC Table FEATURES.
     */
    public static enum StrategysColumns implements SqlTableColumns {
        
    }
    
    /**
     * Representation of the JDBC Table FEATURES.
     */
    public static enum PermissionsColumns implements SqlTableColumns {
        
    }
    
    
    /**
     * Hide constructor.
     */
    private JdbcConstants() {}
    
}
