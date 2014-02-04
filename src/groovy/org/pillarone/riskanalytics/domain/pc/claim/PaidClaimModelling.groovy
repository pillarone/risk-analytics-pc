package org.pillarone.riskanalytics.domain.pc.claim

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class PaidClaimModelling extends UltimateClaimModelling implements IClaim {

    protected final double paid

    PaidClaimModelling() {
    }

    PaidClaimModelling(IComponentMarker origin, double ultimate, DateTime updateDate) {
        super(origin, ultimate, updateDate)
    }

    PaidClaimModelling(IComponentMarker origin, double ultimate, DateTime updateDate, double paid) {
        super(origin, ultimate, updateDate)
        this.paid = paid
    }

    @Override
    double reported() {
        total()
    }

    @Override
    final double paid() {
        paid
    }

    @Override
    double reserves() {
        return outstanding()
    }

    @Override
    final double outstanding() {
        total() - paid()
    }

    @Override
    public String toString() {
        return "PaidClaimModelling{ultimate= $ultimate, paid= $paid, updateDate= $updateDate}"
    }

}
