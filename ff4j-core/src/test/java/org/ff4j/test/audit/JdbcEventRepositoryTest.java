package org.ff4j.test.audit;

import org.ff4j.audit.usage.FeatureUsageEventStore;
import org.ff4j.jdbc.store.AuditTrailJdbc;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Unit testing of JDBC implementation of {@link FeatureUsageEventStore}.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class JdbcEventRepositoryTest extends AbstractEventRepositoryTest {
    
    /** DataBase. */
    private EmbeddedDatabase db;

    /** Builder. */
    private EmbeddedDatabaseBuilder builder = null;
    
    /** {@inheritDoc} */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        db = builder.setType(EmbeddedDatabaseType.HSQL).//
                addScript("classpath:schema-ddl.sql").//
                addScript("classpath:ff-store.sql"). //
                build();
    }

    /** {@inheritDoc} */
    @After
    public void tearDown() throws Exception {
        Thread.sleep(200);
        db.shutdown();
    }
    
    /** {@inheritDoc} */
    @Override
    protected FeatureUsageEventStore initRepository() {
        builder = new EmbeddedDatabaseBuilder();
        db = builder.setType(EmbeddedDatabaseType.HSQL).//
                 addScript("classpath:schema-ddl.sql").//
                 addScript("classpath:ff-store.sql").//
                 build();
        return new AuditTrailJdbc(db);
    }

}
