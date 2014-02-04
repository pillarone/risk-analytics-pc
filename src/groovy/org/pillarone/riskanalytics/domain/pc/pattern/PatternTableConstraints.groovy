package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class PatternTableConstraints implements IMultiDimensionalConstraints {

    static final String IDENTIFIER = "PATTERN"
    static final String MONTHS = "Months"

    boolean matches(int row, int column, Object value) {
        if (column == 0) {
            return value instanceof Integer
        }
        else {
            return value instanceof Number
        }
    }

    String getName() {
        IDENTIFIER
    }

    Class getColumnType(int column) {
        return column == 0 ? Integer : Double
    }

    Integer getColumnIndex(Class marker) {
        null
    }

    boolean emptyComponentSelectionAllowed(int column) {
        false
    }
}
