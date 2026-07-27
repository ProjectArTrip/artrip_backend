package org.atdev.artrip.controller;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.ExhibitSyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/exhibit-sync")
public class AdminExhibitSyncController {

    private final ExhibitSyncService exhibitSyncService;

    @PostMapping
    public ResponseEntity<Void> trigger(@LoginUser Long adminId) {
        exhibitSyncService.triggerSync(adminId);
        return ResponseEntity.ok().build();
    }
}
