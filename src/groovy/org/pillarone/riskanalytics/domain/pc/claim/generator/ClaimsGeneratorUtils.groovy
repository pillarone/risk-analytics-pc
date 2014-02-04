package org.pillarone.riskanalytics.domain.pc.claim.generator

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.pc.util.DateDouble
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ClaimsGeneratorUtils {

    static Collection<DateDouble> generate(double severityScaleFactor, IRandomNumberGenerator claimSizeGenerator,
                                                 IRandomNumberGenerator dateGenerator, int claimNumber, PeriodScope periodScope) {
        Collection<DateDouble> dateAndDoubles = []
        for (int i = 0; i < claimNumber; i++) {
            DateTime occurrenceDate = DateTimeUtilities.getDate(periodScope, dateGenerator.nextValue().doubleValue())
            double ultimate = (Double) claimSizeGenerator.nextValue() * -severityScaleFactor
            dateAndDoubles << new DateDouble(ultimate, occurrenceDate)
        }
        return dateAndDoubles;
    }
}
