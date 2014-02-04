package org.pillarone.riskanalytics.domain.pc.indexing;

import java.util.Map;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum IndexMode {
    CONTINUOUS, STEPWISE_PREVIOUS, STEPWISE_NEXT;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
