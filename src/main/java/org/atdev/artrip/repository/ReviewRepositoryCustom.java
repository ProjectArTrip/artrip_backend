package org.atdev.artrip.repository;

import org.atdev.artrip.service.dto.command.AdminReviewSearchCommand;
import org.atdev.artrip.service.dto.result.AdminReviewResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewRepositoryCustom {
    Page<AdminReviewResult> searchForAdmin(AdminReviewSearchCommand command, Pageable pageable);
}
