package org.pillarone.riskanalytics.domain.pc.pattern

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class PatternStrategyType extends AbstractParameterObjectClassifier {

    public static final PatternStrategyType NONE = new PatternStrategyType('none', 'NONE', [:])
    public static final PatternStrategyType INCREMENTAL = new PatternStrategyType("incremental", "INCREMENTAL", [
        incrementalPattern :  new ConstrainedMultiDimensionalParameter([[0],[1d]], [PatternTableConstraints.MONTHS,INCREMENTS],
            ConstraintsFactory.getConstraints(PatternTableConstraints.IDENTIFIER)),
    ])

    public static final all = [NONE, INCREMENTAL]

    public static final String INCREMENTS = "Increments";

    protected static Map types = [:]
    static {
        PatternStrategyType.all.each {
            PatternStrategyType.types[it.toString()] = it
        }
    }

    private PatternStrategyType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }


    public static PatternStrategyType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IPatternStrategy getDefault() {
        return new TrivialPatternStrategy();
    }

    static IPatternStrategy getStrategy(PatternStrategyType type, Map parameters) {
        IPatternStrategy pattern;
        switch (type) {
            case NONE:
                return new TrivialPatternStrategy()
                break
            case INCREMENTAL:
                return new IncrementalPatternStrategy(
                    incrementalPattern : (ConstrainedMultiDimensionalParameter) parameters['incrementalPattern'])
                break;
            default:
                throw new NotImplementedException(type.toString())
        }
    }
}
