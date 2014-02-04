package org.pillarone.riskanalytics.domain.pc.global

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.components.GlobalParameterComponent
import org.pillarone.riskanalytics.core.parameterization.global.Global

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
class GlobalParameters extends GlobalParameterComponent {

    private DateTime parmProjectionStartDate = new DateTime(new DateTime().getYear()+1,1,1,0,0,0,0);
    private boolean parmRunOffAfterFirstPeriod = false;
    private boolean runtimeTrivialPatterns = false;
    private boolean runtimeTrivialIndices = false;
    private boolean runtimeSanityChecks = true;
    private boolean runtimeRunAtMostFivePeriods = false;

    @Global(identifier = "runAtMostFivePeriods")
    public boolean isRuntimeRunAtMostFivePeriods() {
        return runtimeRunAtMostFivePeriods;
    }

    public void setRuntimeRunAtMostFivePeriods(boolean runtimeRunAtMostFivePeriods) {
        this.runtimeRunAtMostFivePeriods = runtimeRunAtMostFivePeriods;
    }

    @Global(identifier = "projectionStartDate")
    public DateTime projectionStartDate() {
        return parmProjectionStartDate;
    }

    @Global(identifier = "runOffAfterFirstPeriod")
    public boolean runOffAfterFirstPeriod() {
        return parmRunOffAfterFirstPeriod;
    }

    @Global(identifier = "trivialPatterns")
    public boolean trivialPatterns() {
        return isRuntimeTrivialPatterns();
    }

    @Global(identifier = "trivialIndices")
    public boolean trivialIndices() {
        return isRuntimeTrivialIndices();
    }

    @Global(identifier = "projectionPeriods")
    public Integer projectionPeriods() {
        return parmProjection.projectionPeriods();
    }

    @Global(identifier = "sanityChecks")
    public boolean isRuntimeSanityChecks() {
        return runtimeSanityChecks;
    }

    public void setRuntimeSanityChecks(boolean runtimeSanityChecks) {
        this.runtimeSanityChecks = runtimeSanityChecks;
    }

    public boolean isParmRunOffAfterFirstPeriod() {
        return parmRunOffAfterFirstPeriod;
    }

    public void setParmRunOffAfterFirstPeriod(boolean parmRunOffAfterFirstPeriod) {
        this.parmRunOffAfterFirstPeriod = parmRunOffAfterFirstPeriod;
    }

    public DateTime getParmProjectionStartDate() {
        return parmProjectionStartDate;
    }

    public void setParmProjectionStartDate(DateTime parmProjectionStartDate) {
        this.parmProjectionStartDate = parmProjectionStartDate;
    }

    public boolean isRuntimeTrivialPatterns() {
        return runtimeTrivialPatterns;
    }

    public void setRuntimeTrivialPatterns(boolean runtimeTrivialPatterns) {
        this.runtimeTrivialPatterns = runtimeTrivialPatterns;
    }

    public boolean isRuntimeTrivialIndices() {
        return runtimeTrivialIndices;
    }

    public void setRuntimeTrivialIndices(boolean runtimeTrivialIndices) {
        this.runtimeTrivialIndices = runtimeTrivialIndices;
    }
}
