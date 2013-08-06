package org.pillarone.riskanalytics.domain.pc.accounting

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class CashFlow implements ICashflow, Comparable {

    private final DateTime updateDate
    private final IComponentMarker origin
    private final CashFlowType account
    private final double amount

    CashFlow(DateTime updateDate, IComponentMarker origin, CashFlowType account, double amount) {
        this.updateDate = updateDate
        this.origin = origin
        this.account = account
        this.amount = amount
    }

    @Override
    IComponentMarker origin() {
        origin
    }

    @Override
    CashFlowType cashFlowType() {
        account
    }

    @Override
    DateTime date() {
        updateDate
    }

    @Override
    double amount() {
        amount
    }

    @Override
    int compareTo(Object o) {
        return updateDate.compareTo(((CashFlow) o).date())
    }


    @Override
    public java.lang.String toString() {
        return "CashFlow{" +
            "updateDate=" + updateDate +
            ", origin=" + origin +
            ", account=" + account +
            ", amount=" + amount +
            '}';
    }
}
