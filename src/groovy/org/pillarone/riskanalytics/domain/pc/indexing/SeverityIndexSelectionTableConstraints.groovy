package org.pillarone.riskanalytics.domain.pc.indexing

import groovy.transform.CompileStatic
import org.joda.time.DateTime

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class SeverityIndexSelectionTableConstraints extends IndexSelectionTableConstraints {

    static final String IDENTIFIER = "SEVERITY_INDEX_SELECTION"

    String getName() {
        IDENTIFIER
    }

    Class getColumnType(int column) {
        [ISeverityIndexMarker, IndexMode, BaseDateMode, DateTime][column]
    }

    public Integer getColumnIndex(Class marker) {
        if (ISeverityIndexMarker.class.isAssignableFrom(marker)) {
            return 0
        }
        super.getColumnIndex(marker)
    }
}
