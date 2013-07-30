package org.pillarone.riskanalytics.domain.pc.claim

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.Period
import org.junit.Test
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.util.Mode
import org.pillarone.riskanalytics.domain.pc.util.SignTag
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
// todo(sku): reuse corresponding spreadsheet test of pc-cashflow plugin
class ClaimPacketTests extends GroovyTestCase {

    DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)
    PeriodScope periodScope = new PeriodScope(periodCounter: new ContinuousPeriodCounter(date20130101, Period.years(1)))
    TestComponent peril = new TestComponent()

    @Test
    void usageUltimate() {
        ClaimPacket claim = new ClaimPacket(1000, periodScope, date20130101, ClaimType.ATTRITIONAL, peril)
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 1000 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)

        periodScope.prepareNextPeriod()
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)

        periodScope.prepareNextPeriod()
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)
    }

    @Test
    void usagePaid() {
        PatternPacket payoutPattern = new PatternPacket([0.8d, 0.95d, 1d], [Period.months(2), Period.months(12), Period.months(30)])
        ClaimPacket claim = new ClaimPacket(1000, periodScope, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern)
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 800 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 800 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)

        periodScope.prepareNextPeriod()
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 950 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 150 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)

        periodScope.prepareNextPeriod()
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 50 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)
    }

    @Test
    void usageReported() {
        PatternPacket reportingPattern = new PatternPacket([0.9d, 1d, 1d], [Period.months(2), Period.months(12), Period.months(30)])
        PatternPacket payoutPattern = new PatternPacket([0.8d, 0.95d, 1d], [Period.months(2), Period.months(12), Period.months(30)])
        ClaimPacket claim = new ClaimPacket(1000, periodScope, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern, reportingPattern)
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 800 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 800 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 900 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 900 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)

        periodScope.prepareNextPeriod()
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 950 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 150 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 100 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)

        periodScope.prepareNextPeriod()
        assert 1000 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.TOTAL, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS)
        assert 50 == claim.value(peril, ClaimProperty.PAID, SignTag.GROSS, periodScope)
        assert 1000 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS)
        assert 0 == claim.value(peril, ClaimProperty.REPORTED, SignTag.GROSS, periodScope)
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
