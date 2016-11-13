package org.ff4j.strategy;

import static org.ff4j.utils.JsonUtils.mapAsJson;
import static org.ff4j.utils.JsonUtils.valueAsJson;

import java.util.Map;

import org.ff4j.exception.InvalidStrategyTypeException;

/**
 * Pattern strategy, implementing evaluation at runtime (status or value).
 *
 * @author Cedrick LUNVEN  (@clunven)
 * 
 * @since 2.x
 */
public interface FF4jExecutionStrategy {
    
    /**
     * Allow to parameterized Flipping Strategy
     * 
     * @param featureName
     *            current featureName
     * @param initValue
     *            initial Value
     */
    void init(String uid, Map<String, String> initParam);

    /**
     * Initial Parameters required to insert this new flipping.
     * 
     * @return initial parameters for this strategy
     */
    Map<String, String> getInitParams();
    
    /**
     * Generate flipping strategy as json.
     * 
     * @return
     *      flippling strategy as json.     
     */
     public static String asJson(final FF4jExecutionStrategy strategy) {
        if (strategy == null) return "null";
        StringBuilder json = new StringBuilder("{");
        json.append(valueAsJson("initParams") + ":");
        json.append(mapAsJson(strategy.getInitParams()));
        json.append("," + valueAsJson("type")  + ":");
        json.append(valueAsJson(strategy.getClass().getCanonicalName()));
        json.append("}");
        return json.toString();
    }

}
