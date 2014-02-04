package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.parameterization.ConstrainedString

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class PatternUtils {

    /**
     * @param patterns      from different origins. Function will fail if one origin would provide several patterns implementing
     *                      the same IPatternMarker interface.
     * @param criteria      the selected component is used for the comparison with the pattern origin
     * @param patternMarker is necessary as a component might produce several patterns of different types, but only one
     *                      per type (see PayoutReportingCombinedPatterns)
     * @param returnClone   return a clone of the pattern matching the criteria
     * @return the pattern with the origin component matching the selected component in criteria
     */
    public static PatternPacket filterPattern(List<PatternPacket> patterns, ConstrainedString criteria,
                                              Class<? extends IPatternMarker> patternMarker, boolean returnClone = true) {
        for (PatternPacket pattern : patterns) {
            if (pattern.origin.equals(criteria.selectedComponent) && pattern.samePatternType(patternMarker)) {
                return returnClone ? pattern.clone() as PatternPacket : pattern as PatternPacket;
            }
        }
        return null;
    }
}
