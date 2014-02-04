package org.pillarone.riskanalytics.domain.pc.indexing

import groovy.transform.CompileStatic
import org.joda.time.DateTime

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class RunOffIndexSelectionTableConstraints extends IndexSelectionTableConstraints {

    static final String IDENTIFIER = "RUN_OFF_INDEX_SELECTION"

    String getName() {
        IDENTIFIER
    }

    public Class getColumnType(int column) {
        [IRunOffIndexMarker, IndexMode, BaseDateMode, DateTime][column]
    }

    Integer getColumnIndex(Class marker) {
        if (IRunOffIndexMarker.class.isAssignableFrom(marker)) {
            return 0
        }
        super.getColumnIndex(marker)
    }
}
