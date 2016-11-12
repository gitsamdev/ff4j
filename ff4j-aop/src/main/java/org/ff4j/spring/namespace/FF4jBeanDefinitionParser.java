package org.ff4j.spring.namespace;

import static org.ff4j.spring.namespace.FF4jNameSpaceConstants.ATT_FF4J_AUTH_MANAGER;
import static org.ff4j.spring.namespace.FF4jNameSpaceConstants.ATT_FF4J_AUTOCREATE;
import static org.ff4j.spring.namespace.FF4jNameSpaceConstants.ATT_FF4J_FILENAME;

/*
 * #%L
 * ff4j-aop
 * %%
 * Copyright (C) 2013 - 2015 Ff4J
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


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ff4j.FF4j;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSimpleBeanDefinitionParser;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;

/**
 * Parser for tag <ff4j:ff4j>
 *
 * @author Lunven Cedrick
 */
public final class FF4jBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {

    /** logger for class. **/
    private static Log logger = LogFactory.getLog(FF4jBeanDefinitionParser.class);
    
    /** {@inheritDoc} **/
    protected Class < FF4j > getBeanClass(final Element pelement) {
        return FF4j.class;
    }

    /** {@inheritDoc} **/
    protected void postProcess(final BeanDefinitionBuilder definitionBuilder, final Element ff4jTag) {
        super.postProcess(definitionBuilder, ff4jTag);
        logger.debug("Initialization from <ff4j:ff4j> TAG");
        // If filename is present ff4j will be initialized with both features and properties inmemory.
        if (StringUtils.hasLength(ff4jTag.getAttribute(ATT_FF4J_FILENAME))) {
            String fileName = ff4jTag.getAttribute(ATT_FF4J_FILENAME);
            FeatureStoreInMemory  imfs = new FeatureStoreInMemory(fileName);
            PropertyStoreInMemory imps = new PropertyStoreInMemory(fileName);
            definitionBuilder.getBeanDefinition().getPropertyValues().addPropertyValue("featureStore", imfs);
            definitionBuilder.getBeanDefinition().getPropertyValues().addPropertyValue("propertiesStore", imps);
            logger.debug("... Setting in-memory stores : " + imfs.findAll().size() + " feature(s), " + imps.findAll().size() + " propertie(s)");
        }
        
        if (StringUtils.hasLength(ff4jTag.getAttribute(ATT_FF4J_AUTOCREATE))) {
            String autocreate = ff4jTag.getAttribute(ATT_FF4J_AUTOCREATE);
            logger.debug("... Setting autocreate property to '" + autocreate + "'");
        }
        
        if (StringUtils.hasLength(ff4jTag.getAttribute(ATT_FF4J_AUTH_MANAGER))) {
            String authManagerBeanId = ff4jTag.getAttribute(ATT_FF4J_AUTH_MANAGER);
            RuntimeBeanReference refSolution = new RuntimeBeanReference(authManagerBeanId);
            definitionBuilder.getBeanDefinition().getPropertyValues().addPropertyValue("authorizationsManager", refSolution);
            logger.debug("... Setting authorizationManager with " + authManagerBeanId);
        }
        
        logger.debug("... Initialization done");
        
    }
}
