package org.pillarone.riskanalytics.domain.pc.claim;

/**
 * @author stefan (dot) kunz (at) intuitive-collaboration (dot) com
 */
public enum ClaimProperty {

    INITIAL {
        @Override
        double value(IClaim claim) {
            return claim.initial();
        }
    },
    TOTAL {
        @Override
        double value(IClaim claim) {
            return claim.total();
        }
    },
    REPORTED {
        @Override
        double value(IClaim claim) {
            return claim.reported();
        }
    },
    PAID {
        @Override
        double value(IClaim claim) {
            return claim.paid();
        }
    },
    RESERVES {
        @Override
        double value(IClaim claim) {
            return claim.reserves();
        }
    },
    IBNR {
        @Override
        double value(IClaim claim) {
            return claim.ibnr();
        }
    },
    OUTSTANDING {
        @Override
        double value(IClaim claim) {
            return claim.outstanding();
        }
    },
    PREMIUM_RISK {
        @Override
        double value(IClaim claim) {
            return claim.premiumRisk();
        }
    },
    RESERVE_RISK {
        @Override
        double value(IClaim claim) {
            return claim.reserveRisk();
        }
    };

    abstract double value(IClaim claim);
}
