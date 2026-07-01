package org.atdev.artrip.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.atdev.artrip.controller.dto.request.AdminCreateNoticeRequest;
import org.atdev.artrip.controller.dto.request.AdminUpdateNoticeRequest;
import org.atdev.artrip.controller.dto.response.AdminNoticeCreateResponse;
import org.atdev.artrip.controller.dto.response.AdminNoticeListResponse;
import org.atdev.artrip.controller.spec.AdminNoticeSpecification;
import org.atdev.artrip.global.resolver.LoginUser;
import org.atdev.artrip.service.AdminNoticeService;
import org.atdev.artrip.service.dto.command.AdminNoticeCreateCommand;
import org.atdev.artrip.service.dto.command.AdminNoticeUpdateCommand;
import org.atdev.artrip.service.dto.result.AdminNoticeCreateResult;
import org.atdev.artrip.utils.page.PageQuery;
import org.atdev.artrip.utils.page.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController implements AdminNoticeSpecification {

    private final AdminNoticeService noticeService;

    @Override
    @GetMapping
    public ResponseEntity<PageResponse<AdminNoticeListResponse>> listNotices(
            @LoginUser Long adminId,
            PageQuery pageQuery
    ) {
        Page<AdminNoticeListResponse> page = noticeService.listNotices(pageQuery.toPageable());
        return ResponseEntity.ok(PageResponse.from(page));
    }

    @Override
    @PostMapping
    public ResponseEntity<AdminNoticeCreateResponse> createNotice(
            @LoginUser Long userId,
            @Valid @RequestBody AdminCreateNoticeRequest request) {

        AdminNoticeCreateCommand command = AdminCreateNoticeRequest.toCommand(userId, request.title(), request.content());

        AdminNoticeCreateResult result = noticeService.createNotice(command);
        AdminNoticeCreateResponse response = AdminNoticeCreateResponse.from(result);

        return ResponseEntity.ok(response);
    }

    @Override
    @RequestMapping(value = "/{noticeId}", method = {RequestMethod.PATCH, RequestMethod.PUT})
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
