package org.pillarone.riskanalytics.domain.pc.claim;

import org.joda.time.DateTime;
import org.pillarone.riskanalytics.core.components.IComponentMarker;
import org.pillarone.riskanalytics.domain.pc.accounting.CashFlowType;
import org.pillarone.riskanalytics.domain.pc.accounting.ICashflow;
import org.pillarone.riskanalytics.domain.pc.util.SignTag;

import java.util.List;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimPacket {

    Double valueCumulatedAt(IComponentMarker component, CashFlowType claimProperty, SignTag signTag, DateTime evaluationDate);
    ICashflow cashFlowCumulatedAt(IComponentMarker component, CashFlowType claimProperty, SignTag signTag, DateTime evaluationDate);
    List<ICashflow> cashFlowsCumulated(IComponentMarker component, CashFlowType claimProperty, SignTag signTag, DateTime fromIncluding, DateTime toExcluded);

    /**
     * @param component
     * @param claimProperty
     * @param signTag
     * @param fromIncluding
     * @param toExcluded
     * @return empty list if there were no updates
     */
    List<ICashflow> cashFlowsIncremental(IComponentMarker component, CashFlowType claimProperty, SignTag signTag, DateTime fromIncluding, DateTime toExcluded);

    IClaim claimCumulated(IComponentMarker component, SignTag signTag, DateTime evaluationDate);

}
