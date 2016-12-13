package org.ff4j.audit;

import static org.ff4j.event.EventConstants.ACTION_CLEAR;
import static org.ff4j.event.EventConstants.ACTION_CREATE;
import static org.ff4j.event.EventConstants.ACTION_CREATESCHEMA;
import static org.ff4j.event.EventConstants.ACTION_DELETE;
import static org.ff4j.event.EventConstants.ACTION_UPDATE;
import static org.ff4j.event.EventConstants.TARGET_PROPERTY;
import static org.ff4j.event.EventConstants.TARGET_PSTORE;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.ff4j.FF4j;
import org.ff4j.event.EventBuilder;
import org.ff4j.event.EventPublisher;
import org.ff4j.property.Property;
import org.ff4j.store.PropertyStore;

/**
 * Implementation of audit on top of store.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class PropertyStoreAuditProxy implements PropertyStore {

    /** Current FeatureStore. */
    private PropertyStore target = null;
    
    /** Reference. */
    private FF4j ff4j = null;
    
    /**
     * Only constructor.
     *
     * @param pTarget
     */
    public PropertyStoreAuditProxy(FF4j pFF4j, PropertyStore pTarget) {
        this.target = pTarget;
        this.ff4j   = pFF4j;
    }

    /** {@inheritDoc} */
    @Override
    public  void create(Property<?> prop) {
        long start = System.nanoTime();
        target.create(prop);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_CREATE)
                    .property(prop.getUid())
                    .value(prop.asString())
                    .duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void update(String name, String newValue) {
        long start = System.nanoTime();
        target.update(name, newValue);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_UPDATE)
                    .property(name)
                    .value(newValue)
                    .duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void update(Property<?> prop) {
        long start = System.nanoTime();
        target.update(prop);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_UPDATE)
                    .property(prop.getUid())
                    .value(prop.asString())
                    .duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void delete(String name) {
        long start = System.nanoTime();
        target.delete(name);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_DELETE)
                    .property(name)
                    .duration(duration));
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean exists(String name) {
        return target.exists(name);
    }

    /** {@inheritDoc} */
    @Override
    public Optional <Property<?> > findById(String name) {
        return target.findById(name);
    }
    
    /** {@inheritDoc} */
    @Override
    public Property<?> read(String name) {
        return target.read(name);
    }
    
    /** {@inheritDoc} */
    @Override
    public Property<?> read(String name, Property<?> defaultValue) {
        return target.read(name, defaultValue);
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream < Property<?> > findAll() {
        return target.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public Stream < String > listPropertyNames() {
        return target.listPropertyNames();
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        long start = System.nanoTime();
        target.deleteAll();
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_CLEAR).type(TARGET_PSTORE)
                .name(ff4j.getPropertiesStore().getClass().getName())
                .duration(duration));
    }
    
    /** {@inheritDoc} */
    @Override
    public Stream<Property<?>> findAll(Iterable<String> ids) {
        return target.findAll();
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<Property<?>> properties) {
        // Do not use target as the delete/create operation will be traced
        if (properties != null) {
            for (Property<?> property : properties) {
                if (exists(property.getUid())) {
                    delete(property.getUid());
                }
                create(property);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public long count() {
        return listPropertyNames().count();
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends Property<?>> entities) {
        if (entities != null) {
            entities.forEach(this::delete);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Property<?> entity) {
        delete(entity.getUid());
    }  
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        target.createSchema();
        publish(builder(ACTION_CREATESCHEMA).feature("For Properties"));
    }
    
    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return target.isEmpty();
    } 
    
    /**
     * Init a new builder;
     *
     * @return
     *      new builder
     */
    private EventBuilder builder(String action) {
        return new EventBuilder(ff4j).type(TARGET_PROPERTY).action(action);
    }
    
    /**
     * Publish target event to {@link EventPublisher}
     *
     * @param eb
     *      current builder
     */
    private void publish(EventBuilder eb) {
        ff4j.getEventPublisher().publish(eb.build());
    }

	/**
	 * Getter accessor for attribute 'target'.
	 *
	 * @return
	 *       current value of 'target'
	 */
	public PropertyStore getTarget() {
		return target;
	}

}
