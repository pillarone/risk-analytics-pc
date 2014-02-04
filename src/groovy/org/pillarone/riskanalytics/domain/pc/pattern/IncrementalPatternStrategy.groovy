package org.pillarone.riskanalytics.domain.pc.pattern

import org.joda.time.Period
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class IncrementalPatternStrategy extends AbstractPatternStrategy implements IPatternStrategy {

    static final String INCREMENTAL_PATTERN = "incrementalPattern"

    private ConstrainedMultiDimensionalParameter incrementalPattern
    private PatternPacket pattern

    IParameterObjectClassifier getType() {
        PatternStrategyType.INCREMENTAL
    }

    Map getParameters() {
        [(INCREMENTAL_PATTERN.toString()) : incrementalPattern]
    }

    PatternPacket pattern(Class<? extends IPatternMarker> patternMarker) {
        if (pattern == null) {
            int columnMonthIndex = incrementalPattern.getColumnIndex(PatternTableConstraints.MONTHS)
            List<Double> incrementalValues = getPatternValues(incrementalPattern, columnMonthIndex,
                incrementalPattern.getColumnIndex(PatternStrategyType.INCREMENTS))
            List<Double> cumulativeValues = getCumulativePatternValues(incrementalValues)
            List<Period> cumulativePeriods = getCumulativePeriods(incrementalPattern, columnMonthIndex)
            pattern = new  PatternPacket(patternMarker, cumulativeValues, cumulativePeriods)
        }
        return pattern
    }
}
