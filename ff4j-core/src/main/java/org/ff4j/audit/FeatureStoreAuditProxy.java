package org.ff4j.audit;

import static org.ff4j.audit.EventConstants.ACTION_CLEAR;
import static org.ff4j.audit.EventConstants.ACTION_CREATE;
import static org.ff4j.audit.EventConstants.ACTION_CREATESCHEMA;
import static org.ff4j.audit.EventConstants.ACTION_DELETE;
import static org.ff4j.audit.EventConstants.ACTION_TOGGLE_OFF;
import static org.ff4j.audit.EventConstants.ACTION_TOGGLE_ON;
import static org.ff4j.audit.EventConstants.ACTION_UPDATE;
import static org.ff4j.audit.EventConstants.TARGET_FEATURE;
import static org.ff4j.audit.EventConstants.TARGET_FSTORE;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Stream;

import org.ff4j.FF4j;
import org.ff4j.feature.Feature;
import org.ff4j.store.FeatureStore;

/**
 * Proxy to publish operation to audit.
 *
 * @author Cedrick Lunven (@clunven)
 */
public class FeatureStoreAuditProxy implements FeatureStore {

    /** Current FeatureStore. */
    private FeatureStore target = null;
    
    /** Reference. */
    private FF4j ff4j = null;
    
    /**
     * Only constructor.
     *
     * @param pTarget
     */
    public FeatureStoreAuditProxy(FF4j pFF4j, FeatureStore pTarget) {
        this.target = pTarget;
        this.ff4j   = pFF4j;
    }
    
    /** {@inheritDoc} */
    @Override
    public void createSchema() {
        target.createSchema();
        publish(builder(ACTION_CREATESCHEMA).feature("For Features"));
    }
    
    /** {@inheritDoc} */
    @Override
    public void enable(String uid) {
        long start = System.nanoTime();
        target.enable(uid);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_TOGGLE_ON).feature(uid).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void disable(String uid) {
        long start = System.nanoTime();
        target.disable(uid);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_TOGGLE_OFF).feature(uid).duration(duration));
    }    

    /** {@inheritDoc} */
    @Override
    public void create(Feature fp) {
        long start = System.nanoTime();
        target.create(fp);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_CREATE).feature(fp.getUid()).duration(duration));
    }
    
    /** {@inheritDoc} */
    @Override
    public void delete(String uid) {
        long start = System.nanoTime();
        target.delete(uid);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_DELETE).feature(uid).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void update(Feature fp) {
        long start = System.nanoTime();
        target.update(fp);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_UPDATE).feature(fp.getUid()).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void grantRoleOnFeature(String uid, String roleName) {
        System.out.println("GRANT");
        long start = System.nanoTime();
        target.grantRoleOnFeature(uid, roleName);
        long duration = System.nanoTime() - start;
        publish(builder("GRANT ROLE " + roleName).feature(uid).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void removeRoleFromFeature(String uid, String roleName) {
        long start = System.nanoTime();
        target.removeRoleFromFeature(uid, roleName);
        long duration = System.nanoTime() - start;
        publish(builder("REMOVE ROLE " + roleName).feature(uid).duration(duration));
    }
    
    /** {@inheritDoc} */
    @Override
    public void enableGroup(String groupName) {
        long start = System.nanoTime();
        target.enableGroup(groupName);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_TOGGLE_ON).group(groupName).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void disableGroup(String groupName) {
        long start = System.nanoTime();
        target.disableGroup(groupName);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_TOGGLE_OFF).group(groupName).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void addToGroup(String uid, String groupName) {
        long start = System.nanoTime();
        target.addToGroup(uid, groupName);
        long duration = System.nanoTime() - start;
        publish(builder("ADD TO GROUP " + groupName).feature(uid).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void removeFromGroup(String uid, String groupName) {
        long start = System.nanoTime();
        target.removeFromGroup(uid, groupName);
        long duration = System.nanoTime() - start;
        publish(builder("ADD TO GROUP " + groupName).feature(uid).duration(duration));
    }

    /** {@inheritDoc} */
    @Override
    public void deleteAll() {
        long start = System.nanoTime();
        target.deleteAll();
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_CLEAR).type(TARGET_FSTORE)
                .name(ff4j.getFeatureStore().getClass().getName())
                .duration(duration));
    }
    
    /**
     * Init a new builder;
     *
     * @return
     *      new builder
     */
    private EventBuilder builder(String action) {
        return new EventBuilder(ff4j).type(TARGET_FEATURE).action(action);
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
    
    /** {@inheritDoc} */
    @Override
    public boolean exists(String uid) {
        return target.exists(uid);
    }

    /** {@inheritDoc} */
    @Override
    public Optional < Feature > findById(String uid) {
        return target.findById(uid);
    }

    /** {@inheritDoc} */
    @Override
    public Stream < Feature > findAll() {
        return target.findAll();
    }

    /** {@inheritDoc} */
    @Override
    public boolean existGroup(String groupName) {
        return target.existGroup(groupName);
    }

    /** {@inheritDoc} */
    @Override
    public Stream < Feature > readGroup(String groupName) {
        return target.readGroup(groupName);
    }

    /** {@inheritDoc} */
    @Override
    public Stream < String >  readAllGroups() {
        return target.readAllGroups();
    }
    
    /** {@inheritDoc} */
    @Override
    public void save(Collection<Feature> features) {
        long start = System.nanoTime();
        target.save(features);
        long duration = System.nanoTime() - start;
        publish(builder(ACTION_UPDATE).type(TARGET_FSTORE)
                .name(ff4j.getFeatureStore().getClass().getName())
                .duration(duration));
    }
    
    /** {@inheritDoc} */
    @Override
    public long count() {
        return target.count();
    }
    
    /** {@inheritDoc} */
    @Override
    public void delete(Iterable<? extends Feature> entities) {
       if (entities != null) {
           entities.forEach(this::delete);
       }
    }
    
    /** {@inheritDoc} */
    @Override
    public Feature read(String id) {
        return target.read(id);
    }

    /** {@inheritDoc} */
    @Override
    public void delete(Feature entity) {
        this.delete(entity.getUid());
    }

    /** {@inheritDoc} */
    @Override
    public Stream<Feature> findAll(Iterable<String> ids) {
        return target.findAll();
    }   

	/**
	 * Getter accessor for attribute 'target'.
	 *
	 * @return
	 *       current value of 'target'
	 */
	public FeatureStore getTarget() {
		return target;
	}    

    
}