package org.pillarone.riskanalytics.domain.pc.claim.generator;

import org.pillarone.riskanalytics.core.parameterization.IParameterObject;
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope;
import org.pillarone.riskanalytics.domain.pc.claim.ClaimType;
import org.pillarone.riskanalytics.domain.pc.indexing.Factors;
import org.pillarone.riskanalytics.domain.pc.util.DateDouble;

import java.util.Collection;
import java.util.List;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public interface IClaimsGeneratorStrategy extends IParameterObject {

    Collection<DateDouble> claims(PeriodScope periodScope, List<Factors> severityFactors);

    ClaimType claimType();
}
