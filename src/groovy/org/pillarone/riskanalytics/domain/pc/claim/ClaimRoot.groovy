package org.pillarone.riskanalytics.domain.pc.claim

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.event.IEvent
import org.pillarone.riskanalytics.domain.pc.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.utils.marker.IClaimMarker
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ClaimRoot implements IClaimRoot {

    private final double initial
    private final DateTime occurrenceDate
    private final DateTime inceptionDate
    private final ClaimType claimType
    private final IPerilMarker peril
    private final IEvent event

    private final PatternPacket reportingPattern
    private final PatternPacket payoutPattern

    private final FactorsPacket severityIndex
    private final FactorsPacket runOffIndex

    ClaimRoot(double initial, DateTime occurrenceDate, DateTime inceptionDate, ClaimType claimType, IPerilMarker peril,
              IEvent event = null, PatternPacket reportingPattern = null, PatternPacket payoutPattern = null,
              FactorsPacket severityIndex = null, FactorsPacket runOffIndex = null) {
        this.initial = initial
        this.occurrenceDate = occurrenceDate
        this.inceptionDate = inceptionDate
        this.claimType = claimType
        this.event = event
        this.peril = peril
        this.reportingPattern = reportingPattern
        this.payoutPattern = payoutPattern
        this.severityIndex = severityIndex
        this.runOffIndex = runOffIndex
    }

    @Override
    IPerilMarker peril() {
        peril
    }

    boolean hasPayouts() {
        payoutPattern
    }

    @Override
    DateTime occurrenceDate() {
        occurrenceDate
    }

    @Override
    double initial() {
        initial
    }

    boolean hasIBNR() {
        reportingPattern
    }

    PatternPacket payoutPattern() {
        payoutPattern
    }

    PatternPacket reportingPattern() {
        reportingPattern
    }
}
