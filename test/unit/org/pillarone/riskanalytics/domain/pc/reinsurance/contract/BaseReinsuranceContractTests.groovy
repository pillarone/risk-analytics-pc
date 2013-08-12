package org.pillarone.riskanalytics.domain.pc.reinsurance.contract

import org.joda.time.DateTime
import org.joda.time.Period
import org.junit.Test
import org.pillarone.riskanalytics.core.simulation.ContinuousPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.claim.*
import org.pillarone.riskanalytics.domain.pc.pattern.PatternPacket
import org.pillarone.riskanalytics.domain.pc.util.SignTag

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class BaseReinsuranceContractTests extends GroovyTestCase {

    DateTime date20130101 = new DateTime(2013,1,1,0,0,0,0)
    PeriodScope periodScope = new PeriodScope(periodCounter: new ContinuousPeriodCounter(date20130101, Period.years(1)))
    TestComponent peril = new TestComponent()
    BaseReinsuranceContract contract

    @Override
    protected void setUp() throws Exception {
        contract = new BaseReinsuranceContract(periodScope: periodScope, coveredComponent: peril,
            parmContractStrategy: ReinsuranceContractType.getStrategy(ReinsuranceContractType.QUOTASHARE, [quotaShare: 0.2d]))
    }

    @Test
    void usageUltimate() {
        ClaimPacket claim1000 = new ClaimPacket(1000, date20130101, ClaimType.ATTRITIONAL, peril)
        ClaimPacket claim800 = new ClaimPacket(800, date20130101, ClaimType.ATTRITIONAL, peril)
        contract.inClaims = [claim1000, claim800]
        contract.doCalculation()

        ArrayList<IClaim> cededClaims = contract.outClaims*.claimCumulated(contract, SignTag.CEDED, periodScope.nextPeriodStartDate)
        println cededClaims
        assert [UltimateClaimModelling.class] * 2, cededClaims*.class
        assert cededClaims*.total().containsAll([200d, 160d])
    }

    @Test
    void usagePaid() {
        PatternPacket payoutPattern = new PatternPacket([0.8d, 0.95d, 1d], [Period.months(2), Period.months(12), Period.months(30)])
        ClaimPacket claim1000 = new ClaimPacket(1000, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern)
        ClaimPacket claim800 = new ClaimPacket(800, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern)
        contract.inClaims = [claim1000, claim800]
        contract.doCalculation()

        ArrayList<IClaim> cededClaims = contract.outClaims*.claimCumulated(contract, SignTag.CEDED, periodScope.nextPeriodStartDate)
        println cededClaims
        assert [PaidClaimModelling.class] * 2 == cededClaims*.class
        assert cededClaims*.total().containsAll([200d, 160d])
        assert cededClaims*.paid().containsAll([160d, 128d])

        periodScope.prepareNextPeriod()
        contract.reset()
        contract.doCalculation()
        cededClaims = contract.outClaims*.claimCumulated(contract, SignTag.CEDED, periodScope.nextPeriodStartDate)
        println cededClaims
        assert [PaidClaimModelling.class] * 2 == cededClaims*.class
        assert cededClaims*.total().containsAll([200d, 160d])
        assert cededClaims*.paid().containsAll([190d, 152d])

        periodScope.prepareNextPeriod()
        contract.reset()
        contract.doCalculation()
        cededClaims = contract.outClaims*.claimCumulated(contract, SignTag.CEDED, periodScope.nextPeriodStartDate)
        println cededClaims
        assert [PaidClaimModelling.class] * 2 == cededClaims*.class
        assert cededClaims*.total().containsAll([200d, 160d])
        assert cededClaims*.paid().containsAll([200d, 160d])
    }

    @Test
    void usageReported() {
        PatternPacket reportingPattern = new PatternPacket([0.9d, 1d, 1d], [Period.months(0), Period.months(12), Period.months(30)])
        PatternPacket payoutPattern = new PatternPacket([0.8d, 0.95d, 1d], [Period.months(2), Period.months(12), Period.months(30)])
        ClaimPacket claim1000 = new ClaimPacket(1000, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern, reportingPattern)
        ClaimPacket claim800 = new ClaimPacket(800, date20130101, ClaimType.ATTRITIONAL, peril, payoutPattern, reportingPattern)
        contract.inClaims = [claim1000, claim800]
        contract.doCalculation()

        ArrayList<IClaim> cededClaims = contract.outClaims*.claimCumulated(contract, SignTag.CEDED, periodScope.nextPeriodStartDate)
        println cededClaims
        assert [ReportedClaimModelling.class] * 2 == cededClaims*.class
        assert cededClaims*.total().containsAll([200d, 160d])
        assert cededClaims*.reported().containsAll([180d, 144d])
        assert cededClaims*.paid().containsAll([160d, 128d])

        periodScope.prepareNextPeriod()
        contract.reset()
        contract.doCalculation()
        cededClaims = contract.outClaims*.claimCumulated(contract, SignTag.CEDED, periodScope.nextPeriodStartDate)
        println cededClaims
        assert [ReportedClaimModelling.class] * 2 == cededClaims*.class
        assert cededClaims*.total().containsAll([200d, 160d])
        assert cededClaims*.reported().containsAll([200d, 160d])
        assert cededClaims*.paid().containsAll([190d, 152d])

        periodScope.prepareNextPeriod()
        contract.reset()
        contract.doCalculation()
        cededClaims = contract.outClaims*.claimCumulated(contract, SignTag.CEDED, periodScope.nextPeriodStartDate)
        println cededClaims
        assert [ReportedClaimModelling.class] * 2 == cededClaims*.class
        assert cededClaims*.total().containsAll([200d, 160d])
        assert cededClaims*.reported().containsAll([200d, 160d])
        assert cededClaims*.paid().containsAll([200d, 160d])
    }
}
