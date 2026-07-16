package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.ExhibitReportRepository;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.result.AdminExhibitReportResult;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminExhibitReportService {

    private final ExhibitReportRepository exhibitReportRepository;
    private final ExhibitRepository exhibitRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public Page<AdminExhibitReportResult> list(Long adminId, Pageable pageable) {
        User user = userRepository.findById(adminId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }

        return exhibitReportRepository.findAll(pageable).map(AdminExhibitReportResult::from);
    }
}
