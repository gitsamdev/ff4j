package org.ff4j.security;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.ff4j.FF4jContext;
import org.ff4j.feature.Feature;
import org.ff4j.feature.ToggleStrategy;

/**
 * Will check if feature is toggled based on ACL and current user.
 *
 * @author Cedrick LUNVEN  (@clunven)
 */
public class RbacStrategy implements ToggleStrategy {
    
    /** role. */
    private static final String PARAM_GRANTED_ROLES = "roles";
    
    /** users. */
    private static final String PARAM_GRANTED_USERS = "users";
    
    /** Grantees. */
    private FF4jGrantees grantees = new FF4jGrantees();
    
    /** {@inheritDoc} */
    @Override
    public void init(String uid, Map<String, String> initParam) {
        if (initParam.containsKey(PARAM_GRANTED_ROLES)) {
            grantees.getRoles().addAll(
                    Arrays.asList(initParam.get(PARAM_GRANTED_ROLES).split(",")));
        }
        if (initParam.containsKey(PARAM_GRANTED_USERS)) {
            grantees.getUsers().addAll(
                    Arrays.asList(initParam.get(PARAM_GRANTED_USERS).split(",")));
        }
        
    }

    /** {@inheritDoc} */
    @Override
    public Map<String, String> getInitParams() {
        Map <String, String> mapOfParams = new HashMap<>();
        mapOfParams.put(PARAM_GRANTED_ROLES, String.join(",", grantees.getRoles()));
        mapOfParams.put(PARAM_GRANTED_USERS, String.join(",", grantees.getUsers()));
        return mapOfParams;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isToggled(Feature feature, FF4jContext executionContext) {
        if (executionContext.getCurrentUser().isPresent()) {
            FF4jUser user = executionContext.getCurrentUser().get();
            return grantees.isUserGranted(user);
        }
        // user not present or guest
        return true;
    }

}
