package org.pillarone.riskanalytics.domain.pc.util;

import org.joda.time.DateTime;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
// todo: think, might be used by collectors and not the packet itself
public enum ValuationMode {
    PERIOD_CLOSING, LAST_UPDATE, PERIOD;
}