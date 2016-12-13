package org.ff4j.test.store;

import org.ff4j.jdbc.JdbcQueryBuilder;
import org.junit.Test;

public class JdbcQueryBuilderTest {

    @Test
    public void testCreateSchemaDDL() {
        //System.out.println(new JdbcQueryBuilder().sqlCreateSchema());
        
        System.out.println(new JdbcQueryBuilder().sqlInsertCustomProperties());
        
        
    }
}
