package org.pillarone.riskanalytics.domain.pc.claim

import groovy.transform.CompileStatic
import org.apache.commons.lang.NotImplementedException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlow
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlowContainer
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlowType
import org.pillarone.riskanalytics.domain.pc.accounting.ICashflow
import org.pillarone.riskanalytics.domain.pc.pattern.DateFactors
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.util.SignTag
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
// todo: think merging within R/I programs
@CompileStatic
class ClaimPacket extends AbstractClaimPacket implements IClaimPacket {

    private static Log LOG = LogFactory.getLog(ClaimPacket);

    private Map<IComponentMarker, CashFlowContainer> claimsGross = [:]
    private Map<IComponentMarker, CashFlowContainer> claimsCeded = [:]

    ClaimPacket() {
    }

    ClaimPacket(double initial, DateTime occurrenceDate, ClaimType claimType, IPerilMarker peril,
                PatternPacket payoutPattern = null, PatternPacket reportingPattern = null) {
        super(initial, occurrenceDate, claimType, peril, payoutPattern, reportingPattern)
//        updateInternalStructure(occurrenceDate);
        // todo: performance improvement idea: if patterns can't be modified, call updateInternalStructure in order to fully develop the claim right at the beginning
    }

    /**
     * make sure lists are filled by sorting IClaim objects by update date
     * @param claim
     * @param signTag
     */
    void add(ICashflow claim, SignTag signTag) {
        switch (signTag) {
            case SignTag.GROSS:
                CashFlowContainer container = claimsGross.get(claim.origin())
                if (!container) {
                    container = new CashFlowContainer()
                    claimsGross.put(claim.origin(), container)
                }
                container.add(claim)
                break;
            case SignTag.CEDED:
                CashFlowContainer container = claimsCeded.get(claim.origin())
                if (!container) {
                    container = new CashFlowContainer()
                    claimsCeded.put(claim.origin(), container)
                }
                container.add(claim)
                break;
            case SignTag.NET:
                LOG.debug("adding net is not supported as these figures are calculated based on persisted gross and ceded")
                break;
            default:
                throw new NotImplementedException("unknown signTag: $signTag")
        }
    }

    void addAll(Collection<ICashflow> claims, SignTag signTag) {
        for (ICashflow claim: claims) {
            add(claim, signTag)
        }
    }

    @Override
    Double valueCumulatedAt(IComponentMarker component, CashFlowType claimProperty, SignTag signTag, DateTime evaluationDate) {
        updateInternalStructure(evaluationDate)
        return cashFlowCumulatedAt(component, claimProperty, signTag, evaluationDate).amount()
    }

    @Override
    ICashflow cashFlowCumulatedAt(IComponentMarker component, CashFlowType claimProperty, SignTag signTag, DateTime evaluationDate) {
        updateInternalStructure(evaluationDate)
        switch (signTag) {
            case SignTag.GROSS:
                return claimsGross[component].valueCumulatedAt(claimProperty, evaluationDate)
            case SignTag.CEDED:
                return claimsCeded[component].valueCumulatedAt(claimProperty, evaluationDate)
            case SignTag.NET:
                ICashflow gross = claimsGross[component].valueCumulatedAt(claimProperty, evaluationDate)
                ICashflow ceded = claimsCeded[component].valueCumulatedAt(claimProperty, evaluationDate)
                return new CashFlow(gross.date(), gross.origin(), gross.cashFlowType(), gross.amount() - ceded.amount())
            default:
                throw new NotImplementedException("unknown mode: $signTag")
        }
    }

