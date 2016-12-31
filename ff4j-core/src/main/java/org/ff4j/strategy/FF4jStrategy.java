package org.ff4j.strategy;

import static org.ff4j.utils.JsonUtils.mapAsJson;
import static org.ff4j.utils.JsonUtils.valueAsJson;

import java.util.Map;

import org.ff4j.exception.PropertyAccessException;

/**
 * Pattern strategy, implementing evaluation at runtime (status or value).
 *
 * @author Cedrick LUNVEN  (@clunven)
 * 
 * @since 2.x
 */
public interface FF4jStrategy {
    
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
     * Instanciate flipping strategy from its class name.
     *
     * @param className
     *      current class name
     * @return
     *      the flipping strategy
     */
    static FF4jStrategy of(String uid, String className,  Map<String, String> initparams) {
        try {
            FF4jStrategy evalStrategy = (FF4jStrategy) Class.forName(className).newInstance();
            evalStrategy.init(uid, initparams);
            return evalStrategy;
        } catch (Exception ie) {
            throw new PropertyAccessException("Cannot instantiate Strategy, no default constructor available", ie);
        } 
    }
    /**
     * Generate flipping strategy as json.
     * 
     * @return
     *      flippling strategy as json.     
     */
     default String toJson() {
        StringBuilder json = new StringBuilder("{");
        json.append(valueAsJson("initParams") + ":");
        json.append(mapAsJson(getInitParams()));
        json.append("," + valueAsJson("type")  + ":");
        json.append(valueAsJson(getClass().getCanonicalName()));
        json.append("}");
        return json.toString();
    }

}
