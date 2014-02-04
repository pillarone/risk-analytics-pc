package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.Period
import org.pillarone.riskanalytics.core.packets.Packet
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class PatternPacket extends Packet {

    private List<Double> cumulativeValues
    private List<Period> cumulativePeriods

    public PatternPacket() {
    }

    public PatternPacket(Class<? extends IPatternMarker> patternMarker, List<Double> cumulativeValues, List<Period> cumulativePeriods) {
        this.patternMarker = patternMarker
        this.cumulativeValues = cumulativeValues
        this.cumulativePeriods = cumulativePeriods
//        checkIncreasingPeriodLengths();
    }

    public PatternPacket(Class<? extends IPatternMarker> patternMarker, List<Double> cumulativeValues,
                         List<Period> cumulativePeriods, boolean stochasticHitPattern) {
        this(patternMarker, cumulativeValues, cumulativePeriods)
//        this.stochasticHitPattern = stochasticHitPattern;
    }

    /**
     * @param originalPattern   used for the patternMarker and stochasticHitPattern property
     * @param cumulativeValues
     * @param cumulativePeriods
     */
    public PatternPacket(PatternPacket originalPattern, List<Double> cumulativeValues, List<Period> cumulativePeriods) {
        this(originalPattern.patternMarker, cumulativeValues, cumulativePeriods, false)
    }

    /**
     * this field is required to enable different kinds of pattern within one mdp @see PayoutReportingCombinedPattern
     */
    private Class<? extends IPatternMarker> patternMarker;

    PatternPacket(List<Double> cumulativeValues, List<Period> cumulativePeriods) {
        this.cumulativeValues = cumulativeValues
        this.cumulativePeriods = cumulativePeriods
    }

    /**
     * If patternStartDate and occurrenceDate differ an additional DateFactor with increment 0 is inserted.
     *
     * @param occurrenceDate
     * @param periodCounter
     * @return
     */
    List<DateFactors> getDateFactorsOfCurrentPeriod(DateTime occurrenceDate, IPeriodCounter periodCounter) {
        List<DateFactors> dateFactors = new ArrayList<DateFactors>()       //      todo(sku): avoid looping through complete pattern
        for (int devPeriod = 0; devPeriod < cumulativeValues.size(); devPeriod++) {
            DateTime date = occurrenceDate.plus(cumulativePeriods.get(devPeriod))
            if (devPeriod == 0 && date.isAfter(occurrenceDate) && periodCounter.belongsToCurrentPeriod(occurrenceDate)) {
                dateFactors.add(new DateFactors(occurrenceDate, 0))
            }
            if (periodCounter.belongsToCurrentPeriod(date)) {
                dateFactors.add(new DateFactors(date, cumulativeValues.get(devPeriod)))
            }
        }

        return dateFactors
    }

    List<DateFactors> getDateFactorsOfCurrentPeriod(DateTime occurrenceDate, DateTime fromIncluded, DateTime toExcluded) {
        List<DateFactors> dateFactors = new ArrayList<DateFactors>()       //      todo(sku): avoid looping through complete pattern
        for (int devPeriod = 0; devPeriod < cumulativeValues.size(); devPeriod++) {
            DateTime date = occurrenceDate.plus(cumulativePeriods.get(devPeriod))
            if (devPeriod == 0 && date.isAfter(occurrenceDate) && !(date.isBefore(fromIncluded)) && date.isBefore(toExcluded)) {
                dateFactors.add(new DateFactors(occurrenceDate, 0))
            }
            if (!(date.isBefore(fromIncluded)) && date.isBefore(toExcluded)) {
                dateFactors.add(new DateFactors(date, cumulativeValues.get(devPeriod)))
            }
        }

        return dateFactors
    }

    boolean samePatternType(Class<? extends IPatternMarker> other) {
        return patternMarker.equals(other)
    }

    public static final class TrivialPattern extends PatternPacket {

        public TrivialPattern(Class<? extends IPatternMarker> patternMarker) {
            super(patternMarker, Collections.unmodifiableList(Arrays.asList(1d)), Collections.unmodifiableList(Arrays.asList(Period.days(0))));
        }
    }

}
