package org.atdev.artrip.external.publicdata.exhibit.web.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.external.publicdata.exhibit.service.ExhibitSyncService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/sync")
public class ExhibitSyncController {

    private final ExhibitSyncService exhibitSyncService;

    @PostMapping("/exhibits")
    public CommonResponse<ExhibitSyncService.SyncResult> syncAll() {
        ExhibitSyncService.SyncResult result = exhibitSyncService.syncAll();
        return CommonResponse.onSuccess(result);
    }
}
