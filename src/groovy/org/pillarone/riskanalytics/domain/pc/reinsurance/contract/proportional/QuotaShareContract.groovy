package org.pillarone.riskanalytics.domain.pc.reinsurance.contract.proportional

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlow
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlowType
import org.pillarone.riskanalytics.domain.pc.accounting.ICashflow
import org.pillarone.riskanalytics.domain.pc.claim.ClaimPacket
import org.pillarone.riskanalytics.domain.pc.reinsurance.contract.IReinsuranceContract
import org.pillarone.riskanalytics.domain.pc.util.SignTag
import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class QuotaShareContract implements IReinsuranceContract {

    protected double quotaShare = 0;

    private IReinsuranceContractMarker contract;

    public QuotaShareContract(IReinsuranceContractMarker contract, double quotaShare) {
        this.quotaShare = quotaShare;
        this.contract = contract;
    }


    @Override
    Collection<ICashflow> cededCashflows(Collection<ICashflow> grossCashflows) {
        List<ICashflow> cededCashflows = []
        for (ICashflow cashflow : grossCashflows) {
            cededCashflows << new CashFlow(cashflow, contract, cashflow.amount() * quotaShare)
        }
        cededCashflows
    }


    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer()
        buffer.append(quotaShare)
        return buffer.toString()
    }
}
