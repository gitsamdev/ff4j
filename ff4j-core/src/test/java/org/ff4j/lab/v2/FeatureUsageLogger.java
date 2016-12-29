package org.ff4j.lab.v2;

import org.ff4j.audit.usage.FeatureUsageListener;
import org.ff4j.feature.Feature;

public class FeatureUsageLogger implements FeatureUsageListener {

    /** {@inheritDoc} */
    @Override
    public void onFeatureExecuted(Feature feature) {
        System.out.println("feature hit :" + feature.toJson());
    }

}
