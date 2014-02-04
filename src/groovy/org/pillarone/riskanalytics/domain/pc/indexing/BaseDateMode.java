package org.pillarone.riskanalytics.domain.pc.indexing;

import java.util.Map;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum BaseDateMode {
    START_OF_PROJECTION, DATE_OF_LOSS, FIXED_DATE, DAY_BEFORE_FIRST_PERIOD;

    public Object getConstructionString(Map parameters) {
        return getClass().getName() + "." + this;
    }
}
