package org.pillarone.riskanalytics.domain.pc.reinsurance.contract.proportional

import groovy.transform.CompileStatic
import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.domain.pc.claim.ClaimPacket
import org.pillarone.riskanalytics.domain.pc.reinsurance.contract.IReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.reinsurance.contract.IReinsuranceContractStrategy
import org.pillarone.riskanalytics.domain.pc.reinsurance.contract.ReinsuranceContractType
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class QuotaShareContractStrategy extends AbstractParameterObject implements IReinsuranceContractStrategy {

    private Double quotaShare
//    private ILossParticipationStrategy lossParticipation
//    private ILimitStrategy limit
//    private ICommissionStrategy commission

    /**not a parameter but updated during calculateCommission() to avoid side effect for the parameter variable
     * required as we need to share the loss carried forward among different instances */
//    private DoubleValuePerPeriod lossCarriedForward

    ReinsuranceContractType getType() {
        ReinsuranceContractType.QUOTASHARE
    }

    Map getParameters() {
        [
            (QUOTASHARE.toString()): quotaShare,
//            LOSSPARTICIPATION: lossParticipation,
//            LIMIT: limit,
//            COMMISSION: commission
        ]
    }

    @Override
    List<IReinsuranceContract> contracts(IReinsuranceContractMarker contract) {
        return [new QuotaShareContract(contract, quotaShare)]
    }


    double termDeductible() { 0d }
    double termLimit() { 0d }

    static final String QUOTASHARE = "quotaShare"
    static final String LOSSPARTICIPATION = "lossParticipation"
    static final String LIMIT = "limit"
    static final String COMMISSION = "commission"


}
