package org.pillarone.riskanalytics.domain.pc.reinsurance.contract

import org.pillarone.riskanalytics.domain.pc.accounting.ICashflow

/**
 *  Common methods to calculate the effects of a reinsurance contract
 *  implemented by all reinsurance contract strategies.
 *
 * @author stefan.kunz (at) intuitive-collaboration (dot) com
 */
public interface IReinsuranceContract {

    Collection<ICashflow> cededCashflows(Collection<ICashflow> grossCashflows);

}