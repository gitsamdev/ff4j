package org.ff4j;

import static org.ff4j.audit.EventConstants.ACTION_CHECK_OFF;
import static org.ff4j.audit.EventConstants.ACTION_CHECK_OK;
import static org.ff4j.audit.EventConstants.SOURCE_JAVA;
import static org.ff4j.utils.JsonUtils.attributeAsJson;
import static org.ff4j.utils.JsonUtils.objectAsJson;

import java.io.InputStream;
import java.util.Collection;

import org.ff4j.audit.EventBuilder;
import org.ff4j.audit.EventPublisher;
import org.ff4j.audit.FeatureStoreAuditProxy;
import org.ff4j.audit.PropertyStoreAuditProxy;
import org.ff4j.cache.FF4jCacheManager;
import org.ff4j.cache.FF4jCacheProxy;
import org.ff4j.conf.XmlConfig;
import org.ff4j.conf.XmlParser;
import org.ff4j.exception.FeatureNotFoundException;
import org.ff4j.feature.Feature;
import org.ff4j.feature.FlippingExecutionContext;
import org.ff4j.feature.FlippingStrategy;
import org.ff4j.inmemory.EventRepositoryInMemory;
import org.ff4j.inmemory.FeatureStoreInMemory;
import org.ff4j.inmemory.PropertyStoreInMemory;
import org.ff4j.property.Property;
import org.ff4j.security.AuthorizationsManager;
import org.ff4j.store.EventRepository;
import org.ff4j.store.FeatureStore;
import org.ff4j.store.PropertyStore;

/**
 * Principal class stands as public api to work with FF4J.
 *
 * <ul>
 * <p>
 * <li>
 * It embeddes a {@link FeatureStore} to record features statused. By default, features are stored into memory but you would like
 * to persist them in an external storage (as database) and choose among implementations available in different modules (jdbc,
 * mongo, http...).
 * </p>
 * 
 * <p>
 * <li>It embeddes a {@link AuthorizationsManager} to add permissions and limit usage of features to granted people. FF4J does not
 * created roles, it's rely on external security provider as SpringSecurity Apache Chiro.
 * </p>
 * 
 * <p>
 * <li>It embeddes a {@link EventRepository} to monitoring actions performed on features.
 * </p>
 * 
 * </ul>
 * 
 * @author Cedrick Lunven (@clunven)
 */
public class FF4j {
    
    /** Intialisation. */
    private final long startTime = System.currentTimeMillis();

    /** Version of ff4j. */
    private final String version = getClass().getPackage().getImplementationVersion();
    
    /** Source of initialization (JAVA_API, WEBAPI, SSH, CONSOLE...). */
    private String source = SOURCE_JAVA;
    
    /** Do not through {@link FeatureNotFoundException} exception and but feature is required. */
    private boolean autocreate = false;
    
    /** Storage to persist feature within {@link FeatureStore}. */
    private FeatureStore featureStore = new FeatureStoreInMemory();
    
    /** Storage to persist properties within {@link PropertyStore}. */
    private PropertyStore propertyStore = new PropertyStoreInMemory();
   
    /** Security policy to limit access through ACL with {@link AuthorizationsManager}. */
    private AuthorizationsManager authorizationsManager = null;
    
    /** Capture informations relative to audit. */
    private boolean enableAudit = false;
   
    /** Repository for audit event. */
    private EventRepository eventRepository = new EventRepositoryInMemory();

    /** Event Publisher (threadpool, executor) to send data into {@link EventRepository} */
    private EventPublisher eventPublisher = null;
   
    /** This attribute indicates to stop the event publisher. */
    private volatile boolean shutdownEventPublisher;
    
    /** Post Processing like audit enable. */
    private boolean initialized = false;

    /** Hold flipping execution context as Thread-safe data. */
    private ThreadLocal<FlippingExecutionContext> currentExecutionContext = new ThreadLocal<FlippingExecutionContext>();
    
