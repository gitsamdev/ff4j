package org.ff4j.aop.proxy.annotation;

/*
 * #%L ff4j-aop %% Copyright (C) 2013 Ff4J %% Licensed under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License. #L%
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.lang.model.type.NullType;

/**
 * FeatureFlipping Core Annotation.
 * 
 * <br/>
 * By annotating the target method the advisor could intercept method call and subsitute with alter class or mock class.
 * 
 * @author <a href="mailto:cedrick.lunven@gmail.com">Cedrick LUNVEN</a>
 * @author <a href="https://github.com/ghostd">Vincent Ricard</a>
 */
@Inherited
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FeatureToggle {
    
    /**
     * Feature UID to be used.
     * 
     * @return unique target id.
     */
    String feature();

    /**
     * Class to be executed if the feature is ON
     * 
     * @return target Flipping Strategy
     */
    Class<?> on() default NullType.class;
       
    /**
     * Class to be executed if the feature is OFF and underlying type is interface
     * 
     * @return target Flipping Strategy
     */
    Class<?> off() default NullType.class;
    
}