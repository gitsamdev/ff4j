package org.ff4j.test.property;

import org.ff4j.jdbc.PropertyStoreJdbc;
import org.ff4j.store.PropertyStore;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Test for {@link PropertyStoreJdbc}.
 *
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 */
public class JdbcPropertyStoreTest  extends AbstractPropertyStoreJunitTest {

    /** DataBase. */
    private EmbeddedDatabase db;

    /** Builder. */
    private EmbeddedDatabaseBuilder builder = null;

    /** {@inheritDoc} */
    @Override
    protected PropertyStore initPropertyStore() {
        builder = new EmbeddedDatabaseBuilder();
        db = builder.setType(EmbeddedDatabaseType.HSQL).//
                addScript("classpath:schema-ddl.sql").//
                addScript("classpath:ff-store.sql").//
                build();
        return new PropertyStoreJdbc(db);
    }
    
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
        db.shutdown();
    }
    
    @Test
    public void initJdbcPropertyStore() {
        EmbeddedDatabaseBuilder b2 = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db2 = b2.setType(EmbeddedDatabaseType.HSQL).//
                build();
        PropertyStoreJdbc jdbcStore2 = new PropertyStoreJdbc(db2, "ff4j.xml");
        Assert.assertNotNull(jdbcStore2);
    }
    
    @Test
    public void testClear() {
        EmbeddedDatabaseBuilder b2 = new EmbeddedDatabaseBuilder();
        EmbeddedDatabase db2 = b2.setType(EmbeddedDatabaseType.HSQL).//
                build();
        PropertyStoreJdbc jdbcStore2 = new PropertyStoreJdbc(db2, "ff4j.xml");
        Assert.assertNotNull(jdbcStore2);
    }
    
   
    
    
}