    /**
     * Default constructor to allows instantiation through IoC. The created store is an empty {@link FeatureStoreInMemory}.
     */
    public FF4j() {
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
    public boolean check(String uid, FlippingExecutionContext executionContext) {
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
        return isFeatureToggled(uid, strats, currentExecutionContext.get());
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
    public boolean checkOveridingStrategy(String uid, FlippingStrategy strats, FlippingExecutionContext executionContext) {
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
    protected boolean isFeatureToggled(String uid, FlippingStrategy strats, FlippingExecutionContext executionContext) {
        
        // Read from store
        Feature feature = getFeature(uid);
        
        // First level check (status = ON and permission OK)
        boolean featureToggled = feature.isEnable() && isAllowed(feature);
        if (featureToggled) {
            if (strats != null) {
                featureToggled = strats.evaluate(uid, getFeatureStore(), executionContext);
            } else if (feature.getFlippingStrategy().isPresent()) {
                featureToggled = feature.getFlippingStrategy().get().evaluate(uid, getFeatureStore(), executionContext);
            }
        }
        
        // Update current context
        if (null != executionContext) {
            currentExecutionContext.set(executionContext);
        }
        
        // Publish audit
        if (isEnableAudit()) {
            getEventPublisher().publish(new EventBuilder(this)
                        .feature(uid)
                        .action(featureToggled ? ACTION_CHECK_OK : ACTION_CHECK_OFF)
                        .build());
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
        return getAuthorizationsManager() == null || 
               !feature.getPermissions().isPresent() ||
               feature.getPermissions().get().isEmpty() ||
               feature.getPermissions().get().stream().anyMatch(getAuthorizationsManager().getCurrentUserPermissions()::contains);
    }
    
    /**
     * Enable Feature.
     * 
     * @param featureID
     *            unique feature identifier.
     */
    public FF4j toggleOn(String uid) {
        try {
            getFeatureStore().enable(uid);
        } catch (FeatureNotFoundException fnfe) {
            if (this.autocreate) {
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
            getFeatureStore().disable(uid);
        } catch (FeatureNotFoundException fnfe) {
             if (this.autocreate) {
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
    public Feature getFeature(String featureID) {
        Feature fp = null;
        try {
            fp = getFeatureStore().read(featureID);
        } catch (FeatureNotFoundException fnfe) {
            if (this.autocreate) {
                fp = new Feature(featureID).toggleOff();
                getFeatureStore().create(fp);
            } else {
                throw fnfe;
            }
        }
        return fp;
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
     * Enable auditing of features when not found.
     * 
     * @param flag
     *            target value for autocreate flag
     * @return current instance
     */
    public FF4j audit(boolean val) {
         setEnableAudit(val);
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
    public FF4j cache(FF4jCacheManager cm) {
        FF4jCacheProxy cp = new FF4jCacheProxy(getFeatureStore(), getPropertiesStore(), cm);
        setFeatureStore(cp);
        setPropertiesStore(cp);
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
        sb.append(attributeAsJson("autocreate", isAutocreate()));
        sb.append(attributeAsJson("version", version));
        if (getFeatureStore() != null) {
            sb.append(objectAsJson("featuresStore", getFeatureStore().toString()));
        }
        if (getPropertiesStore() != null) {
            sb.append(objectAsJson("propertiesStore", getPropertiesStore().toString()));
        }
        if (getEventRepository() != null) {
            sb.append(objectAsJson("eventRepository", getEventRepository().toString()));
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
        this.autocreate = autocreate;
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
     * Getter accessor for attribute 'eventRepository'.
     * 
     * @return current value of 'eventRepository'
     */
    public EventRepository getEventRepository() {
        return eventRepository;
    }

    /**
     * Setter accessor for attribute 'eventRepository'.
     * 
     * @param eventRepository
     *            new value for 'eventRepository '
     */
    public void setEventRepository(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
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
        
        // Execution Context
        FlippingExecutionContext context = new FlippingExecutionContext();
        this.currentExecutionContext.set(context);
        
        // Event Publisher
        eventPublisher = new EventPublisher(eventRepository);
        this.shutdownEventPublisher = true;
        
        // Audit is enabled, proxified stores for auditing
        if (isEnableAudit()) {
        	
        	if (featureStore != null && !(featureStore instanceof FeatureStoreAuditProxy)) {
                this.featureStore = new FeatureStoreAuditProxy(this, featureStore);
            }
            if (propertyStore != null && !(propertyStore instanceof PropertyStoreAuditProxy)) { 
                this.propertyStore = new PropertyStoreAuditProxy(this, propertyStore);
            }
        } else {
        	
        	 // Audit is disabled but could have been enabled before... removing PROXY if relevant
        	 if (featureStore != null && featureStore instanceof FeatureStoreAuditProxy) {
        		 this.featureStore = ((FeatureStoreAuditProxy) featureStore).getTarget();
        	 }
        	 if (propertyStore != null && propertyStore instanceof PropertyStoreAuditProxy) { 
        		 this.propertyStore = ((PropertyStoreAuditProxy) propertyStore).getTarget();
             }
        }
        
        // Flag as OK
        this.initialized = true;
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
        if (null != getEventRepository()) {
            getEventRepository().createSchema();
        }
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
     * Initialize flipping execution context.
     *
     * @return
     *      get current context
     */
    public FlippingExecutionContext getCurrentContext() {
        if (!initialized) {
            init();
        }
        
        if (null == this.currentExecutionContext.get()) {
            this.currentExecutionContext.set(new FlippingExecutionContext());
        }
        return this.currentExecutionContext.get();
    }

    /**
     * Getter accessor for attribute 'autocreate'.
     *
     * @return current value of 'autocreate'
     */
    public boolean isAutocreate() {
        return autocreate;
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
     * Clear context.
     */
    public void removeCurrentContext() {
        this.currentExecutionContext.remove();
    }

    /**
     * Getter accessor for attribute 'enableAudit'.
     *
     * @return
     *       current value of 'enableAudit'
     */
    public boolean isEnableAudit() {
        return enableAudit;
    }

    /**
     * Setter accessor for attribute 'enableAudit'.
     *
     * @param enableAudit
     * 		new value for 'enableAudit '
     */
    public void setEnableAudit(boolean enableAudit) {
    	this.enableAudit = enableAudit;
    	
    	// if you disable the audit : the auditProxy must be destroy and use targets
    	initialized = false;
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
    public FF4jCacheProxy getCacheProxy() {
        FeatureStore fs = getFeatureStore();
        // Pass through audit proxy if exists
        if (fs instanceof FeatureStoreAuditProxy) {
            fs = ((FeatureStoreAuditProxy) fs).getTarget();
        }
        if (fs instanceof FF4jCacheProxy) {
            return (FF4jCacheProxy) fs;
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
        } else if (fs instanceof FF4jCacheProxy) {
            return getConcreteFeatureStore(((FF4jCacheProxy) fs).getTargetFeatureStore());
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
        } else if (ps instanceof FF4jCacheProxy) {
            return getConcretePropertyStore(((FF4jCacheProxy) ps).getTargetPropertyStore());
        }
        return ps;
    }
    
}
