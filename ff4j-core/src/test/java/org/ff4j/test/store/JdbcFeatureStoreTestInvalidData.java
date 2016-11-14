package org.ff4j.test.store;

import java.util.HashMap;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2016 FF4J
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import org.ff4j.exception.FeatureAccessException;
import org.ff4j.exception.InvalidStrategyTypeException;
import org.ff4j.feature.FlippingStrategy;
import org.ff4j.jdbc.FeatureStoreJdbc;
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
        FlippingStrategy.instanciate("ID", "com.KO", new HashMap<String, String>());
    }
    
    /** {@inheritDoc} */
    @After
    public void tearDown() throws Exception {
        db.shutdown();
    }

}
