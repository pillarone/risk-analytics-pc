package org.pillarone.riskanalytics.domain.pc.claim

import com.google.common.collect.ImmutableList
import com.google.common.collect.SortedSetMultimap
import com.google.common.collect.TreeMultimap
import groovy.transform.CompileStatic
import org.apache.commons.lang.NotImplementedException
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.event.IEvent
import org.pillarone.riskanalytics.domain.pc.indexing.FactorsPacket
import org.pillarone.riskanalytics.domain.pc.pattern.DateFactors
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.util.SignTag
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker
import org.pillarone.riskanalytics.domain.utils.marker.ISegmentMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
// todo: think merging within R/I programs
@CompileStatic
class ClaimPacket extends Packet {

    private final double initial
    private final DateTime occurrenceDate
    private final DateTime inceptionDate
    private final ClaimType claimType
    private final IEvent event
    private final IPerilMarker peril

    private final PatternPacket reportingPattern
    private final PatternPacket payoutPattern

    private final FactorsPacket severityIndex
    private final FactorsPacket runOffIndex


    private final PeriodScope periodScope
    private int lastInitializedPeriod = Integer.MIN_VALUE

    private SortedSetMultimap<IComponentMarker, IClaim> claimsGross = TreeMultimap.create()
    private SortedSetMultimap<IComponentMarker, IClaim> claimsCeded = TreeMultimap.create()
    private SortedSetMultimap<IComponentMarker, IClaim> claimsNet = TreeMultimap.create()


    ClaimPacket(double initial, PeriodScope periodScope, DateTime occurrenceDate, ClaimType claimType, IPerilMarker peril,
                PatternPacket payoutPattern = null, PatternPacket reportingPattern = null) {
        this.initial = initial
        this.periodScope = periodScope
        this.occurrenceDate = occurrenceDate
        this.claimType = claimType
        this.peril = peril
        this.payoutPattern = payoutPattern
        this.reportingPattern = reportingPattern
    }

    /**
     *  Adds a new IClaim object to claimsGross of component peril using the implementation matching the arguments
     *  provided in the c'tor. This function needs to be queried by any member function before accessing the claimsGross.
     */
    private void initPeriod() {
        if (periodScope.currentPeriod > lastInitializedPeriod) {
            lastInitializedPeriod = periodScope.currentPeriod
            if (reportingPattern == null && payoutPattern == null && severityIndex == null && runOffIndex == null) {
                if (periodScope.periodCounter.belongsToCurrentPeriod(occurrenceDate)) {
                    add(new UltimateClaimModelling(peril, initial, occurrenceDate), SignTag.GROSS)
                }
            }
            else if (reportingPattern == null && payoutPattern != null && severityIndex == null && runOffIndex == null) {
                for (DateFactors dateFactors : payoutPattern.getDateFactorsOfCurrentPeriod(occurrenceDate, periodScope.periodCounter)) {
                    add(new PaidClaimModelling(peril, initial, dateFactors.date, dateFactors.factor * initial), SignTag.GROSS)
                }
            }
            else if (reportingPattern != null && payoutPattern != null && severityIndex == null && runOffIndex == null) {
                // todo(sku): extend possible use cases (different pattern lenghts, different dates)
                List<DateFactors> payoutDateFactors = payoutPattern.getDateFactorsOfCurrentPeriod(occurrenceDate, periodScope.periodCounter)
                List<DateFactors> reportingDateFactors = reportingPattern.getDateFactorsOfCurrentPeriod(occurrenceDate, periodScope.periodCounter)
                if (payoutDateFactors.size() != reportingDateFactors.size()) {
                    throw new NotImplementedException('implementation currently restricted to patterns of same length')
                }
                for (int i = 0; i < payoutDateFactors.size(); i++) {
                    DateFactors payout = payoutDateFactors[i]
                    DateFactors reporting = reportingDateFactors[i]
                    DateTime payoutDate = payout.date
                    DateTime reportedDate = reporting.date
                    if (!(payoutDate.equals(reportedDate))) {
                        throw new NotImplementedException('implementation currently restricted to updates on same date')
                    }
                    Double paid = payout.factor * initial
                    Double reported = reporting.factor * initial
                    add(new ReportedClaimModelling(peril, initial, payoutDate, paid, reported), SignTag.GROSS)
                }
            }
        }
    }

