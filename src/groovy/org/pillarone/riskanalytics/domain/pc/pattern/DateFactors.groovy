package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic
import org.joda.time.DateTime

/**
 * Helper class for PatternPacket users
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 *
 * Compare DateFactors by date.
 */
@CompileStatic
public class DateFactors implements Comparable<DateFactors> {

    private final DateTime date
    private final double factor

    public DateFactors(DateTime date, double factor) {
        this.date = date
        this.factor = factor
    }

    public DateTime getDate() {
        return date
    }

    public double getFactor() {
        return factor
    }


    @Override
    public String toString() {
        StringBuilder result = new StringBuilder()
        result.append(date)
        result.append(SEPARATOR)
        result.append(factor)
        return result.toString()
    }

    public int compareTo(DateFactors o) {
        return o.getDate().compareTo(this.getDate())
    }

    private static final String SEPARATOR = ", "
}