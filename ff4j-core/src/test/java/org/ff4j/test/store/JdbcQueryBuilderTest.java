package org.ff4j.test.store;

import org.ff4j.jdbc.JdbcQueryBuilder;
import org.junit.Test;

public class JdbcQueryBuilderTest {

    @Test
    public void testCreateSchemaDDL() {
        JdbcQueryBuilder jdbcQB = new JdbcQueryBuilder();
        
        //System.out.println(jdbcQB.sqlDropSchema());
        System.out.println(jdbcQB.sqlCreateSchema());
        
        
        //System.out.println(new JdbcQueryBuilder().sqlStrategyOfFeature());
        
        
    }
}
