package org.pillarone.riskanalytics.domain.pc.claim.generator

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.indexing.IndexUtils
import org.pillarone.riskanalytics.domain.pc.util.DateDouble
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class AttritionalClaimsGeneratorStrategy extends AbstractClaimsGeneratorStrategy {

    protected ExposureBase claimsSizeBase
    protected RandomDistribution claimsSizeDistribution
    protected DistributionModified claimsSizeModification

    IParameterObjectClassifier getType() {
        return ClaimsGeneratorType.ATTRITIONAL
    }

    Map getParameters() {
        [
            (CLAIMS_SIZE_BASE.toString()): claimsSizeBase,
            (CLAIMS_SIZE_DISTRIBUTION.toString()): claimsSizeDistribution,
            (CLAIMS_SIZE_MODIFICATION.toString()): claimsSizeModification
        ]
    }

    @Override
    Collection<DateDouble> claims(PeriodScope periodScope, List<Factors> severityFactors) {
        lazyInitClaimsSizeGenerator()
        DateTime exposureStartDate = new DateTime().withDayOfYear(1)
        double severityFactor = IndexUtils.aggregateFactor(severityFactors, exposureStartDate, periodScope.getPeriodCounter(), exposureStartDate)
        return ClaimsGeneratorUtils.generate(severityFactor, claimSizeGenerator, dateGenerator, 1, periodScope)
    }

    ClaimType claimType() {
        ClaimType.ATTRITIONAL
    }

    @Override
    void lazyInitClaimsSizeGenerator() {
        setGenerator(claimsSizeDistribution, claimsSizeModification)
    }
}
