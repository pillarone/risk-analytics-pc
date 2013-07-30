package org.pillarone.riskanalytics.domain.pc.claim

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ReportedClaimModelling extends PaidClaimModelling implements IClaim {

    protected final double reported

    ReportedClaimModelling(IComponentMarker origin, double ultimate, DateTime updateDate, double paid) {
        super(origin, ultimate, updateDate, paid)
    }

    ReportedClaimModelling(IComponentMarker origin, double ultimate, DateTime updateDate, double paid, double reported) {
        super(origin, ultimate, updateDate, paid)
        this.reported = reported
    }

    @Override
    final double reported() {
        reported
    }

    @Override
    final double ibnr() {
        total() - reported()
    }

    @Override
    double reserves() {
        reported() - paid()
    }
}
