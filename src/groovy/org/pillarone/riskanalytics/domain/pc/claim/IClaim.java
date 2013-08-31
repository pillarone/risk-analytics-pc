package org.pillarone.riskanalytics.domain.pc.claim;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.IComponentMarker;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
// todo(sku): depending on collector strategies additional methods returning IComponentMarker information are required
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