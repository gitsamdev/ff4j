package org.ff4j.test.store;

import org.ff4j.feature.FeatureStore;
import org.ff4j.jdbc.JdbcQueryBuilder;
import org.ff4j.jdbc.store.FeatureStoreJdbc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class JdbcFeatureStorePrefixTest extends CoreFeatureStoreTestSupport {

    /** DataBase. */
    private EmbeddedDatabase db;

    /** Builder. */
    private EmbeddedDatabaseBuilder builder = null;

    /** {@inheritDoc} */
    @Override
    protected FeatureStore initStore() {
        builder = new EmbeddedDatabaseBuilder();
        db = builder.
        		setType(EmbeddedDatabaseType.HSQL).//
        		addScript("classpath:ddl-prefix-schema.sql").//
        		addScript("classpath:ddl-prefix-data.sql").build();

        FeatureStoreJdbc jdbcStore = new FeatureStoreJdbc();
        jdbcStore.setQueryBuilder(new JdbcQueryBuilder("T_FF4J_", "_01"));
        jdbcStore.setDataSource(db);
        return jdbcStore;
    }

    /** {@inheritDoc} */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = builder.setType(EmbeddedDatabaseType.HSQL).
        		addScript("classpath:ddl-prefix-schema.sql").//
        		addScript("classpath:ddl-prefix-data.sql").build();
    }

    /** {@inheritDoc} */
    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testRemoveFromGroupInvalidGroup() {
        testedStore.removeFromGroup(F4, G0);
    }
}
