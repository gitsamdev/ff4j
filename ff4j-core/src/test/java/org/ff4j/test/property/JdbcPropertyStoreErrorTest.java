package org.ff4j.test.property;

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
import static org.mockito.Mockito.doThrow;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.ff4j.exception.PropertyAccessException;
import org.ff4j.jdbc.PropertyStoreJdbc;
import org.ff4j.property.domain.PropertyString;
import org.junit.Test;
import org.mockito.Mockito;

public class JdbcPropertyStoreErrorTest {
    
    @Test(expected = PropertyAccessException.class)
    public void testgetExistKO()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.exists("xx");
    }
    
    @Test(expected = PropertyAccessException.class)
    public void testgetReadAll()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.findAll();
    }
    
    @Test(expected = PropertyAccessException.class)
    public void testCreateKO()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.create(new PropertyString("p1","v1"));
    }
    
    @Test(expected = PropertyAccessException.class)
    public void testReadKO()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.findById("p1");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testUpdateKO()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.update(null);
    }
    
    @Test(expected = PropertyAccessException.class)
    public void testUpdateKO2()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.update("p1", "v1");
    }
    
    @Test(expected = PropertyAccessException.class)
    public void tesDeleteKO()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.delete("p1");
    }
    
    @Test(expected = PropertyAccessException.class)
    public void testListProperties()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.listPropertyNames();
    }
    
    @Test(expected = PropertyAccessException.class)
    public void testClearKO()  throws SQLException {
        DataSource mockDS = Mockito.mock(DataSource.class);
        doThrow(new SQLException()).when(mockDS).getConnection();
        PropertyStoreJdbc jrepo = new PropertyStoreJdbc(mockDS);
        jrepo.setDataSource(mockDS);
        jrepo.deleteAll();
    }

}
