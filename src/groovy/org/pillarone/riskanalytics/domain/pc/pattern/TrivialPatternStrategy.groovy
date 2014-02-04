package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class TrivialPatternStrategy extends AbstractPatternStrategy implements IPatternStrategy {

    public IParameterObjectClassifier getType() {
        PatternStrategyType.NONE
    }

    Map getParameters() {
        [:]
    }

    PatternPacket pattern(Class<? extends IPatternMarker> patternMarker) {
        new PatternPacket.TrivialPattern(patternMarker)
    }
}
