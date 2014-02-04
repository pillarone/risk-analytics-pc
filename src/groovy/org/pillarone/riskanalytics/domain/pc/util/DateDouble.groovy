package org.pillarone.riskanalytics.domain.pc.util

import groovy.transform.CompileStatic
import org.joda.time.DateTime

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class DateDouble {

    Double value
    DateTime date

    DateDouble(Double value, DateTime date) {
        this.value = value
        this.date = date
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        DateDouble that = (DateDouble) o

        if (date != that.date) return false
        if (value != that.value) return false

        return true
    }

    int hashCode() {
        int result
        result = (value != null ? value.hashCode() : 0)
        result = 31 * result + (date != null ? date.hashCode() : 0)
        return result
    }
}
