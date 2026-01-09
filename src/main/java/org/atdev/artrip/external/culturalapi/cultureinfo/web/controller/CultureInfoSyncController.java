package org.atdev.artrip.external.culturalapi.cultureinfo.web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.external.culturalapi.cultureinfo.service.CultureInfoSyncService;
import org.atdev.artrip.global.apipayload.CommonResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/admin/sync")
@RequiredArgsConstructor
public class CultureInfoSyncController {

    private final CultureInfoSyncService CultureInfoSyncService;

    @GetMapping("/exhibits")
    public CommonResponse<Map<String, Object>> syncByPeriod(
            @RequestParam String from,
            @RequestParam(required = false) String to) {

        log.info("기간별 전시 동기화 시작 - 기간: {} ~ {}", from, to != null ? to : "전체");

        CultureInfoSyncService.SyncResult result;
        if (to != null) {
            result = CultureInfoSyncService.syncByPeriod(from, to);
        } else {
            result = CultureInfoSyncService.syncByPeriod(from, null);
        }

        return CommonResponse.onSuccess(Map.of(
                "period", Map.of(
                        "from", from,
                        "to", to != null ? to : "unlimited"
                ),
                "inserted", result.getInserted(),
                "updated", result.getUpdated(),
                "skipped", result.getSkipped(),
                "failed", result.getFailed()
        ));
    }

    @PostMapping("/exhibits/all")
    public CommonResponse<Map<String, Object>> syncAll() {
        log.info("전체 전시 동기화 시작");

        CultureInfoSyncService.SyncResult result = CultureInfoSyncService.syncAll();

        return CommonResponse.onSuccess(Map.of(
                "inserted", result.getInserted(),
                "updated", result.getUpdated(),
                "skipped", result.getSkipped(),
                "failed", result.getFailed()
        ));
    }
}