    private DateTime lastInternalUpdate = new DateTime(1900,1,1,0,0,0,0)
    /**
     * Fills claimsGross and claimsCeded according to patterns to make sure the internal structures can provide the correct
     * information at dateTime. This function needs to be call first and always when any cash-flow information of this
     * object is queried.
     * @param dateTime
     */
    private void updateInternalStructure(DateTime dateTime) {
        if (dateTime.isAfter(lastInternalUpdate)) {
            if (claimsGross.isEmpty()) {
                add(new CashFlow(root.occurrenceDate(), root.peril(), CashFlowType.CLAIM_TOTAL, root.initial()), SignTag.GROSS)
                if (!(root.hasIBNR())) {
                    add(new CashFlow(root.occurrenceDate(), root.peril(), CashFlowType.CLAIM_REPORTED, root.initial()), SignTag.GROSS)
                }
                if (!(root.hasPayouts())) {
                    add(new CashFlow(root.occurrenceDate(), root.peril(), CashFlowType.CLAIM_PAID, root.initial()), SignTag.GROSS)
                }
            }
            if (root.hasPayouts()) {
                for (DateFactors dateFactors : root.payoutPattern().getDateFactorsOfCurrentPeriod(root.occurrenceDate(), lastInternalUpdate, dateTime)) {
                    add(new CashFlow(dateFactors.date, root.peril(), CashFlowType.CLAIM_PAID, dateFactors.factor * root.initial()), SignTag.GROSS)
                }
            }
            if (root.hasIBNR()) {
                for (DateFactors dateFactors : root.reportingPattern().getDateFactorsOfCurrentPeriod(root.occurrenceDate(), lastInternalUpdate, dateTime)) {
                    add(new CashFlow(dateFactors.date, root.peril(), CashFlowType.CLAIM_REPORTED, dateFactors.factor * root.initial()), SignTag.GROSS)
                }
            }
        }
        lastInternalUpdate = dateTime
    }

    /**
     * Check for matching cashFlowType
     * @param cashFlowType
     * @return false if cashFlowType does not match available patterns
     */
    public boolean nonTrivial(CashFlowType cashFlowType) {
        if (cashFlowType.equals(CashFlowType.CLAIM_TOTAL)) {
            return true
        }
        else if (cashFlowType.equals(CashFlowType.CLAIM_PAID) && root.hasPayouts()) {
            return true
        }
        else if (cashFlowType.equals(CashFlowType.CLAIM_REPORTED) && root.hasIBNR()) {
            return true
        }
        return false
    }

    @Override
    List<ICashflow> cashFlowsCumulated(IComponentMarker component, CashFlowType claimProperty, SignTag signTag,
                                       DateTime fromIncluding, DateTime toExcluded) {
        updateInternalStructure(toExcluded)
        List<ICashflow> cashflows = []
        switch (signTag) {
            case SignTag.GROSS:
                cashflows.addAll claimsGross[component].valuesCumulated(claimProperty, fromIncluding, toExcluded)
                break
            case SignTag.CEDED:
                cashflows.addAll claimsCeded[component].valuesCumulated(claimProperty, fromIncluding, toExcluded)
                break
            case SignTag.NET:
                List<ICashflow> gross = claimsGross[component].valuesCumulated(claimProperty, fromIncluding, toExcluded)
                List<ICashflow> ceded = claimsCeded[component].valuesCumulated(claimProperty, fromIncluding, toExcluded)
                throw new NotImplementedException("not yet implemented for net case")
            default:
                throw new NotImplementedException("unknown mode: $signTag")
        }
        return cashflows
    }

    @Override
    List<ICashflow> cashFlowsIncremental(IComponentMarker component, CashFlowType claimProperty, SignTag signTag,
                                         DateTime fromIncluding, DateTime toExcluded) {
        updateInternalStructure(toExcluded)
        switch (signTag) {
            case SignTag.GROSS:
                return claimsGross[component].valuesIncremental(claimProperty, fromIncluding, toExcluded)
            case SignTag.CEDED:
                return claimsCeded[component].valuesIncremental(claimProperty, fromIncluding, toExcluded)
            case SignTag.NET:
                List<ICashflow> gross = claimsGross[component].valuesIncremental(claimProperty, fromIncluding, toExcluded)
                List<ICashflow> ceded = claimsCeded[component].valuesIncremental(claimProperty, fromIncluding, toExcluded)
                throw new NotImplementedException("not yet implemented for net case")
            default:
                throw new NotImplementedException("unknown mode: $signTag")
        }
    }

}
