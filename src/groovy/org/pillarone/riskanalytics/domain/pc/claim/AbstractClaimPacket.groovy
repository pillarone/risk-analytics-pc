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
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlowType
import org.pillarone.riskanalytics.domain.pc.accounting.ICashflow
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
abstract class AbstractClaimPacket extends Packet implements IClaimPacket {

    protected final IClaimRoot root

    AbstractClaimPacket(double initial, DateTime occurrenceDate, ClaimType claimType, IPerilMarker peril,
                PatternPacket payoutPattern = null, PatternPacket reportingPattern = null) {
        root = new ClaimRoot(initial, occurrenceDate, occurrenceDate, claimType, peril, null, reportingPattern, payoutPattern)
    }

    @Override
    IClaim claimCumulated(IComponentMarker component, SignTag signTag, DateTime evaluationDate) {
        double ultimate = valueCumulatedAt(component, CashFlowType.CLAIM_TOTAL, signTag, evaluationDate)
        if (!(root.hasPayouts()) && !(root.hasIBNR())) {
            return new UltimateClaimModelling(component, ultimate, evaluationDate)
        }
        else if (root.hasPayouts()) {
            double paid = valueCumulatedAt(component, CashFlowType.CLAIM_PAID, signTag, evaluationDate)
            if (root.hasIBNR()) {
                double reported = valueCumulatedAt(component, CashFlowType.CLAIM_REPORTED, signTag, evaluationDate)
                return new ReportedClaimModelling(component, ultimate, evaluationDate, paid, reported)
            }
            else {
                return new PaidClaimModelling(component, ultimate, evaluationDate, paid)
            }
        }
    }

    List<IClaim> claimsCumulated(IComponentMarker component, SignTag signTag, DateTime fromIncluding, DateTime toExcluded) {
        throw new NotImplementedException()
    }

    List<IClaim> claimsIncremental(IComponentMarker component, SignTag signTag, DateTime fromIncluding, DateTime toExcluded) {
        throw new NotImplementedException()
    }

    ImmutableList<ISegmentMarker> segment() {
        throw new NotImplementedException()
    }

    IPerilMarker peril() {
        root.peril()
    }

    ImmutableList<IReinsuranceContractMarker> contracts() {
        throw new NotImplementedException()
    }

    ImmutableList<IComponentMarker> marker(IComponentMarker marker) {
        throw new NotImplementedException()
    }
}
