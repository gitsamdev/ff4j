package org.ff4j.property;

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


import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.ff4j.FF4jBaseObject;
import org.ff4j.utils.FF4jUtils;

/**
 * Abstraction of Property.
 *
 * @author Cedrick Lunven (@clunven)
 */
public abstract class Property < T > extends FF4jBaseObject < Property< T > > implements Supplier < T > {
    
    /** serialVersionUID. */
    private static final long serialVersionUID = -2484426537747694712L;

    /** Canonical name for JSON serialization. */
    protected String type = getClass().getCanonicalName();
    
    /** Current Value. */
    protected T value;
    
    /** If value have a limited set of values. */
    protected Optional < Set < T > > fixedValues = Optional.empty();
   
    /** Some store do not allow property edition. */
    protected boolean readOnly = false;
   
    /**
     * Constructor by property name.
     *
     * @param name
     *         unique property name
     */
    protected Property(String uid) {
        super(uid);
    }
    
    /**
     * Constructor with name and value as String.
     *
     * @param name
     *      current name
     * @param value
     *      current value
     */
    protected Property(String name, String value) {
        this(name);
        this.value = fromString(value);
    }
    
    /**
     * Constructor with name and value as String.
     *
     * @param name
     *      current name
     * @param value
     *      current value
     */
    protected Property(String name, T value) {
        this(name);
        this.value = value;
    }
    
    /**
     * Unmarshalling of value for serailized string expression.
     *
     * @param v
     *      value represented as a serialized String
     * @return
     *      target value
     */
    public abstract T fromString(String v);

    /** {@inheritDoc} */
    @Override
    public T get() {
        return getValue();
    }
    
    /**
     * Check dynamically the class of the parameter T.
     *
     * @return
     *      class of template T parameter
     * @throws Exception
     *      error on reading type
     */
    @SuppressWarnings({ "unchecked" })
    public Class<T> parameterizedType() {
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        return  (Class<T>) pt.getActualTypeArguments()[0];
    }
    
    /** 
     * Serialized value as String
     *
     * @return
     *      current value as a string or null
     */
    public String asString() {
        if (get() == null) {
            return null;
        }
        return get().toString();
    }
    
    /**
     * Return value as int (if possible).
     *
     * @return
     *      int value
     */
    public int asInt() {
        return Integer.parseInt(asString());
    }

    /**
     * Return value as double if possible.
     *
     * @return
     *      int value
     */
    public double asDouble() {
        return Double.parseDouble(asString());
    }
    
    /**
     * Return value as boolean if possible.
     *
     * @return
     *      boolea value
     */
    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

    /**
     * Getter accessor for attribute 'value'.
     *
     * @return
     *       current value of 'value'
     */
    public T getValue() {
        return value;
    }

    /**
     * Setter accessor for attribute 'value'.
     * @param value
     * 		new value for 'value '
     */
    public Property<T> setValue(T value) {
        if (fixedValues.isPresent() && !fixedValues.get().contains(value)) {
            throw new IllegalArgumentException("Invalid value corrects are " + fixedValues);
        }
        this.value = value;
        return this;
    }
    
    /**
     * Load value from its string expression.
     *
     * @param value
     *      current string value
     */
    public Property<T> setValueFromString(String value) {
        this.value = fromString(value);
        return this;
    }

    /**
     * Getter accessor for attribute 'fixedValues'.
     *
     * @return
     *       current value of 'fixedValues'
     */
    public Optional < Set<T> > getFixedValues() {
        return fixedValues;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toJson();
    }
    
    /** {@inheritDoc} */
    public String toJson() {
        StringBuilder jsonExpression = new StringBuilder("{ ");
        jsonExpression.append(super.baseJson());
        jsonExpression.append(",\"type\":\"" + type + "\"");
        jsonExpression.append(",\"readOnly\":\"" + readOnly + "\"");
        jsonExpression.append(",\"value\":");
        jsonExpression.append((null == value) ? "null" : "\"" + asString() + "\"");
        fixedValues.ifPresent(fv -> {
            jsonExpression.append(",\"fixedValues\":[" + fv.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]");
        });
        jsonExpression.append("}");
        return jsonExpression.toString();
    }
      
    @SuppressWarnings("unchecked")
    public Property<T> setFixedValues(T... perms) {
        return setFixedValues(FF4jUtils.setOf(perms));
    }
    
    public Property<T> setFixedValues(Set<T> perms) {
        fixedValues = Optional.ofNullable(perms);
        return this;
    }
    
    public Property<T> add2FixedValueFromString(String v) {
        return addFixedValue(fromString(v));
    }

    @SuppressWarnings("unchecked")
    public Property<T> addFixedValue(T permission) {
        return addFixedValues(permission);
    }
    
    @SuppressWarnings("unchecked")
    public Property<T> addFixedValues(T... fixed) {
        if (fixed != null) {
            Set<T> setFixedValues = FF4jUtils.setOf(fixed);
            if (fixedValues.isPresent()) {
                fixedValues.get().addAll(setFixedValues);
            } else {
                fixedValues = Optional.of(setFixedValues);
            }
        }
        return this;
    }

    /**
     * Getter accessor for attribute 'type'.
     *
     * @return
     *       current value of 'type'
     */
    public String getType() {
        return type;
    }

    /**
     * Setter accessor for attribute 'type'.
     * @param type
     * 		new value for 'type '
     */
    public Property<T> setType(String type) {
        this.type = type;
        return this;
    }
    
    /**
     * Getter accessor for attribute 'readOnly'.
     *
     * @return
     *       current value of 'readOnly'
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Setter accessor for attribute 'readOnly'.
     * @param readOnly
     * 		new value for 'readOnly '
     */
    public Property<T> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

}
