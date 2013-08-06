package org.pillarone.riskanalytics.domain.pc.claim;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket;
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimRoot {
    IPerilMarker peril();
    PatternPacket reportingPattern();
    PatternPacket payoutPattern();
    boolean hasIBNR();
    boolean hasPayouts();

    DateTime occurrenceDate();

    double initial();
}
