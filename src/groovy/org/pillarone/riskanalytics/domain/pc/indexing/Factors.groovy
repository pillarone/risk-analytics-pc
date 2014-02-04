package org.pillarone.riskanalytics.domain.pc.indexing

import groovy.transform.CompileStatic
import org.apache.commons.lang.NotImplementedException
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class Factors {

    private FactorsPacket packet
    private BaseDateMode baseDate
    private IndexMode indexMode
    private DateTime fixedDate


    public Factors(FactorsPacket packet, BaseDateMode baseDate, IndexMode indexMode, DateTime fixedDate) {
        this.packet = packet
        this.baseDate = baseDate
        this.indexMode = indexMode
        this.fixedDate = fixedDate
    }

    public Double getFactor(DateTime date) {
        switch (indexMode) {
            case IndexMode.CONTINUOUS:
                return packet.getFactorInterpolated(date)
            case IndexMode.STEPWISE_NEXT:
                return packet.getFactorCeiling(date)
            case IndexMode.STEPWISE_PREVIOUS:
                return packet.getFactorFloor(date)
            default:
                throw new NotImplementedException("BaseDateMode " + indexMode.toString() + " not implemented.")
        }
    }

    /**
     * Evaluates the baseDate property to define the evaluation date of getFactor(date)
     * @param periodCounter
     * @param dateOfLoss
     * @return
     */
    public Double getFactor(IPeriodCounter periodCounter, DateTime dateOfLoss, DateTime updateDate) {
        DateTime baseDate = evaluateBaseDate(periodCounter, dateOfLoss)
        double factorAtBaseDate = getFactor(baseDate)
        double factorAtUpdateDate = getFactor(updateDate)
        return factorAtUpdateDate / factorAtBaseDate
    }

    public IndexPacket getIndices(DateTime date) {
        new IndexPacket(
            packet.getFactorInterpolated(date),
            packet.getFactorFloor(date),
            packet.getFactorCeiling(date)
        )
    }

    /**
     * Evaluates the baseDate property to define the evaluation date of getIndices(date)
     * @param periodCounter
     * @param dateOfLoss
     * @return
     */
    public IndexPacket getIndices(IPeriodCounter periodCounter, DateTime dateOfLoss) {
        getIndices(evaluateBaseDate(periodCounter, dateOfLoss))
    }

    /**
     * @param periodCounter
     * @param dateOfLoss
     * @return evaluation date according to baseDate property
     */
    private DateTime evaluateBaseDate(IPeriodCounter periodCounter, DateTime dateOfLoss) {
        switch (baseDate) {
            case BaseDateMode.DATE_OF_LOSS:
                return dateOfLoss
            case BaseDateMode.DAY_BEFORE_FIRST_PERIOD:
                return periodCounter.startOfFirstPeriod().minusDays(1)
            case BaseDateMode.START_OF_PROJECTION:
                return periodCounter.startOfFirstPeriod()
            case BaseDateMode.FIXED_DATE:
                return fixedDate
        }
        return null
    }
}
