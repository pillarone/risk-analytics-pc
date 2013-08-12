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

    private double quotaShare
//    private ILossParticipationStrategy lossParticipation
//    private ILimitStrategy limit
//    private ICommissionStrategy commission

    /**not a parameter but updated during calculateCommission() to avoid side effect for the parameter variable
     * required as we need to share the loss carried forward among different instances */
//    private DoubleValuePerPeriod lossCarriedForward

    public ReinsuranceContractType getType() {
        return ReinsuranceContractType.QUOTASHARE
    }

    public Map getParameters() {
        [
            QUOTASHARE: quotaShare,
//            LOSSPARTICIPATION: lossParticipation,
//            LIMIT: limit,
//            COMMISSION: commission
        ]
    }

    @Override
    List<IReinsuranceContract> contracts(IReinsuranceContractMarker contract) {
        return [new QuotaShareContract(contract, quotaShare)]
    }


    public double termDeductible() { 0d }
    public double termLimit() { 0d }

    public static final String QUOTASHARE = "quotaShare"
    public static final String LOSSPARTICIPATION = "lossParticipation"
    public static final String LIMIT = "limit"
    public static final String COMMISSION = "commission"


}
