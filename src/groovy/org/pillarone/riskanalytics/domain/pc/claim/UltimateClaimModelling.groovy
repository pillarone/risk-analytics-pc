package org.pillarone.riskanalytics.domain.pc.claim

import groovy.transform.CompileStatic
import org.apache.commons.lang.NotImplementedException
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.core.packets.MultiValuePacket

/**
 * This is a helper class for persisting values. For calculation purposes use AbstractClaimPacket.
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class UltimateClaimModelling extends MultiValuePacket implements IClaim {

    protected final IComponentMarker origin
    protected final double ultimate
    protected final DateTime updateDate

    UltimateClaimModelling(IComponentMarker origin, double ultimate, DateTime updateDate) {
        this.origin = origin
        this.ultimate = ultimate
        this.updateDate = updateDate
    }

    @Override
    final IComponentMarker origin() {
        return origin
    }

    @Override
    final DateTime updateDate() {
        return updateDate
    }

    @Override
    final double initial() {
        ultimate
    }

    @Override
    final double total() {
        ultimate
    }

    @Override
    double reported() {
        total()
    }

    @Override
    double paid() {
        total()
    }

    @Override
    double reserves() {
        return 0
    }

    @Override
    double ibnr() {
        return 0
    }

    @Override
    double outstanding() {
        return 0
    }

    @Override
    double premiumRisk() {
        throw new NotImplementedException()
    }

    @Override
    double reserveRisk() {
        throw new NotImplementedException()
    }

    @Override
    public String toString() {
        return "UltimateClaimModelling{ultimate= $ultimate, updateDate= $updateDate}"
    }

    @Override
    int compareTo(Object o) {
        return updateDate.compareTo(((IClaim) o).updateDate())
    }
}
