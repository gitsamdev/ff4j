package org.ff4j.test.property;

import org.ff4j.jdbc.JdbcQueryBuilder;
import org.ff4j.jdbc.PropertyStoreJdbc;
import org.ff4j.store.PropertyStore;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Test for {@link PropertyStoreJdbc}.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class JdbcPropertyStorePrefixTest  extends AbstractPropertyStoreJunitTest {

    /** DataBase. */
    private EmbeddedDatabase db;

    /** Builder. */
    private EmbeddedDatabaseBuilder builder = null;

    /** {@inheritDoc} */
    @Override
    protected PropertyStore initPropertyStore() {
        builder = new EmbeddedDatabaseBuilder();
        db = builder.
        		setType(EmbeddedDatabaseType.HSQL).//
        		addScript("classpath:ddl-prefix-schema.sql").//
        		addScript("classpath:ddl-prefix-data.sql").build();
        
        PropertyStoreJdbc jdbcStore = new PropertyStoreJdbc();
        jdbcStore.setQueryBuilder(new JdbcQueryBuilder("T_FF4J_", "_01"));
        jdbcStore.setDataSource(db);
        return jdbcStore;
    }
    
    /** {@inheritDoc} */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = builder.
        		setType(EmbeddedDatabaseType.HSQL).//
        		addScript("classpath:ddl-prefix-schema.sql").//
        		addScript("classpath:ddl-prefix-data.sql").build();
    }

    /** {@inheritDoc} */
    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }
   
    
    
}
