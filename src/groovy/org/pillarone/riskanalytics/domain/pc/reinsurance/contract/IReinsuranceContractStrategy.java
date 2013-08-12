package org.pillarone.riskanalytics.domain.pc.reinsurance.contract;

import org.pillarone.riskanalytics.domain.utils.marker.IReinsuranceContractMarker;

import java.util.List;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContractStrategy {

    List<IReinsuranceContract> contracts(IReinsuranceContractMarker contract);

}
