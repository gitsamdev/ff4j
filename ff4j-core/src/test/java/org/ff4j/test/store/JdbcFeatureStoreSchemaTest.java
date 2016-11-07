package org.ff4j.test.store;

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


import static org.ff4j.utils.JdbcUtils.isTableExist;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.ff4j.feature.Feature;
import org.ff4j.jdbc.FeatureStoreJdbc;
import org.ff4j.jdbc.JdbcQueryBuilder;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyString;
import org.ff4j.strategy.PonderationStrategy;
import org.ff4j.utils.FF4jUtils;
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
public class JdbcFeatureStoreSchemaTest {

    /** DataBase. */
    private EmbeddedDatabase db;

    /** Builder. */
    private EmbeddedDatabaseBuilder builder = null;
    
    /** Tested Store. */
    protected FeatureStoreJdbc testedStore;

    /** {@inheritDoc} */
    @Before
    public void setUp() throws Exception {
        initStore();
    }
    
    /** {@inheritDoc} */
    public void initStore() {
        builder = new EmbeddedDatabaseBuilder();
        db = builder.setType(EmbeddedDatabaseType.HSQL).build();
        testedStore = new FeatureStoreJdbc();
        testedStore.setDataSource(db);
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
        Assert.assertFalse(isTableExist(ds, qb.getTableNameFeatures()));
        Assert.assertFalse(isTableExist(ds, qb.getTableNameRoles()));
        Assert.assertFalse(isTableExist(ds, qb.getTableNameCustomProperties()));
        // When
        testedStore.createSchema();
        // then
        Assert.assertTrue(isTableExist(ds, qb.getTableNameFeatures()));
        Assert.assertTrue(isTableExist(ds, qb.getTableNameRoles()));
        Assert.assertTrue(isTableExist(ds, qb.getTableNameCustomProperties()));
        // When (no error)
        testedStore.createSchema();
    }
    
    @Test
    public void testworkWithSchema() {
        // Given
        testedStore.createSchema();
        Assert.assertFalse(testedStore.exist("fx"));
        // When
        Feature fullFeature = new Feature("fx").toggleOn();
        fullFeature.setPermissions(FF4jUtils.setOf("toto", "tata"));
        fullFeature.setFlippingStrategy(new PonderationStrategy(0.5d));
        Map < String , Property<?>> customProperties = new HashMap< String , Property<?>>();
        fullFeature.setCustomProperties(customProperties);
        testedStore.create(fullFeature);
        // Then
        Assert.assertTrue(testedStore.exist("fx"));
    }
    
    @Test
    public void testCreateCustomProperties() {
        testedStore.createSchema();
        
        // When
        Feature fullFeature = new Feature("fx").toggleOn();
        fullFeature.setPermissions(FF4jUtils.setOf("toto", "tata"));
        fullFeature.setFlippingStrategy(new PonderationStrategy(0.5d));
        Map < String , Property<?>> customProperties = new HashMap< String , Property<?>>();
        fullFeature.setCustomProperties(customProperties);
        testedStore.create(fullFeature);
        
        testedStore.createCustomProperties("fx", null);
        
        Property<?> p1 = new PropertyString("p1");
        
        Property<String> p2 = new PropertyString("p2");
        p2.setFixedValues(FF4jUtils.setOf("v1","v3"));
        
        testedStore.createCustomProperties("fx", Arrays.asList(p2,p1));
        testedStore.createCustomProperties("fx", null);
        
    }
}
