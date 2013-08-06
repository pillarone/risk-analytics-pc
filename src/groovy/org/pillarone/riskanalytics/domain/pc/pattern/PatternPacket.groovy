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

    private List<Double> cumulativeValues;
    private List<Period> cumulativePeriods;

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
    public List<DateFactors> getDateFactorsOfCurrentPeriod(DateTime occurrenceDate, IPeriodCounter periodCounter) {
        List<DateFactors> dateFactors = new ArrayList<DateFactors>();       //      todo(sku): avoid looping through complete pattern
        for (int devPeriod = 0; devPeriod < cumulativeValues.size(); devPeriod++) {
            DateTime date = occurrenceDate.plus(cumulativePeriods.get(devPeriod));
            if (devPeriod == 0 && date.isAfter(occurrenceDate) && periodCounter.belongsToCurrentPeriod(occurrenceDate)) {
                dateFactors.add(new DateFactors(occurrenceDate, 0));
            }
            if (periodCounter.belongsToCurrentPeriod(date)) {
                dateFactors.add(new DateFactors(date, cumulativeValues.get(devPeriod)));
            }
        }

        return dateFactors;
    }

    public List<DateFactors> getDateFactorsOfCurrentPeriod(DateTime occurrenceDate, DateTime fromIncluded, DateTime toExcluded) {
        List<DateFactors> dateFactors = new ArrayList<DateFactors>();       //      todo(sku): avoid looping through complete pattern
        for (int devPeriod = 0; devPeriod < cumulativeValues.size(); devPeriod++) {
            DateTime date = occurrenceDate.plus(cumulativePeriods.get(devPeriod));
            if (devPeriod == 0 && date.isAfter(occurrenceDate) && !(date.isBefore(fromIncluded)) && date.isBefore(toExcluded)) {
                dateFactors.add(new DateFactors(occurrenceDate, 0));
            }
            if (!(date.isBefore(fromIncluded)) && date.isBefore(toExcluded)) {
                dateFactors.add(new DateFactors(date, cumulativeValues.get(devPeriod)));
            }
        }

        return dateFactors;
    }
}
