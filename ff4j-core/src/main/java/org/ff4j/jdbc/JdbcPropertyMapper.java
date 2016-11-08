package org.ff4j.jdbc;

import static org.ff4j.jdbc.JdbcStoreConstants.COL_PROPERTY_DESCRIPTION;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_PROPERTY_FIXED;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_PROPERTY_ID;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_PROPERTY_TYPE;
import static org.ff4j.jdbc.JdbcStoreConstants.COL_PROPERTY_VALUE;

import java.sql.PreparedStatement;

/*
 * #%L
 * ff4j-core
 * %%
 * Copyright (C) 2013 - 2015 FF4J
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import org.ff4j.exception.PropertyAccessException;
import org.ff4j.mapper.PropertyMapper;
import org.ff4j.property.Property;
import org.ff4j.property.PropertyFactory;
import org.ff4j.property.PropertyString;

/**
 * Convert resultset into {@link PropertyString}.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class JdbcPropertyMapper implements PropertyMapper < PreparedStatement, ResultSet > {
    
    /** {@inheritDoc} */
    @Override
    public PreparedStatement toStore(Property<?> bean) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Property<?> fromStore(ResultSet rs) {
        try {
            Property<?> p = PropertyFactory.createProperty(
                    rs.getString(COL_PROPERTY_ID),  
                    rs.getString(COL_PROPERTY_TYPE), 
                    rs.getString(COL_PROPERTY_VALUE));
            p.setDescription(rs.getString(COL_PROPERTY_DESCRIPTION));
            // TODO
            //p.setCreationDate(currentDate);
            //p.setLastModified(currentDate);
            //p.setOwner(owner)
            //p.setReadOnly(readOnly)
            
            String fixedValues  = rs.getString(COL_PROPERTY_FIXED);
            if (fixedValues != null) {
                Arrays.stream(fixedValues.split(",")).forEach(v-> p.add2FixedValueFromString(v.trim()) );
            }
            return p;
        } catch (SQLException sqlEx) {
            throw new PropertyAccessException("Cannot map Resultset into property", sqlEx);
        }
        
    }
    
}
