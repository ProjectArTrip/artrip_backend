package org.atdev.artrip.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.AdminCreateNoticeRequest;
import org.atdev.artrip.controller.dto.request.AdminUpdateNoticeRequest;
import org.atdev.artrip.controller.spec.AdminNoticeSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AdminNoticeService;
import org.atdev.artrip.service.dto.command.AdminNoticeCreateCommand;
import org.atdev.artrip.service.dto.command.AdminNoticeUpdateCommand;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notice")
@RequiredArgsConstructor
public class AdminNoticeController implements AdminNoticeSpecification {

    private final AdminNoticeService noticeService;

    @Override
    @PostMapping
    public ResponseEntity<Void> createNotice(
            @LoginUser Long userId,
            @Valid @RequestBody AdminCreateNoticeRequest request) {

        AdminNoticeCreateCommand command = AdminCreateNoticeRequest.toCommand(userId, request.title(), request.content());

        noticeService.createNotice(command);

        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{noticeId}")
    public ResponseEntity<Void> updateNotice(
            @LoginUser Long userId,
            @PathVariable Long noticeId,
            @Valid @RequestBody AdminUpdateNoticeRequest request
    ) {
        AdminNoticeUpdateCommand command = AdminNoticeUpdateCommand.toCommand(userId, noticeId, request.title(), request.content());
        noticeService.updateNotice(command);

        return ResponseEntity.noContent().build();
    }

    @Override
    @DeleteMapping("/{noticeId}")
    public ResponseEntity<Void> deleteNotice(
            @PathVariable Long noticeId,
            @LoginUser Long userId
            ) {

        noticeService.deleteNotice(userId, noticeId);

        return ResponseEntity.noContent().build();
    }
}
