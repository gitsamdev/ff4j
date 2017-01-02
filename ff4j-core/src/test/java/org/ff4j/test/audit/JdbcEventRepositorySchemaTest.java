package org.ff4j.test.audit;

import static org.ff4j.jdbc.JdbcUtils.isTableExist;

import javax.sql.DataSource;

import org.ff4j.jdbc.JdbcQueryBuilder;
import org.ff4j.jdbc.store.AuditTrailJdbc;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

/**
 * Check DB and create Schema.
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class JdbcEventRepositorySchemaTest {

    /** DataBase. */
    private EmbeddedDatabase db;

    /** Builder. */
    private EmbeddedDatabaseBuilder builder = null;
    
    /** Tested Store. */
    protected AuditTrailJdbc testedStore;

    /** {@inheritDoc} */
    @Before
    public void setUp() throws Exception {
        initStore();
    }
    
    /** {@inheritDoc} */
    public void initStore() {
        builder = new EmbeddedDatabaseBuilder();
        db = builder.setType(EmbeddedDatabaseType.HSQL).build();
        testedStore = new AuditTrailJdbc(db);
    }
   
    /** {@inheritDoc} */
    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }
    
    @Test
    public void testCreateSchema() {
        DataSource       ds = testedStore.getDataSource();
        JdbcQueryBuilder qb = testedStore.getQueryBuilder();
        // Given
        Assert.assertFalse(isTableExist(ds, qb.getTableNameAudit()));
        // When
        testedStore.createSchema();
        // then
        Assert.assertTrue(isTableExist(ds, qb.getTableNameAudit()));
        // When (no error)
        testedStore.createSchema();
    }
    
}
