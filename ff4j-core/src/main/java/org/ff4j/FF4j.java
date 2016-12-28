package org.ff4j;

import static org.ff4j.utils.JsonUtils.attributeAsJson;
import static org.ff4j.utils.JsonUtils.objectAsJson;

import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

import org.ff4j.audit.usage.AbstractFeatureUsageService;
import org.ff4j.audit.usage.FeatureUsageListener;
import org.ff4j.audit.usage.FeatureUsageService;
import org.ff4j.cache.CacheManager;
import org.ff4j.cache.FeatureStoreCacheProxy;
import org.ff4j.cache.PropertyStoreCacheProxy;
import org.ff4j.conf.XmlConfig;
import org.ff4j.conf.XmlParser;
import org.ff4j.event.Event;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.feature.FlippingStrategy;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.inmemory.FeatureUsageInMemory;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.ff4j.property.Property;
import org.ff4j.security.FF4JSecurityManager;
import org.ff4j.store.AbstractObservable;
import org.ff4j.store.FeatureStore;
import org.ff4j.store.PropertyStore;

/**
 * Main class and public api to work with framework FF4j.
 * 
 * <ul>It proposes a few underlying elements :
 *  <li>{@link FeatureStore} is used to store status of manipulated features.
 *  <li>{@link PropertyStore} is used to store properties values.
 *  <li>{@link FeatureUsageService} is used to store audit information.
 *  <li>{@link FF4JSecurityManager} to limit access to features is relevant (permissions).
 * </ul>
 *
 * @author Cedrick Lunven (@clunven)
 *
 * @since 1.0
 */
public class FF4j extends AbstractObservable < FeatureUsageListener > {
    
    /** Intialisation. */
    private final long startTime = System.currentTimeMillis();

    /** Version of ff4j. */
    private final String version = getClass().getPackage().getImplementationVersion();
    
    // -- §§ Handle Features §§ ---
    
    /** Storage to persist feature within {@link FeatureStore}. */
    private FeatureStore featureStore = new FeatureStoreInMemory();
     
    /** Flag to ask for automatically create the feature if not found in the store. */
    private boolean autoCreateFeatures = false;
    
    // -- §§ Handle Properties §§ ---
    
    /** Storage to persist properties within {@link PropertyStore}. */
    private PropertyStore propertyStore = new PropertyStoreInMemory();
    
    // -- §§ Configuration §§ ---
    
    /** Source. */
    private String source = Event.Source.JAVA_API.name();
    
    /** Security policy to limit access through ACL with {@link FF4JSecurityManager}. */
    private FF4JSecurityManager securityManager = null;
    
    /** Define feature usage. */
    private AbstractFeatureUsageService featureUsage = null;
    
    /**
     * Base constructor to allows instantiation through IoC.
     * Default stores are created and will work in memory.
     */
    public FF4j() {
    }

    /**
     * Constructor initializing ff4j with an InMemoryStore
     */
    public FF4j(String xmlFile) {
        this.featureStore  = new FeatureStoreInMemory(xmlFile);
        this.propertyStore = new PropertyStoreInMemory(xmlFile);
        this.featureUsage  = new FeatureUsageInMemory();
        registerListener("DefaultUsageTracking", this.featureUsage);
    }

    /**
     * Constructor initializing ff4j with an InMemoryStore using an InputStream. Simplify integration with Android through
     * <code>Asset</code>
     */
    public FF4j(InputStream xmlFileResourceAsStream) {
        this.featureStore  = new FeatureStoreInMemory(xmlFileResourceAsStream);
        this.propertyStore = new PropertyStoreInMemory(xmlFileResourceAsStream);
    }

    /**
     * Ask if flipped.
     * 
     * @param uid
     *            feature unique identifier.
     * @param executionContext
     *            current execution context
     * @return current feature status
     */
    public boolean check(String uid) {
        return isFeatureToggled(uid, null, null);
    }

    /**
     * Elegant way to ask for flipping.
     * 
     * @param featureID
     *            feature unique identifier.
     * @param executionContext
     *            current execution context
     * @return current feature status
     */
    public boolean check(String uid, FF4jExecutionContext executionContext) {
        return isFeatureToggled(uid, null, executionContext);
    }

