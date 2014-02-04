package org.pillarone.riskanalytics.domain.pc.claim.generator

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.core.simulation.SimulationException
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.domain.utils.datetime.DateTimeUtilities
import org.pillarone.riskanalytics.domain.utils.marker.IPerilMarker
import org.pillarone.riskanalytics.domain.utils.math.dependance.DependancePacket
import org.pillarone.riskanalytics.domain.utils.math.distribution.AbstractRandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.CensoredDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified
import org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModifier
import org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution
import org.pillarone.riskanalytics.domain.utils.math.distribution.TruncatedDistribution
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator
import org.pillarone.riskanalytics.domain.utils.math.generator.RandomNumberGeneratorFactory
import umontreal.iro.lecuyer.probdist.Distribution

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
abstract class AbstractClaimsGeneratorStrategy extends AbstractParameterObject implements IClaimsGeneratorStrategy {

    private Map<String, IRandomNumberGenerator> cachedClaimSizeGenerators = new HashMap<String, IRandomNumberGenerator>()
    protected IRandomNumberGenerator claimSizeGenerator
    protected IRandomNumberGenerator dateGenerator = RandomNumberGeneratorFactory.getUniformGenerator()
    protected Distribution modifiedClaimsSizeDistribution
    protected double shift

    /**
     * This function is required to be overridden by the overriding class. Often the override will call the
     * {@link #setGenerator(org.pillarone.riskanalytics.domain.utils.math.distribution.RandomDistribution, org.pillarone.riskanalytics.domain.utils.math.distribution.DistributionModified)}
     * function.
     */
    abstract void lazyInitClaimsSizeGenerator()

    /**
     * This function attempts to lookup a random distribution and modifier pair against their hash key in the
     * {@link AbstractClaimsGeneratorStrategy#cachedClaimSizeGenerators} hash map. It then sets the
     * {@link AbstractClaimsGeneratorStrategy#claimSizeGenerator} for this class.
     * @param distribution random distribution
     * @param modifier modification for that distribution
     */
    protected void setGenerator(RandomDistribution distribution, DistributionModified modifier) {
        String key = key(distribution, modifier)
        if (cachedClaimSizeGenerators.containsKey(key)) {
            claimSizeGenerator = cachedClaimSizeGenerators.get(key)
        }
        else {
            claimSizeGenerator = RandomNumberGeneratorFactory.getGenerator(distribution, modifier)
            cachedClaimSizeGenerators.put(key, claimSizeGenerator)
        }
    }

    public IRandomNumberGenerator getDateGenerator() {
        return dateGenerator
    }

    protected void setGenerator(RandomDistribution distribution) {
        setGenerator(distribution, DistributionModifier.getStrategy(DistributionModifier.NONE, null))
    }

    protected void setDateGenerator(RandomDistribution distribution) {
        dateGenerator = RandomNumberGeneratorFactory.getGenerator(distribution)
    }

    protected String key(AbstractRandomDistribution distribution, DistributionModified modifier) {
        return String.valueOf(distribution.hashCode()) + String.valueOf(modifier.hashCode())
    }


    protected final static String CLAIMS_SIZE_BASE = "claimsSizeBase"
    protected final static String CLAIMS_SIZE_DISTRIBUTION = "claimsSizeDistribution"
    protected final static String CLAIMS_SIZE_MODIFICATION = "claimsSizeModification"
    protected final static String FREQUENCY_BASE = "frequencyBase"
    protected final static String FREQUENCY_DISTRIBUTION = "frequencyDistribution"
    protected final static String FREQUENCY_MODIFICATION = "frequencyModification"
    protected final static String OCCURRENCE_DATE_DISTRIBUTION = "occurrenceDateDistribution"

    public Distribution getModifiedClaimsSizeDistribution() {
        if(modifiedClaimsSizeDistribution == null) {
            throw new SimulationException("claims distribution is null. Have you called the lazyInit method ? Have you initialised the component correctly? ")
        }
        return modifiedClaimsSizeDistribution
    }

    public void setModifiedClaimsSizeDistribution(Distribution modifiedClaimsSizeDistribution) {
        this.modifiedClaimsSizeDistribution = modifiedClaimsSizeDistribution
    }

    public void setModifiedDistribution(RandomDistribution distribution, DistributionModified modifier) {
        Distribution dist = distribution.getDistribution()
        if (modifier.getType().equals(DistributionModifier.CENSORED) || modifier.getType().equals(DistributionModifier.CENSOREDSHIFT)) {
            modifiedClaimsSizeDistribution = new CensoredDistribution(dist, (Double) modifier.getParameters().get("min"),
                (Double) modifier.getParameters().get("max"))
        }
        else if (modifier.getType().equals(DistributionModifier.TRUNCATED) || modifier.getType().equals(DistributionModifier.TRUNCATEDSHIFT)) {
            Double leftBoundary = (Double) modifier.getParameters().get("min")
            Double rightBoundary = (Double) modifier.getParameters().get("max")
            modifiedClaimsSizeDistribution = new TruncatedDistribution(dist, leftBoundary, rightBoundary)
        }
        else if (modifier.getType().equals(DistributionModifier.LEFTTRUNCATEDRIGHTCENSOREDSHIFT)) {
            Double leftBoundary = (Double) modifier.getParameters().get("min")
            Double rightBoundary = (Double) modifier.getParameters().get("max")
            modifiedClaimsSizeDistribution = new CensoredDistribution(new TruncatedDistribution(dist,
                leftBoundary, Double.POSITIVE_INFINITY),
                Double.NEGATIVE_INFINITY, rightBoundary)
        }
        else {
            modifiedClaimsSizeDistribution = dist
        }
        shift = modifier.getParameters().get("shift") == null ? 0 : (Double) modifier.getParameters().get("shift")
    }

    public double getShift() {
        return shift
    }

    public void setShift(double shift) {
        this.shift = shift
    }
}
