package org.atdev.artrip.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NotificationAction {

    MOVE_NOTICE_DETAIL("MOVE_NOTICE_DETAIL"),
    MOVE_CURATION_DETAIL("MOVE_CURATION_DETAIL"),
    MOVE_EXHIBIT_DETAIL("MOVE_EXHIBIT_DETAIL"),
    MOVE_STAMP("MOVE_STAMP"),
    MOVE_REVIEW_EDIT("MOVE_REVIEW_EDIT");

    private final String action;

}