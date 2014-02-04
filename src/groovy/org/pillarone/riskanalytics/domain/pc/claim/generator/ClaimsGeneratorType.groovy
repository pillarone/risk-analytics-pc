package org.pillarone.riskanalytics.domain.pc.claim.generator

import org.apache.commons.lang.NotImplementedException
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObjectClassifier
import org.pillarone.riskanalytics.core.parameterization.IParameterObject
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.domain.pc.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionType
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class ClaimsGeneratorType extends AbstractParameterObjectClassifier {

    public static final ClaimsGeneratorType ATTRITIONAL = new ClaimsGeneratorType("attritional", "ATTRITIONAL", [
        claimsSizeBase: ExposureBase.ABSOLUTE,
        claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ["constant": 0d]),
        claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, [:])])
    public static final ClaimsGeneratorType EXTERNAL_VALUES = new ClaimsGeneratorType("external values by iteration", "EXTERNAL_VALUES", [
        claimsSizeBase: ExposureBase.ABSOLUTE,
        values: ExternalValuesType.getDefault(),
        produceClaim: FrequencySeverityClaimType.SINGLE])

    public static final all = [ATTRITIONAL, EXTERNAL_VALUES]

    protected static Map types = [:]
    static {
        ClaimsGeneratorType.all.each {
            ClaimsGeneratorType.types[it.toString()] = it
        }
    }

    private ClaimsGeneratorType(String displayName, String typeName, Map parameters) {
        super(displayName, typeName, parameters)
    }

    public static ClaimsGeneratorType valueOf(String type) {
        types[type]
    }

    public List<IParameterObjectClassifier> getClassifiers() {
        return all
    }

    public IParameterObject getParameterObject(Map parameters) {
        return getStrategy(this, parameters)
    }

    static IClaimsGeneratorStrategy getDefault() {
        return new AttritionalClaimsGeneratorStrategy(
            claimsSizeBase: ExposureBase.ABSOLUTE,
            claimsSizeDistribution: DistributionType.getStrategy(DistributionType.CONSTANT, ['constant': 0d]),
            claimsSizeModification: DistributionModifier.getStrategy(DistributionModifier.NONE, Collections.emptyMap())
        )
    }

    static IClaimsGeneratorStrategy getStrategy(ClaimsGeneratorType type, Map parameters) {
        IClaimsGeneratorStrategy claimsGenerator;
        switch (type) {
            case ClaimsGeneratorType.ATTRITIONAL:
                claimsGenerator = new AttritionalClaimsGeneratorStrategy(
                    claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
                    claimsSizeDistribution: (RandomDistribution) parameters.get("claimsSizeDistribution"),
                    claimsSizeModification: (DistributionModified) parameters.get("claimsSizeModification"))
                break;
//            case ClaimsGeneratorType.EXTERNAL_VALUES:
//                claimsGenerator = new ExternalValuesStrategy(
//                    claimsSizeBase: (ExposureBase) parameters.get("claimsSizeBase"),
//                    values: (IExternalValuesStrategy) parameters.get("values"),
//                    produceClaim: (FrequencySeverityClaimType) parameters.get("produceClaim")) as IClaimsGeneratorStrategy
//                break;
            default:
                throw new NotImplementedException(type.toString())
        }
        return claimsGenerator;
    }

}
