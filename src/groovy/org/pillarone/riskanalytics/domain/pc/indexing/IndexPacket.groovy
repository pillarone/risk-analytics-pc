package org.pillarone.riskanalytics.domain.pc.indexing

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.packets.MultiValuePacket

/**
 * Contains the different index mode values. Should be filled with values from period start date
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
class IndexPacket extends MultiValuePacket {

    private double continuous = 1
    private double stepwisePrevious = 1
    private double stepwiseNext = 1

    IndexPacket() {
    }

    public IndexPacket(double continuous, double stepwisePrevious, double stepwiseNext) {
        this.continuous = continuous
        this.stepwisePrevious = stepwisePrevious
        this.stepwiseNext = stepwiseNext
    }

    /**
     * @param factorsPackets
     * @param evaluationDate typically begin of period
     */
    IndexPacket(FactorsPacket factorsPackets, DateTime evaluationDate) {
        continuous *= factorsPackets.getFactorInterpolated(evaluationDate)
        stepwisePrevious *= factorsPackets.getFactorFloor(evaluationDate)
        stepwiseNext *= factorsPackets.getFactorCeiling(evaluationDate)
    }

    void multiply(IndexPacket other) {
        continuous = other.continuous
        stepwisePrevious *= other.stepwisePrevious
        stepwiseNext *= other.stepwiseNext
    }
}
