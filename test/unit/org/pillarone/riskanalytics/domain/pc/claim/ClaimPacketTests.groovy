package org.pillarone.riskanalytics.domain.pc.claim

import org.joda.time.DateTime
import org.joda.time.Period
import org.junit.Test
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlowType
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.util.SignTag
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): reuse corresponding spreadsheet test of pc-cashflow plugin
//@CompileStatic
class ClaimPacketTests extends GroovyTestCase {

    DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)
    PeriodScope periodScope = new PeriodScope(periodCounter: new ContinuousPeriodCounter(date20130101, Period.years(1)))
    TestComponent peril = new TestComponent()

    @Test
    void usageUltimate() {
        ClaimPacket claim = new ClaimPacket(1000, date20130101, ClaimType.ATTRITIONAL, peril)
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [1000d] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert [1000d] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert [1000d] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        IClaim cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 1000d == cumulatedClaim.paid()

        periodScope.prepareNextPeriod()
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 1000d == cumulatedClaim.paid()

        periodScope.prepareNextPeriod()
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 1000d == cumulatedClaim.paid()
    }

    @Test
    void usagePaid() {
        PatternPacket payoutPattern = new PatternPacket([0.8d, 0.95d, 1d], [Period.months(2), Period.months(12), Period.months(30)])
        ClaimPacket claim = new ClaimPacket(1000, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern)
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [1000] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 800 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [0, 800] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [1000] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        IClaim cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 800d == cumulatedClaim.paid()

        periodScope.prepareNextPeriod()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 950 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [150] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 950d == cumulatedClaim.paid()

        periodScope.prepareNextPeriod()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [50] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 1000d == cumulatedClaim.paid()
    }

    @Test
    void usageReported() {
        PatternPacket reportingPattern = new PatternPacket([0.9d, 1d, 1d], [Period.months(0), Period.months(12), Period.months(30)])
        PatternPacket payoutPattern = new PatternPacket([0.8d, 0.95d, 1d], [Period.months(2), Period.months(12), Period.months(30)])
        ClaimPacket claim = new ClaimPacket(1000, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern, reportingPattern)
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [1000] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 800 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [0, 800] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 900 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [900] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        IClaim cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 900d == cumulatedClaim.reported()
        assert 800d == cumulatedClaim.paid()

        periodScope.prepareNextPeriod()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 950 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [150] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [100] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 950d == cumulatedClaim.paid()

        periodScope.prepareNextPeriod()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_TOTAL, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [50] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_PAID, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        assert 1000 == claim.valueCumulatedAt(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert [0d] == claim.cashFlowsIncremental(peril, CashFlowType.CLAIM_REPORTED, SignTag.GROSS, periodScope.currentPeriodStartDate, periodScope.nextPeriodStartDate)*.amount()
        cumulatedClaim = claim.claimCumulated(peril, SignTag.GROSS, periodScope.nextPeriodStartDate)
        assert 1000d == cumulatedClaim.total()
        assert 1000d == cumulatedClaim.reported()
        assert 1000d == cumulatedClaim.paid()
    }

}

class TestComponent extends Component implements IPerilMarker, Comparable {

    @Override
    protected void doCalculation() {
    }

    @Override
    int compareTo(Object o) {
        return name.compareTo(((TestComponent) o).name)
    }
}
