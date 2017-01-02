package org.ff4j.test.store;

import java.util.HashMap;

import org.ff4j.exception.InvalidStrategyTypeException;
import org.ff4j.feature.ToggleStrategy;
import org.ff4j.jdbc.store.FeatureStoreJdbc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * This test is meant to access a Jfeature store in 'pure' JDBC.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class JdbcFeatureStoreTestInvalidData {

    /** DataBase. */
    private EmbeddedDatabase db;

    /** Target Store. */
    private FeatureStoreJdbc jdbcStore;
   
    /** {@inheritDoc} */
    @Before
    public void setUp() throws Exception {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        db = builder.
                setType(EmbeddedDatabaseType.HSQL).//
                addScript("classpath:schema-ddl.sql").//
                addScript("classpath:ff-invalidstore.sql").build();

        jdbcStore = new FeatureStoreJdbc();
        jdbcStore.setDataSource(db);
    }

    @Test(expected = InvalidStrategyTypeException.class)
    public void testReadInvalid() {
        jdbcStore.findById("forth");
    }
    
    @Test(expected = InvalidStrategyTypeException.class)
    public void testInvalidStrategy() {
        ToggleStrategy.of("ID", "com.KO", new HashMap<String, String>());
    }
    
    /** {@inheritDoc} */
    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

}
