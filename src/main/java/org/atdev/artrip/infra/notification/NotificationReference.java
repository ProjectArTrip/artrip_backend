package org.atdev.artrip.infra.notification;

import org.atdev.artrip.constants.NotificationAction;

import java.util.HashMap;
import java.util.Map;

public record NotificationReference(
        NotificationAction action,
        Long referenceId
) {

    public static NotificationReference none() {
        return new NotificationReference(NotificationAction.NONE, null);
    }

    public static NotificationReference reviewEdit(Long reviewId) {
        return new NotificationReference(NotificationAction.MOVE_REVIEW_EDIT, reviewId);
    }

    public static NotificationReference notice(Long noticeId) {
        return new NotificationReference(NotificationAction.MOVE_NOTICE_DETAIL, noticeId);
    }

    public static NotificationReference exhibit(Long exhibitId) {
        return new NotificationReference(NotificationAction.MOVE_EXHIBIT_DETAIL, exhibitId);
    }

    public static NotificationReference exhibitList() {
        return new NotificationReference(NotificationAction.MOVE_EXHIBIT_LIST, null);
    }


    public Map<String, String> toFcmData() {
        Map<String, String> data = new HashMap<>();
        data.put("action", action.getAction());
        if (referenceId != null) {
            data.put("referenceId", String.valueOf(referenceId));
        }
        return Map.copyOf(data);
    }
}