    /**
     * Overriding strategy on feature.
     * 
     * @param featureID
     *            feature unique identifier.
     * @param executionContext
     *            current execution context
     * @return
     */
    public boolean checkOveridingStrategy(String uid, FlippingStrategy strats) {
        return isFeatureToggled(uid, strats, FF4jExecutionContextHolder.getContext());
    }

    /**
     * Overriding strategy on feature.
     * 
     * @param featureID
     *            feature unique identifier.
     * @param executionContext
     *            current execution context
     * @return
     */
    public boolean checkOveridingStrategy(String uid, FlippingStrategy strats, FF4jExecutionContext executionContext) {
        return isFeatureToggled(uid, strats, executionContext);
    }

    /**
     * Feature toggle.
     *
     * @param uid
     *      feature identifier
     * @param strats
     *      flipping strategy
     * @param executionContext
     *      execution context
     * @return
     */
    protected boolean isFeatureToggled(String uid, FlippingStrategy strats, FF4jExecutionContext executionContext) {
        
        // Read from store
        Feature feature = getFeature(uid);
        
        // Update current context
        FF4jExecutionContextHolder.add2Context(executionContext);
        
        // First level check (status = ON and permission OK)
        boolean featureToggled = feature.isEnable() && isAllowed(feature);
        if (featureToggled) {
            if (strats != null) {
                featureToggled = strats.evaluate(uid, getFeatureStore(), 
                        FF4jExecutionContextHolder.getContext());
            } else if (feature.getFlippingStrategy().isPresent()) {
                featureToggled = feature.getFlippingStrategy().get()
                        .evaluate(uid, getFeatureStore(), executionContext);
            }
        }
        
        // Send information that feature will be used
        if (featureToggled) {
            this.notify((listener) -> listener.onFeatureExecuted(feature));
        }
        return featureToggled;
    }
    
    /**
     * Load SecurityProvider roles (e.g : SpringSecurity GrantedAuthorities)
     * 
     * @param featureName
     *            target name of the feature
     * @return if the feature is allowed
     */
    public boolean isAllowed(Feature feature) {
        /* Get current connected user
        FF4jUser connectedUser = new FF4jUser("");
        if (connectedUser != null) { 
            return feature.isAllowedToUse(connectedUser);
        }*/
        return true;
    }
    
    /**
     * Enable Feature.
     * 
     * @param featureID
     *            unique feature identifier.
     */
    public FF4j toggleOn(String uid) {
        try {
            getFeatureStore().toggleOn(uid);
        } catch (FeatureNotFoundException fnfe) {
            if (this.autoCreateFeatures) {
                getFeatureStore().create(new Feature(uid).toggleOn());
            } else {
                throw fnfe;
            }
        }
        return this;
    }
    
    /**
     * Disable Feature.
     * 
     * @param featureID
     *            unique feature identifier.
     */
    public FF4j toggleOff(String uid) {
        try {
            getFeatureStore().toggleOff(uid);
        } catch (FeatureNotFoundException fnfe) {
             if (this.autoCreateFeatures) {
                 getFeatureStore().create(new Feature(uid).toggleOff());
             } else {
                throw fnfe;
             }
        }
        return this;
    }
    
    /**
     * Enable Feature.
     * 
     * @param featureID
     *            unique feature identifier.
     */
    @Deprecated
    public FF4j enable(String uid) {
        return toggleOn(uid);
    }
    
    /**
     * Disable Feature.
     * 
     * @param featureID
     *            unique feature identifier.
     */
    @Deprecated
    public FF4j disable(String uid) {
        return toggleOff(uid);
    }
    
    /**
     * Create new Feature.
     * 
     * @param featureID
     *            unique feature identifier.
     */
    public FF4j createFeature(Feature fp) {
        getFeatureStore().create(fp);
        return this;
    }
    
    /**
     * Create new Property.
     * 
     * @param featureID
     *            unique feature identifier.
     */
    public FF4j createProperty(Property<?> prop) {
        getPropertiesStore().create(prop);
        return this;
    }

