package org.pillarone.riskanalytics.domain.pc.claim.generator

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import groovy.transform.CompileStatic
import org.apache.commons.lang.NotImplementedException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.indexing.Factors
import org.pillarone.riskanalytics.domain.pc.util.DateDouble
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ExternalValuesByIterationStrategy extends AbstractParameterObject implements IExternalValuesStrategy {

    static Log LOG = LogFactory.getLog(ExternalValuesByIterationStrategy)

    static final String ITERATION = "iteration"
    static final String VALUE = "value"

    private ConstrainedMultiDimensionalParameter valueTable = new ConstrainedMultiDimensionalParameter(
        GroovyUtils.toList("[[0], [0d]]"), [ITERATION, VALUE],
        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
    private PeriodApplication usage = PeriodApplication.FIRSTPERIOD

    private ListMultimap<Integer, Double> internalValueByIteration = ArrayListMultimap.create()
    private int iteration = 0

    IParameterObjectClassifier getType() {
        ExternalValuesType.BY_ITERATION
    }

    Map getParameters() {
        [
            "valueTable": valueTable,
            "usage": usage
        ]
    }

    /**
     *
     * @param baseClaims this list is needed in order to calculate the number of required claims after calculateDependantClaimsWithContractBase
     *                   has been executed i.e. if external severity information is provided and only the missing claim
     *                   number needs to be generated.
     * @param uwInfos
     * @param severityFactors
     * @param uwInfosFilterCriteria
     * @param periodScope
     * @return
     */
//    List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
//                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
//                                          PeriodScope periodScope, ClaimType claimType, ExposureBase claimsSizeBase,
//                                          IRandomNumberGenerator dateGenerator) {
//        lazyInitializeDistributionMaps()
//        if (usage.equals(PeriodApplication.ALLPERIODS) || periodScope.isFirstPeriod()) {
//            if (periodScope.isFirstPeriod()) {
//                iteration++
//            }
//            List<Double> ultimates = internalValueByIteration.get(iteration)
//            int numberOfClaims = ultimates.size()
//            double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, claimsSizeBase, uwInfosFilterCriteria)
//            List<EventPacket> events = ClaimsGeneratorUtils.generateEventsOrNull(claimType, numberOfClaims, periodScope, dateGenerator)
//            LossesOccurringContractBase contractBase = new LossesOccurringContractBase()
//            for (int i = 0 i < numberOfClaims i++) {
//                EventPacket event = events == null ? null : events.get(i)
//                // todo(sku): replace with information from underwriting
//                DateTime exposureStartDate = contractBase.exposureStartDate(periodScope, dateGenerator)
//                double ultimate = ultimates.get(i) * -severityScalingFactor
//                DateTime occurrenceDate = contractBase.occurrenceDate(exposureStartDate, dateGenerator, periodScope, event)
//                double scaleFactor = IndexUtils.aggregateFactor(severityFactors, exposureStartDate, periodScope.getPeriodCounter(), exposureStartDate)
//                baseClaims.add(new ClaimRoot(ultimate * scaleFactor, claimType, exposureStartDate, occurrenceDate, event))
//            }
//        }
//        baseClaims
//    }

    private void lazyInitializeDistributionMaps() {
        if (internalValueByIteration.isEmpty()) {
            int columnIndexIteration = valueTable.getColumnIndex(ITERATION)
            int columnIndexValue = valueTable.getColumnIndex(VALUE)
            for (int row = valueTable.getTitleRowCount(); row < valueTable.getRowCount(); row++) {
                int iteration = InputFormatConverter.getInt(valueTable.getValueAt(row, columnIndexIteration))
                double value = InputFormatConverter.getDouble(valueTable.getValueAt(row, columnIndexValue))
                internalValueByIteration.put(iteration, value)
            }
        }
    }

    @Override
    Collection<DateDouble> claims(IPeriodCounter periodCounter, List<Factors> severityFactors) {
        throw new NotImplementedException()
    }

    @Override
    ClaimType claimType() {
        throw new NotImplementedException()
    }
}
