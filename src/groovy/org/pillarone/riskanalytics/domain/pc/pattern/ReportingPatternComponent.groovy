package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ReportingPatternComponent extends PatternComponent implements IReportingPatternMarker {
    @Override
    protected Class<? extends IPatternMarker> getPatternMarker() {
        IReportingPatternMarker;
    }
}
