package org.pillarone.riskanalytics.domain.pc.reinsurance.contract

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.packets.PacketList
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlowType
import org.pillarone.riskanalytics.domain.pc.accounting.ICashflow
import org.pillarone.riskanalytics.domain.pc.claim.ClaimPacket
import org.pillarone.riskanalytics.domain.pc.util.SignTag
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
//@CompileStatic
class BaseReinsuranceContractComponent extends Component implements IReinsuranceContractMarker {

    private static Log LOG = LogFactory.getLog(BaseReinsuranceContractComponent)

    PeriodScope periodScope

    PacketList<ClaimPacket> inClaims = new PacketList<ClaimPacket>(ClaimPacket)

    /** Contains gross claims covered in the current periods according to their time and cover filter. This includes
     *  gross claims for which there is no cover left or no cover available as the counter party has gone default. */
    PacketList<ClaimPacket> outClaims = new PacketList<ClaimPacket>(ClaimPacket)

    IReinsuranceContractStrategy parmContractStrategy = ReinsuranceContractType.getDefault()
    private IComponentMarker coveredComponent   // this needs to be filled according to parmCover

    /** This set is kept within one iteration and extended by every covered period as claims are sent once only through the model. */
    private Set<ClaimPacket> coveredClaims = new HashSet<ClaimPacket>()
    private List<IReinsuranceContract> contracts

    @Override
    protected void doCalculation() {
        updateCoveredClaims()
        contracts = parmContractStrategy.contracts(this)
        calculateCededClaims()
        fillOutChannels()
    }

    /**
     * This implementation assumes that total, paid and reported figures are calculated independently of each other
     */
    void calculateCededClaims() {
        for (IReinsuranceContract contract : contracts) {
            for (ClaimPacket claim : coveredClaims) {
                addCededCashFlows(claim, CashFlowType.CLAIM_TOTAL, contract)
                if (claim.nonTrivial(CashFlowType.CLAIM_PAID)) {
                    addCededCashFlows(claim, CashFlowType.CLAIM_PAID, contract)
                }
                if (claim.nonTrivial(CashFlowType.CLAIM_REPORTED)) {
                    addCededCashFlows(claim, CashFlowType.CLAIM_REPORTED, contract)
                }
            }
        }
    }

    private void addCededCashFlows(ClaimPacket claim, CashFlowType cashFlowType, IReinsuranceContract contract) {
        List<ICashflow> grossCashFlows = claim.cashFlowsCumulated(coveredComponent, cashFlowType, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)
        claim.addAll(contract.cededCashflows(grossCashFlows), SignTag.CEDED)
    }

    void updateCoveredClaims() {
        for (ClaimPacket claim : inClaims) {
            coveredClaims << claim
        }
    }

    void fillOutChannels() {
        outClaims.addAll(coveredClaims)
    }

    @Override
    boolean isProportionalContract() {
        return false
    }
}