    /**
     * The feature will be create automatically if the boolea, autocreate is enabled.
     * 
     * @param featureID
     *            target feature ID
     * @return target feature.
     */
    public Feature getFeature(String uid) {
        Optional <Feature > oFeature = getFeatureStore().findById(uid);
        if (!oFeature.isPresent()) {
            if (autoCreateFeatures) {
                Feature autoFeature = new Feature(uid).toggleOff();
                getFeatureStore().create(autoFeature);
                return autoFeature;
            }
            throw new FeatureNotFoundException(uid);
        }
        return oFeature.get();
    }
    
    /**
     * Read property in Store
     * 
     * @param featureID
     *            target feature ID
     * @return target feature.
     */
    public Property<?> getProperty(String propertyName) {
       return getPropertiesStore().read(propertyName);
    }
    
    /**
     * Help to import features.
     * 
     * @param features
     *      set of features.
     * @return
     *      a reference to this object (builder pattern).
     *
     * @since 1.6
     */
    public FF4j importFeatures(Collection < Feature> features) {
        getFeatureStore().save(features);
        return this;
    }
    
    /**
     * Help to import propertiess.
     * 
     * @param features
     *      set of features.
     * @return
     *      a reference to this object (builder pattern).
     *
     * @since 1.6
     */
    public FF4j importProperties(Collection < Property<?>> properties) {
        getPropertiesStore().save(properties);
        return this;
    }

    /**
     * Enable autocreation of features when not found.
     * 
     * @param flag
     *            target value for autocreate flag
     * @return current instance
     */
    public FF4j autoCreate(boolean flag) {
        setAutocreate(flag);
        return this;
    }
    
    /**
     * Enable a cache proxy.
     * 
     * @param cm
     *      current cache manager
     * @return
     *      current ff4j bean
     */
    public FF4j cache(CacheManager<String, Feature> cm, CacheManager<String, Property<?>> pm) {
        setFeatureStore(new FeatureStoreCacheProxy(getFeatureStore(), cm));
        setPropertiesStore(new PropertyStoreCacheProxy(getPropertiesStore(), pm));
        return this;
    }
    
