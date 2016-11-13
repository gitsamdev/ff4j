package org.ff4j.property;

/*
 * #%L ff4j-core %% Copyright (C) 2013 - 2016 FF4J %% Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. #L%
 */

import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import org.ff4j.FF4jBaseObject;
import org.ff4j.FF4jExecutionContext;
import org.ff4j.strategy.PropertyEvaluationStrategy;
import org.ff4j.utils.FF4jUtils;
import org.ff4j.utils.JsonUtils;

/**
 * Abstraction of Property.
 *
 * @author Cedrick Lunven (@clunven)
 */
public abstract class Property<T> extends FF4jBaseObject<Property<T>> implements Supplier<T> {

    /** serialVersionUID. */
    private static final long serialVersionUID = -2484426537747694712L;

    /** Canonical name for JSON serialization. */
    protected String type = getClass().getCanonicalName();

    /** Current Value. */
    protected T value;

    /** Some store do not allow property edition. */
    protected boolean readOnly = false;

    /** If value have a limited set of values. */
    protected Set<T> fixedValues = null;

    /** Can compute the property value based on your own implementation. */
    protected PropertyEvaluationStrategy<T> evaluationStrategy = null;

    /**
     * Constructor by property name.
     *
     * @param name
     *            unique property name
     */
    protected Property(String uid) {
        super(uid);
    }

    /**
     * Constructor with name and value as String.
     *
     * @param name
     *            current name
     * @param value
     *            current value
     */
    protected Property(String name, String value) {
        this(name);
        this.value = fromString(value);
    }

    /**
     * Constructor with name and value as String.
     *
     * @param name
     *            current name
     * @param value
     *            current value
     */
    protected Property(String name, T value) {
        this(name);
        this.value = value;
    }

    /**
     * Check dynamically the class of the parameter T.
     *
     * @return class of template T parameter
     * @throws Exception
     *             error on reading type
     */
    @SuppressWarnings({"unchecked"})
    public Class<T> parameterizedType() {
        ParameterizedType pt = (ParameterizedType) getClass().getGenericSuperclass();
        return (Class<T>) pt.getActualTypeArguments()[0];
    }    

    /**
     * Unmarshalling of value for serailized string expression.
     *
     * @param v
     *            value represented as a serialized String
     * @return target value
     */
    public abstract T fromString(String v);
    
    /** {@inheritDoc} */
    @Override
    public T get() {
        return getValue();
    }
    
    /** {@inheritDoc} */
    public T get(FF4jExecutionContext ctx) {
        return getValue(ctx);
    }
    
    /** {@inheritDoc} */
    public T getValue() {
        if (evaluationStrategy == null) return value;
        return evaluationStrategy.evaluate(this, null);
    }

    /**
     * If an execution is provided evaluate the property value.
     *
     * @param pec
     *            evaluation strategy
     * @return property value
     */
    public T getValue(FF4jExecutionContext ctx) {
        if (evaluationStrategy == null) return value;
        return evaluationStrategy.evaluate(this, ctx);
    }
    
    /**
     * Serialized value as String
     *
     * @return current value as a string or null
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
     * @return int value
     */
    public int asInt() {
        return Integer.parseInt(asString());
    }

    /**
     * Return value as double if possible.
     *
     * @return int value
     */
    public double asDouble() {
        return Double.parseDouble(asString());
    }

    /**
     * Return value as boolean if possible.
     *
     * @return boolea value
     */
    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }
    
    /**
     * Setter accessor for attribute 'value'.
     * 
     * @param value
     *            new value for 'value '
     */
    public Property<T> setValue(T value) {
        if (fixedValues != null && !fixedValues.isEmpty() && !fixedValues.contains(value)) {
            throw new IllegalArgumentException("Invalid value corrects are " + fixedValues);
        }
        this.value = value;
        return this;
    }

    /**
     * Load value from its string expression.
     *
     * @param value
     *            current string value
     */
    public Property<T> setValueFromString(String value) {
        this.value = fromString(value);
        return this;
    }

    /**
     * Getter accessor for attribute 'fixedValues'.
     *
     * @return current value of 'fixedValues'
     */
    public Optional<Set<T>> getFixedValues() {
        return Optional.ofNullable(fixedValues);
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
        if (fixedValues != null) {
            jsonExpression.append(",\"fixedValues\":" + JsonUtils.collectionAsJson(fixedValues));
        }
        jsonExpression.append("}");
        return jsonExpression.toString();
    }

    @SuppressWarnings("unchecked")
    public Property<T> setFixedValues(T... perms) {
        return setFixedValues(FF4jUtils.setOf(perms));
    }

    public Property<T> setFixedValues(Set<T> perms) {
        fixedValues = perms;
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
            if (fixedValues == null) {
                fixedValues = new HashSet<>();
            }
            fixedValues.addAll(FF4jUtils.setOf(fixed));
        }
        return this;
    }

    /**
     * Getter accessor for attribute 'type'.
     *
     * @return current value of 'type'
     */
    public String getType() {
        return type;
    }

    /**
     * Setter accessor for attribute 'type'.
     * 
     * @param type
     *            new value for 'type '
     */
    public Property<T> setType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Getter accessor for attribute 'readOnly'.
     *
     * @return current value of 'readOnly'
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Setter accessor for attribute 'readOnly'.
     * 
     * @param readOnly
     *            new value for 'readOnly '
     */
    public Property<T> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

}
