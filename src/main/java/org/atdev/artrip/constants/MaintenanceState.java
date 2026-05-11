package org.atdev.artrip.constants;

import lombok.Getter;

@Getter
public enum MaintenanceState {
    NORMAL("정상"),
    NOTICE("안내"),
    BLOCK("차단");

    private final String label;

    MaintenanceState(String label) {
        this.label = label;
    }
}
