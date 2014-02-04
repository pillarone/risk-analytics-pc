package org.pillarone.riskanalytics.domain.pc.claim.generator

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.PeriodStore
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.packets.SingleValuePacket
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.claim.ClaimPacket
import org.pillarone.riskanalytics.domain.pc.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.claim.ReportedClaimModelling
import org.pillarone.riskanalytics.domain.pc.claim.UltimateClaimModelling
import org.pillarone.riskanalytics.domain.pc.indexing.*
import org.pillarone.riskanalytics.domain.pc.pattern.IPayoutPatternMarker
import org.pillarone.riskanalytics.domain.pc.pattern.IReportingPatternMarker
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.pattern.PatternUtils
import org.pillarone.riskanalytics.domain.pc.util.DateDouble
import org.pillarone.riskanalytics.domain.pc.util.SignTag
import org.pillarone.riskanalytics.domain.utils.marker.ICorrelationMarker
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ClaimsGenerator extends Component implements IPerilMarker, ICorrelationMarker {

    PeriodScope periodScope
    PeriodStore periodStore
    boolean globalRunOffAfterFirstPeriod  = true

    PacketList<FactorsPacket> inFactors = new PacketList<FactorsPacket>(FactorsPacket)
    PacketList<PatternPacket> inPatterns = new PacketList<PatternPacket>(PatternPacket)

    PacketList<ClaimPacket> outClaims = new PacketList<ClaimPacket>(ClaimPacket)
    PacketList<SingleValuePacket> outClaimNumberResults = new PacketList<SingleValuePacket>(SingleValuePacket)
    PacketList<UltimateClaimModelling> outClaimResults = new PacketList<UltimateClaimModelling>(UltimateClaimModelling)

    ConstrainedString parmPayoutPattern = new ConstrainedString(IPayoutPatternMarker, "")
    ConstrainedString parmReportingPattern = new ConstrainedString(IReportingPatternMarker, "")
    ConstrainedMultiDimensionalParameter parmSeverityIndices = new ConstrainedMultiDimensionalParameter(
        Collections.emptyList(), SeverityIndexSelectionTableConstraints.COLUMN_TITLES,
        ConstraintsFactory.getConstraints(SeverityIndexSelectionTableConstraints.IDENTIFIER))
    ConstrainedMultiDimensionalParameter parmRunOffIndices = new ConstrainedMultiDimensionalParameter(
        Collections.emptyList(), RunOffIndexSelectionTableConstraints.COLUMN_TITLES,
        ConstraintsFactory.getConstraints(RunOffIndexSelectionTableConstraints.IDENTIFIER))
    IClaimsGeneratorStrategy parmClaimsModel = ClaimsGeneratorType.getDefault()

    private Set<ClaimPacket> claims = new HashSet<ClaimPacket>()

    @Override
    protected void doCalculation() {
//        List<Factors> runOffFactors = IndexUtils.filterFactors(inFactors, parmRunOffIndices)
        List<Factors> severityFactors = IndexUtils.filterFactors(inFactors, parmSeverityIndices)
        List<ClaimPacket> newClaims = generateClaimsOfCurrentPeriod(severityFactors)
        claims.addAll(newClaims)
        outClaims.addAll(newClaims)
        outClaimNumberResults << new SingleValuePacket(newClaims.size())
        claims.each { ClaimPacket claim ->
            outClaimResults << claim.claimCumulated(this, SignTag.GROSS, periodScope.nextPeriodStartDate.minusDays(1))
        }
    }

    private List<ClaimPacket> generateClaimsOfCurrentPeriod(List<Factors> severityFactors) {
        if (generateNewClaims()) {
            List<ClaimPacket> newClaims = []
            Collection<DateDouble> initialsAndOccurrenceDates = parmClaimsModel.claims(periodScope, severityFactors)
            for (DateDouble initialAndOccurrenceDate : initialsAndOccurrenceDates) {
                double initial = initialAndOccurrenceDate.value
                DateTime occurrenceDate = initialAndOccurrenceDate.date
                PatternPacket payoutPattern = PatternUtils.filterPattern(inPatterns, parmPayoutPattern, IPayoutPatternMarker, false)
                PatternPacket reportingPattern = PatternUtils.filterPattern(inPatterns, parmReportingPattern, IReportingPatternMarker, false)
                newClaims << new ClaimPacket(initial, occurrenceDate, parmClaimsModel.claimType() as ClaimType, this, payoutPattern, reportingPattern)
            }
            return newClaims
        }
        return Collections.emptyList()
    }

    private boolean generateNewClaims() {
        globalRunOffAfterFirstPeriod && periodScope.isFirstPeriod() || !globalRunOffAfterFirstPeriod
    }
}
