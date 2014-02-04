package org.pillarone.riskanalytics.domain.pc.indexing

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.IMultiDimensionalConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class IndexSelectionTableConstraints implements IMultiDimensionalConstraints {
    
    static final String IDENTIFIER = "INDEX_SELECTION"
    static final String INDEX = "Index"
    static final String MODE = "Index Mode"
    static final String BASEDATEMODE = "Base Date Mode"
    static final String DATE = "Date"

    static final List<String> COLUMN_TITLES = [INDEX, MODE, BASEDATEMODE, DATE]

    boolean matches(int row, int column, Object value) {
        column  < 3 ? value instanceof String : value instanceof DateTime
    }

    String getName() {
        IDENTIFIER
    }

    Class getColumnType(int column) {
        return [IIndexMarker, IndexMode, BaseDateMode, DateTime][column]
    }

    Integer getColumnIndex(Class marker) {
        if (IIndexMarker.class.isAssignableFrom(marker)) {
            return 0
        }
        else if (IndexMode.class.isAssignableFrom(marker)) {
            return 1
        }
        else if (BaseDateMode.class.isAssignableFrom(marker)) {
            return 2
        }
        else if (DateTime.class.isAssignableFrom(marker)) {
            return 3
        }
        return null
    }

    boolean emptyComponentSelectionAllowed(int column) {
        false
    }
}
