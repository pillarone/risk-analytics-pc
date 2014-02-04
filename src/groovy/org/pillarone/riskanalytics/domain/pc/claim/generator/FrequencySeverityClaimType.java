package org.pillarone.riskanalytics.domain.pc.claim.generator;

import java.util.Map;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum FrequencySeverityClaimType {
    SINGLE, AGGREGATED_EVENT;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
