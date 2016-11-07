package org.ff4j.test.store;

import org.ff4j.jdbc.FeatureStoreJdbc;
import org.ff4j.store.FeatureStore;
import org.junit.After;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

public class JdbcFeatureStoreXmlTest extends CoreFeatureStoreTestSupport {

    /** DataBase. */
    private EmbeddedDatabase db;
    
    /** {@inheritDoc} */
    @Override
    protected FeatureStore initStore() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        db = builder.setType(EmbeddedDatabaseType.HSQL).addScript("classpath:schema-ddl.sql").build();
        return new FeatureStoreJdbc(db, "ff4j.xml");
    }
    /** {@inheritDoc} */
    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }
}
