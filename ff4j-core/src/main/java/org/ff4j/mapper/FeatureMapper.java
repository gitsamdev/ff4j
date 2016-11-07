package org.ff4j.mapper;

import org.ff4j.feature.Feature;

/**
 * Specialization of the interface.
 *
 * @author Cedrick LUNVEN (@clunven)
 *
 * @param <STORE_OBJ>
 *      target driver object.
 */
public interface FeatureMapper <REQ , RES> extends Mapper < Feature, REQ, RES > {}
