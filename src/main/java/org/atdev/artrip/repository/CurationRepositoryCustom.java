package org.atdev.artrip.repository;

import org.atdev.artrip.service.dto.command.AdminCurationSearchCommand;
import org.atdev.artrip.service.dto.result.AdminCurationListResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface CurationRepositoryCustom {

    Page<AdminCurationListResult> searchForAdmin(AdminCurationSearchCommand command, Pageable pageable);
}
