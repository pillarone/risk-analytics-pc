package models.claim

import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.model.StochasticModel
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.LimitedContinuousPeriodCounter
import org.pillarone.riskanalytics.domain.pc.claim.generator.ClaimsGenerator
import org.pillarone.riskanalytics.domain.pc.global.GlobalParameters
import org.pillarone.riskanalytics.domain.pc.pattern.PayoutPatternComponent
import org.pillarone.riskanalytics.domain.pc.pattern.ReportingPatternComponent
import org.pillarone.riskanalytics.domain.pc.reinsurance.contract.BaseReinsuranceContractComponent
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ClaimModel extends StochasticModel {

    GlobalParameters globalParameters
    PayoutPatternComponent payoutPattern
    ReportingPatternComponent reportingPattern
    ClaimsGenerator claimsGenerator
    BaseReinsuranceContractComponent quotaShare

    @Override
    void initComponents() {
        globalParameters = new GlobalParameters()
        payoutPattern = new PayoutPatternComponent()
        reportingPattern = new ReportingPatternComponent()
        claimsGenerator = new ClaimsGenerator()
        quotaShare = new BaseReinsuranceContractComponent()

        addStartComponent payoutPattern
        addStartComponent reportingPattern
    }

    @Override
    void wireComponents() {
        claimsGenerator.inPatterns = payoutPattern.outPattern
        claimsGenerator.inPatterns = reportingPattern.outPattern
        quotaShare.inClaims = claimsGenerator.outClaims
    }

    @Override
    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        Period developmentPeriod = Period.years(20)
        int numberOfYears = Math.max(1, Math.ceil(developmentPeriod.months / 12d) + 1)
        if (globalParameters.isRuntimeRunAtMostFivePeriods()) {
            numberOfYears = Math.min(numberOfYears, 5)
        }
        return new LimitedContinuousPeriodCounter(globalParameters.parmProjectionStartDate, Period.years(1), numberOfYears)
    }

}
