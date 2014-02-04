package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class PayoutPatternComponent extends PatternComponent implements IPayoutPatternMarker {
    @Override
    protected Class<? extends IPatternMarker> getPatternMarker() {
        return IPayoutPatternMarker.class;
    }
}
