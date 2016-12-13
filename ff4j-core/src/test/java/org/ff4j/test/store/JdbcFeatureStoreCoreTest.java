package org.ff4j.test.store;

import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.exception.GroupNotFoundException;
import org.ff4j.jdbc.FeatureStoreJdbc;
import org.ff4j.store.FeatureStore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class JdbcFeatureStoreCoreTest extends CoreFeatureStoreTestSupport {

    /** DataBase. */
    private EmbeddedDatabase db;

    /** Builder. */
    private EmbeddedDatabaseBuilder builder = null;

    /** {@inheritDoc} */
    @Override
    protected FeatureStore initStore() {
        builder = new EmbeddedDatabaseBuilder();
        db = builder.setType(EmbeddedDatabaseType.HSQL).//
        		addScript("classpath:schema-ddl.sql").//
        		addScript("classpath:ff-store.sql").build();

        FeatureStoreJdbc jdbcStore = new FeatureStoreJdbc();
        jdbcStore.setDataSource(db);
        return jdbcStore;
    }

    /** {@inheritDoc} */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = builder.setType(EmbeddedDatabaseType.HSQL).
        		addScript("classpath:schema-ddl.sql").
        		addScript("classpath:ff-store.sql").build();
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
    
    @Test(expected = FeatureNotFoundException.class)
    public void readDoesNotExist() {
        testedStore.read("dont-exist");
    }
    
    @Test(expected = GroupNotFoundException.class)
    public void readGroupDoesNotExist() {
        testedStore.readGroup("dont-exist");
    }
}
