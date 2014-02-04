package org.pillarone.riskanalytics.domain.pc.pattern;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IPatternStrategy extends IParameterObject {

    PatternPacket pattern(Class<? extends IPatternMarker> patternMarker);
}
