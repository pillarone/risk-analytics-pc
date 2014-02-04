package org.pillarone.riskanalytics.domain.pc.pattern

import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.packets.PacketList

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
@CompileStatic
abstract class PatternComponent extends Component {

    PacketList<PatternPacket> outPattern = new PacketList<PatternPacket>(PatternPacket)
    IPatternStrategy parmPattern = PatternStrategyType.getStrategy(PatternStrategyType.NONE, Collections.emptyMap())
    boolean globalTrivialPatterns = false

    private PatternPacket pattern

    @Override
    protected void doCalculation() {
        initSimulation()
        outPattern << pattern
    }

    private void initSimulation() {
        if (pattern == null) {
            if (globalTrivialPatterns) {
                pattern = new PatternPacket.TrivialPattern(getPatternMarker())
            }
            else {
                pattern = parmPattern.pattern(getPatternMarker())
            }
            pattern.setOrigin(this)
        }
    }

    abstract protected Class<? extends IPatternMarker> getPatternMarker()


}
