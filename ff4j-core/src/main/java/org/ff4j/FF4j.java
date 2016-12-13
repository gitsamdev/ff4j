package org.ff4j;

import static org.ff4j.utils.JsonUtils.attributeAsJson;
import static org.ff4j.utils.JsonUtils.objectAsJson;

import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

import org.ff4j.audit.FeatureStoreAuditProxy;
import org.ff4j.audit.FeatureUsageTracking;
import org.ff4j.audit.PropertyStoreAuditProxy;
import org.ff4j.cache.CacheManager;
import org.ff4j.cache.FeatureStoreCacheProxy;
import org.ff4j.cache.PropertyStoreCacheProxy;
import org.ff4j.conf.XmlConfig;
import org.ff4j.conf.XmlParser;
import org.ff4j.event.Event;
import org.ff4j.event.EventPublisher;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.feature.FlippingStrategy;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.inmemory.FeatureUsageTrackingInMemory;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.ff4j.observable.AbstractObservableMixin;
import org.ff4j.observable.FeatureUsageListener;
import org.ff4j.property.Property;
import org.ff4j.security.AuthorizationsManager;
import org.ff4j.store.FeatureStore;
import org.ff4j.store.PropertyStore;

/**
 * Main class and public api to work with framework FF4j.
 * 
 * <ul>It proposes a few underlying elements :
 *  <li>{@link FeatureStore} is used to store status of manipulated features.
 *  <li>{@link PropertyStore} is used to store properties values.
 *  <li>{@link FeatureUsageTracking} is used to store audit information.
 *  <li>{@link AuthorizationsManager} to limit access to features is relevant (permissions).
 * </ul>
 *
 * @author Cedrick Lunven (@clunven)
 * @since 1.0
 */
public class FF4j extends AbstractObservableMixin < FeatureUsageListener > {
    
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
    
    /** Security policy to limit access through ACL with {@link AuthorizationsManager}. */
    private AuthorizationsManager authorizationsManager = null;
    
    /** Event Publisher (threadpool, executor) to send data into {@link FeatureUsageTracking} */
    private EventPublisher eventPublisher = null;
   
    /** This attribute indicates to stop the event publisher. */
    private volatile boolean shutdownEventPublisher;
    
    /** Post Processing like audit enable. */
    private boolean initialized = false;
    
    /**
     * Base constructor to allows instantiation through IoC.
     * Default stores are created and will work in memory.
     */
    public FF4j() {
        registerListener("DefaultUsageTracking", new FeatureUsageTrackingInMemory());
    }

    /**
     * Constructor initializing ff4j with an InMemoryStore
     */
    public FF4j(String xmlFile) {
        this.featureStore  = new FeatureStoreInMemory(xmlFile);
        this.propertyStore = new PropertyStoreInMemory(xmlFile);
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
        
        // 
        this.notify((listener) -> listener.onFeatureExecuted(uid));
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
        return getAuthorizationsManager() == null 
                || !feature.getPermissions().isPresent() 
                || feature.getPermissions().get().isEmpty() 
                || feature.getPermissions().get().stream().anyMatch(
                        getAuthorizationsManager().getCurrentUserPermissions()::contains);
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
        if (getAuthorizationsManager() != null) {
            sb.append(objectAsJson("authorizationsManager", getAuthorizationsManager().toString()));
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
     * Getter accessor for attribute 'authorizationsManager'.
     * 
     * @return current value of 'authorizationsManager'
     */
    public AuthorizationsManager getAuthorizationsManager() {
        return authorizationsManager;
    }

    /**
     * Setter accessor for attribute 'authorizationsManager'.
     * 
     * @param authorizationsManager
     *            new value for 'authorizationsManager '
     */
    public void setAuthorizationsManager(AuthorizationsManager authorizationsManager) {
        this.authorizationsManager = authorizationsManager;
    }

    /**
     * Setter accessor for attribute 'eventPublisher'.
     *
     * @param eventPublisher
     *            new value for 'eventPublisher '
     */
    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Initialization of background components.
     */
    private synchronized void init() {
        
        // Event Publisher
        eventPublisher = new EventPublisher(null);
        this.shutdownEventPublisher = true;
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
        // User
    }
    
    /**
     * Access store as static way (single store).
     * 
     * @return current store
     */
    public FeatureStore getFeatureStore() {
        if (!initialized) {
            init();
        }
        return featureStore;
    }
    
    /**
     * Getter accessor for attribute 'eventPublisher'.
     * 
     * @return current value of 'eventPublisher'
     */
    public EventPublisher getEventPublisher() {
        if (!initialized) { 
            init();
        }
        return eventPublisher;
    }
    
    /**
     * Getter accessor for attribute 'pStore'.
     *
     * @return
     *       current value of 'pStore'
     */
    public PropertyStore getPropertiesStore() {
        if (!initialized) {
            init();
        }
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
     * Shuts down the event publisher if we actually started it (As opposed to
     * having it dependency-injected).
     */
    public void stop() {
        if (this.eventPublisher != null && this.shutdownEventPublisher) {
            this.eventPublisher.stop();
        }
    }

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
        if (fs instanceof FeatureStoreAuditProxy) {
            fs = ((FeatureStoreAuditProxy) fs).getTarget();
        }
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
        // Pass through audit proxy if exists
        if (fs instanceof PropertyStoreAuditProxy) {
            fs = ((PropertyStoreAuditProxy) fs).getTarget();
        }
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
        if (fs instanceof FeatureStoreAuditProxy) {
            return getConcreteFeatureStore(((FeatureStoreAuditProxy) fs).getTarget());
        } else if (fs instanceof FeatureStoreCacheProxy) {
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
        if (ps instanceof PropertyStoreAuditProxy) {
            return getConcretePropertyStore(((PropertyStoreAuditProxy) ps).getTarget());
        } else if (ps instanceof PropertyStoreCacheProxy) {
            return getConcretePropertyStore(((PropertyStoreCacheProxy) ps).getTargetPropertyStore());
        }
        return ps;
    }
    
}
