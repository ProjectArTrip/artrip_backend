package org.atdev.artrip.service.dto.result;

public record PushEnabledResult(
        Boolean enabled
) {
    public static PushEnabledResult of(Boolean enabled) {
        return new PushEnabledResult(enabled);
    }
}
