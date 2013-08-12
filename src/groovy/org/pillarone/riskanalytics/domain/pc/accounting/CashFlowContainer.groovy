package org.pillarone.riskanalytics.domain.pc.accounting

import com.google.common.collect.SortedSetMultimap
import com.google.common.collect.TreeMultimap
import groovy.transform.CompileStatic
import org.joda.time.DateTime

/**
 * Keeps cashflow sorted by date
 *
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class CashFlowContainer {

    private SortedSetMultimap<CashFlowType, ICashflow> cashflows = TreeMultimap.create()

    void add(ICashflow claim) {
        cashflows.put(claim.cashFlowType(), claim)
    }

    ICashflow valueCumulatedAt(CashFlowType claimProperty, DateTime evaluationDate) {
        for (ICashflow cashflow : cashflows.get(claimProperty).toList().reverse()) {
            if (!(cashflow.date().isAfter(evaluationDate))) {
                return cashflow
            }
        }
        return null
    }

    List<ICashflow> valuesCumulated(CashFlowType claimProperty, DateTime fromIncluding, DateTime toExcluded) {
        List<ICashflow> results = []
        for (ICashflow cashflow : cashflows.get(claimProperty).toList().reverse()) {
            if (!(cashflow.date().isBefore(fromIncluding)) && cashflow.date().isBefore(toExcluded)) {
                results << cashflow
            }
        }
        return results.reverse()
    }

    List<ICashflow> valuesIncremental(CashFlowType claimProperty, DateTime fromIncluding, DateTime toExcluded) {
        List<ICashflow> results = []
        List<ICashflow> increments = []
        boolean anchorFound = false
        for (ICashflow cashflow : cashflows.get(claimProperty).toList().reverse()) {
            if (!(cashflow.date().isBefore(fromIncluding)) && cashflow.date().isBefore(toExcluded)) {
                results << cashflow
            }
            else if (!anchorFound && cashflow.date().isBefore(fromIncluding) && !(results.empty)) {
                anchorFound = true
                results << cashflow
            }
        }
        if (results.size() > 1) {
            if (!anchorFound) {
                ICashflow anchor = results[results.size() - 1]
                increments << anchor
            }
            for (int i = results.size() - 1; i > 0; i--) {
                ICashflow previous = results[i]
                ICashflow current = results[i-1]
                increments << new CashFlow(current.date(), current.origin(), current.cashFlowType(), current.amount() - previous.amount())
            }
            return increments
        }
        else if (results.size() == 1) {
            return results
        }
        return []
    }
}
