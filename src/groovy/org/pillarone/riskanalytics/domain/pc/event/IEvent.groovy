package org.pillarone.riskanalytics.domain.pc.event

import groovy.transform.CompileStatic
import org.joda.time.DateTime

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
interface IEvent {
    DateTime eventDate()
}
