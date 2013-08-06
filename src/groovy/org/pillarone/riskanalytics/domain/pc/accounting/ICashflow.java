package org.pillarone.riskanalytics.domain.pc.accounting;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.IComponentMarker;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface ICashflow {

    IComponentMarker origin();

    CashFlowType cashFlowType();

    DateTime date();

    double amount();
}
