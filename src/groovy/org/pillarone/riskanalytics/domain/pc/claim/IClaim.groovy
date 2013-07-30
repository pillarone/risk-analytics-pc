package org.pillarone.riskanalytics.domain.pc.claim

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.IComponentMarker
import org.pillarone.riskanalytics.domain.pc.util.ChangeMode
import org.pillarone.riskanalytics.domain.pc.util.Mode

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaim extends Comparable {

    IComponentMarker origin();
    DateTime updateDate();

    double initial();
    double total();
    double reported();
    double paid();
    double reserves();
    double ibnr();
    double outstanding();
    double premiumRisk();
    double reserveRisk();

}