    /**
     * make sure lists are filled by sorting IClaim objects by update date
     * @param claim
     * @param signTag
     */
    void add(IClaim claim, SignTag signTag) {
        switch (signTag) {
            case SignTag.GROSS:
                claimsGross.put(claim.origin(), claim)
                break;
            case SignTag.CEDED:
                claimsCeded.put(claim.origin(), claim)
                break;
            case SignTag.NET:
                claimsNet.put(claim.origin(), claim)
                break;
            default:
                throw new NotImplementedException("unknown signTag: $signTag")
        }
    }

    // IDEA: get rid of periodScope and apply patterns according to an additionally provided date
    double value(IComponentMarker component, ClaimProperty claimProperty, SignTag signTag) {
        initPeriod()
        switch (signTag) {
            case SignTag.GROSS:
                return cumulatedValue(claimsGross.get(component), claimProperty)
            case SignTag.CEDED:
                return cumulatedValue(claimsCeded.get(component), claimProperty)
            case SignTag.NET:
                return cumulatedValue(claimsNet.get(component), claimProperty)
            default:
                throw new NotImplementedException("unknown mode: $signTag")
        }
    }

    double value(IComponentMarker component, ClaimProperty claimProperty, SignTag signTag, PeriodScope periodScope) {
        initPeriod()
        return value(component, signTag, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate, claimProperty)
    }

    double value(IComponentMarker component, SignTag signTag, DateTime fromIncluding, DateTime toExcluded, ClaimProperty claimProperty) {
        initPeriod()
        switch (signTag) {
            case SignTag.GROSS:
                return incrementValue(claimsGross.get(component), claimProperty, fromIncluding, toExcluded)
            case SignTag.CEDED:
                return incrementValue(claimsCeded.get(component), claimProperty, fromIncluding, toExcluded)
            case SignTag.NET:
                return incrementValue(claimsNet.get(component), claimProperty, fromIncluding, toExcluded)
            default:
                throw new NotImplementedException("unknown mode: $signTag")
        }
    }

    /**
     * @param claims need to be sorted by updateDate
     * @param fromIncluding needs to be before the toExcluded date
     * @param toExcluded
     * @return
     */
    private double incrementValue(SortedSet<IClaim> claims, ClaimProperty claimProperty, DateTime fromIncluding, DateTime toExcluded) {
        Double fromValue = null
        Double toValue = null
        for (IClaim claim : claims.toList().reverse()) {
            if (toValue && !fromValue && claim.updateDate().isBefore(fromIncluding)) {
                fromValue = claimProperty.value(claim)
            }
            if (toValue == null && claim.updateDate().isBefore(toExcluded)) {
                toValue = claimProperty.value(claim)
            }
        }
        if (DateTimeUtilities.isBetweenOrEqualStart(fromIncluding, toExcluded, occurrenceDate)) {
            return toValue == null ? fromValue : toValue
        }
        if (toValue && fromValue) {
            return toValue - fromValue
        }
        else if (fromValue) {
            return fromValue
        }
        return 0
    }

    /**
     *
     * @param claims need to be sorted by updateDate
     * @param claimProperty
     * @return
     */
    private static double cumulatedValue(SortedSet<IClaim> claims, ClaimProperty claimProperty) {
        if (!claims || claims.toList().empty) return 0
        return claimProperty.value(claims.toList()[-1])
    }


    ImmutableList<ISegmentMarker> segment() {
        throw new NotImplementedException()
    }

    IPerilMarker peril() {
        peril
    }

    ImmutableList<IReinsuranceContractMarker> contracts() {
        throw new NotImplementedException()
    }

    ImmutableList<IComponentMarker> marker(IComponentMarker marker) {
        throw new NotImplementedException()
    }
}
