package org.pillarone.riskanalytics.domain.pc.claim.generator

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.ListMultimap
import groovy.transform.CompileStatic
import org.apache.commons.lang.NotImplementedException
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.parameterization.AbstractParameterObject
import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.IParameterObjectClassifier
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.pillarone.riskanalytics.core.simulation.engine.PeriodScope
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.domain.pc.claim.ClaimRoot
import org.pillarone.riskanalytics.domain.pc.claim.ClaimType
import org.pillarone.riskanalytics.domain.pc.exposure.ExposureBase
import org.pillarone.riskanalytics.domain.pc.indexing.Factors
import org.pillarone.riskanalytics.domain.utils.InputFormatConverter
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.math.generator.IRandomNumberGenerator

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
class ExternalValuesByIterationAndDateStrategy extends AbstractParameterObject implements IExternalValuesStrategy {

    static Log LOG = LogFactory.getLog(ExternalValuesByIterationAndDateStrategy)

    static final String ITERATION = "iteration"
    static final String VALUE = "value"
    static final String DATE = "date"

    ConstrainedMultiDimensionalParameter valueTableExtended = new ConstrainedMultiDimensionalParameter(
        [[0], [], [0d]], [ITERATION, VALUE, DATE],
        ConstraintsFactory.getConstraints(DoubleConstraints.IDENTIFIER))
//        ConstraintsFactory.getConstraints(IntDateTimeDoubleConstraints.IDENTIFIER))

    private ListMultimap<IterationPeriod, DateDouble> internalValueByIteration = ArrayListMultimap.create()
    private int iteration = 0

    IParameterObjectClassifier getType() {
//        ExternalValuesType.BY_ITERATION_AND_DATE
        ExternalValuesType.BY_ITERATION
    }

    Map getParameters() {
        ['valueTableExtended': valueTableExtended]
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
//    public List<ClaimRoot> generateClaims(List<ClaimRoot> baseClaims, List<UnderwritingInfoPacket> uwInfos,
//                                          List<Factors> severityFactors, List uwInfosFilterCriteria,
//                                          PeriodScope periodScope, ClaimType claimType, ExposureBase claimsSizeBase,
//                                          IRandomNumberGenerator dateGenerator) {
//        lazyInitializeDistributionMaps(periodScope)
//        if (periodScope.isFirstPeriod()) {
//            iteration++
//        }
//        List<DateDouble> ultimates = internalValueByIteration.get(new IterationPeriod(iteration, periodScope.getCurrentPeriod()))
//        int numberOfClaims = ultimates.size()
//        double severityScalingFactor = UnderwritingInfoUtils.scalingFactor(uwInfos, claimsSizeBase, uwInfosFilterCriteria)
//        List<EventPacket> events = ClaimsGeneratorUtils.generateEventsOrNull(claimType, numberOfClaims, periodScope, dateGenerator)
//        LossesOccurringContractBase contractBase = new LossesOccurringContractBase()
//        for (int i = 0 i < numberOfClaims i++) {
//            EventPacket event = events == null ? null : events.get(i)
//            // todo(sku): replace with information from underwriting
//            DateTime exposureStartDate = contractBase.exposureStartDate(periodScope, dateGenerator)
//            double ultimate = ultimates.get(i).value * -severityScalingFactor
//            DateTime occurrenceDate = ultimates.get(i).date
//            double scaleFactor = IndexUtils.aggregateFactor(severityFactors, exposureStartDate, periodScope.getPeriodCounter(), exposureStartDate)
//            baseClaims.add(new ClaimRoot(ultimate * scaleFactor, claimType, exposureStartDate, occurrenceDate, event))
//        }
//        return baseClaims
//    }

    private void lazyInitializeDistributionMaps(PeriodScope periodScope) {
        if (internalValueByIteration.isEmpty()) {
            int columnIndexIteration = valueTableExtended.getColumnIndex(ITERATION)
            int columnIndexDate = valueTableExtended.getColumnIndex(DATE)
            int columnIndexValue = valueTableExtended.getColumnIndex(VALUE)
            for (int row = valueTableExtended.getTitleRowCount(); row < valueTableExtended.getRowCount(); row++) {
                int iteration = InputFormatConverter.getInt(valueTableExtended.getValueAt(row, columnIndexIteration))
                double value = InputFormatConverter.getDouble(valueTableExtended.getValueAt(row, columnIndexValue))
                DateTime date = (DateTime) valueTableExtended.getValueAt(row, columnIndexDate)
                int period = periodScope.getPeriodCounter().belongsToPeriod(date)
                internalValueByIteration.put(new IterationPeriod(iteration, period), new DateDouble(date, value))
            }
        }
    }


    @Override
    Collection<org.pillarone.riskanalytics.domain.pc.util.DateDouble> claims(IPeriodCounter periodCounter, List<Factors> severityFactors) {
        throw new NotImplementedException()
    }

    @Override
    ClaimType claimType() {
        throw new NotImplementedException()
    }

    private class IterationPeriod {
        int iteration
        int period

        private IterationPeriod(int iteration, int period) {
            this.iteration = iteration
            this.period = period
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true
            if (o == null || getClass() != o.getClass()) return false

            IterationPeriod that = (IterationPeriod) o

            if (iteration != that.iteration) return false
            if (period != that.period) return false

            return true
        }

        @Override
        public int hashCode() {
            int result = iteration
            result = 31 * result + period
            return result
        }
    }

    private class DateDouble {
        DateTime date
        double value

        private DateDouble(DateTime date, double value) {
            this.date = date
            this.value = value
        }
    }
}