    /**
     * Parse configuration file.
     *
     * @param fileName
     *      target file
     * @return
     *      current configuration as XML
     */
    public XmlConfig parseXmlConfig(String fileName) {
        InputStream xmlIN = getClass().getClassLoader().getResourceAsStream(fileName);
        if (xmlIN == null) {
            throw new IllegalArgumentException("Cannot parse XML file " + fileName + " - file not found");
        }
        return new XmlParser().parseConfigurationFile(xmlIN);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        long uptime = System.currentTimeMillis() - startTime;
        long daynumber = uptime / (1000 * 3600 * 24L);
        uptime = uptime - daynumber * 1000 * 3600 * 24L;
        long hourNumber = uptime / (1000 * 3600L);
        uptime = uptime - hourNumber * 1000 * 3600L;
        long minutenumber = uptime / (1000 * 60L);
        uptime = uptime - minutenumber * 1000 * 60L;
        long secondnumber = uptime / 1000L;
        sb.append("\"uptime\":\"");
        sb.append(daynumber + " day(s) ");
        sb.append(hourNumber + " hours(s) ");
        sb.append(minutenumber + " minute(s) ");
        sb.append(secondnumber + " seconds\"");
        sb.append(attributeAsJson("autocreate", autoCreateFeatures));
        sb.append(attributeAsJson("source", source));
        sb.append(attributeAsJson("version", version));
        if (getFeatureStore() != null) {
            sb.append(objectAsJson("featuresStore", getFeatureStore().toString()));
        }
        if (getPropertiesStore() != null) {
            sb.append(objectAsJson("propertiesStore", getPropertiesStore().toString()));
        }
        sb.append("}");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // ------------------- GETTERS & SETTERS -----------------------------------
    // -------------------------------------------------------------------------
       
    /**
     * NON Static to be use by Injection of Control.
     * 
     * @param fbs
     *            target store.
     */
    public void setFeatureStore(FeatureStore fbs) {
        this.featureStore = fbs;
    }

    /**
     * Setter accessor for attribute 'autocreate'.
     * 
     * @param autocreate
     *            new value for 'autocreate '
     */
    public void setAutocreate(boolean autocreate) {
        this.autoCreateFeatures = autocreate;
    }

    
    /**
     * Create tables/collections/columns in DB (if required).
     */
    public void createSchema() {
        if (null != getFeatureStore()) {
            getFeatureStore().createSchema();
        }
        if (null != getPropertiesStore()) {
            getPropertiesStore().createSchema();
        }
        // AuditTrail
        // Feature Usage
        // SecurityManager
    }
    
    /**
     * Access store as static way (single store).
     * 
     * @return current store
     */
    public FeatureStore getFeatureStore() {
        return featureStore;
    }
    
    /**
     * Getter accessor for attribute 'pStore'.
     *
     * @return
     *       current value of 'pStore'
     */
    public PropertyStore getPropertiesStore() {
        return propertyStore;
    }

    /**
     * Getter accessor for attribute 'startTime'.
     *
     * @return
     *       current value of 'startTime'
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * Getter accessor for attribute 'version'.
     *
     * @return
     *       current value of 'version'
     */
    public String getVersion() {
        return version;
    }   

    /**
     * Setter accessor for attribute 'pStore'.
     * @param pStore
     * 		new value for 'pStore '
     */
    public void setPropertiesStore(PropertyStore pStore) {
        this.propertyStore = pStore;
    }
    
    /**
     * Required for spring namespace and 'fileName' attribut on ff4j tag.
     *
     * @param fname
     *      target name
     */
    public void setFileName(String fname)    { /** empty setter for Spring framework */ }
    public void setAuthManager(String mnger) { /** empty setter for Spring framework */}

    /**
     * Getter accessor for attribute 'source'.
     *
     * @return
     *       current value of 'source'
     */
    public String getSource() {
        return source;
    }
    
    /**
     * Reach concrete implementation of the featureStore.
     *
     * @return
     */
    public FeatureStore getConcreteFeatureStore() {
        return getConcreteFeatureStore(getFeatureStore());
    }
    
    /**
     * Reach concrete implementation of the propertyStore.
     *
     * @return
     */
    public PropertyStore getConcretePropertyStore() {
        return getConcretePropertyStore(getPropertiesStore());
    }
    
    /**
     * try to fetch CacheProxy (cannot handled proxy CGLIB, ASM or any bytecode manipulation).
     *
     * @return
     */
    public FeatureStoreCacheProxy getFeatureStoreCacheProxy() {
        FeatureStore fs = getFeatureStore();
        // Pass through audit proxy if exists
       if (fs instanceof FeatureStoreCacheProxy) {
            return (FeatureStoreCacheProxy) fs;
        }
        return null;
    }
    
    /**
     * try to fetch CacheProxy (cannot handled proxy CGLIB, ASM or any bytecode manipulation).
     *
     * @return
     */
    public PropertyStoreCacheProxy getPropertyStoreCacheProxy() {
        PropertyStore fs = getPropertiesStore();
        if (fs instanceof PropertyStoreCacheProxy) {
            return (PropertyStoreCacheProxy) fs;
        }
        return null;
    }
    
    /**
     * Return concrete implementation.
     *
     * @param fs
     *      current featureStore
     * @return
     *      target featureStore
     */
    private FeatureStore getConcreteFeatureStore(FeatureStore fs) {
         if (fs instanceof FeatureStoreCacheProxy) {
            return getConcreteFeatureStore(((FeatureStoreCacheProxy) fs).getTargetFeatureStore());
        }
        return fs;
    }
    
    /**
     * Return concrete implementation.
     *
     * @param fs
     *      current propertyStoyre
     * @return
     *      target propertyStoyre
     */
    private PropertyStore getConcretePropertyStore(PropertyStore ps) {
        if (ps instanceof PropertyStoreCacheProxy) {
            return getConcretePropertyStore(((PropertyStoreCacheProxy) ps).getTargetPropertyStore());
        }
        return ps;
    }

    /**
     * Getter accessor for attribute 'securityManager'.
     *
     * @return
     *       current value of 'securityManager'
     */
    public FF4JSecurityManager getSecurityManager() {
        return securityManager;
    }

    /**
     * Setter accessor for attribute 'securityManager'.
     * @param securityManager
     * 		new value for 'securityManager '
     */
    public void setSecurityManager(FF4JSecurityManager securityManager) {
        this.securityManager = securityManager;
    }
    
}
