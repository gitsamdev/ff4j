package org.ff4j.feature;

import static org.ff4j.utils.JsonUtils.attributeAsJson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.ff4j.FF4jContext;
import org.ff4j.FF4jEntity;

/**
 * Represents a feature flag identified by an unique identifier.
 *
 * <p>
 * Features Flags or Features Toggle have been introduced by Martin Fowler for continuous delivery perspective. It consists of
 * enable/disable some functionalities at runtime.
 *
 * <p>
 * <b>SecurityManagement :</b> Even a feature is enabled, you can limit its usage to a group of users (for instance BETA Tester)
 * before wide over all your users.
 * </p>
 *
 * @author Cedrick Lunven (@clunven)
 */
public class Feature extends FF4jEntity < Feature > {

    /** serial of the class. */
    private static final long serialVersionUID = -1345806526991179050L;

    /** State to decide to toggleOn or not. */
    private boolean enable = false;

    /** Feature could be grouped to enable/disable the whole group. */
    private Optional< String> group = Optional.empty();
    
    /** Custom behaviour to define if feature if enable or not e.g. A/B Testing capabilities. */
    private List < ToggleStrategy > toggleStrategies = new ArrayList<>();
    
    /**
     * Initialize {@link Feature} with id;
     * 
     * @param uid
     */
    public Feature(final String uid) {
        super(uid);
    }

    public Feature(final Feature f) {
        this(f.getUid(), f);
    }

    /**
     * Creatie new feature from existing one.
     * 
     * @param uid
     *            new uid (could be the same)
     * @param f
     */
    public Feature(final String uid, final Feature f) {
        super(uid, f);
        this.enable = f.isEnable();
        this.getToggleStrategies().addAll(f.getToggleStrategies());
        f.getGroup().ifPresent(g -> this.group = Optional.of(g));
    }

    public boolean isToggled(FF4jContext context) {
        if (!isEnable()) return false;
        
        // Break as soon as one of the strategy return false
        boolean toggled = true;
        Iterator<ToggleStrategy> iter = toggleStrategies.iterator();
        while (toggled && iter.hasNext()) {
            toggled = iter.next().isToggled(uid, context);
        }
        return toggled;
    }
    
    public Feature setGroup(String groupName) {
        this.group = Optional.ofNullable(groupName);
        return this;
    }

    public Feature setEnable(boolean status) {
        this.enable = status;
        return this;
    }

    public Feature toggleOn() {
        return setEnable(true);
    }

    public Feature toggleOff() {
        return setEnable(false);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return toJson();
    }

    /**
     * Convert Feature to JSON.
     * 
     * @return target json
     */
    public String toJson() {
        StringBuilder json = new StringBuilder("{");
        json.append(super.baseJson());
        json.append(attributeAsJson("enable", enable));
        group.ifPresent(g -> attributeAsJson("group", g));
        
        json.append(",\"toggleStrategies\": [");
        boolean first = true;
        for (ToggleStrategy element : getToggleStrategies()) {
            json.append(first ? "" : ",");
            json.append(element.toJson());
            first = false;
        }
        json.append("]");
        json.append("}");
        return json.toString();
    }

    public static Feature fromJson(String jsonString) {
        return null;
    }

    /**
     * Getter accessor for attribute 'enable'.
     *
     * @return current value of 'enable'
     */
    public boolean isEnable() {
        return enable;
    }

    /**
     * Getter accessor for attribute 'group'.
     *
     * @return current value of 'group'
     */
    public Optional<String> getGroup() {
        return group;
    }

    /**
     * Getter accessor for attribute 'toggleStrategies'.
     *
     * @return
     *       current value of 'toggleStrategies'
     */
    public List<ToggleStrategy> getToggleStrategies() {
        return toggleStrategies;
    }
    
    /**
     * Getter accessor for attribute 'toggleStrategies'.
     *
     * @return
     *       current value of 'toggleStrategies'
     */
    public Feature addToggleStrategy(ToggleStrategy ts) {
        getToggleStrategies().add(ts);
        return this;
    }
    
}
