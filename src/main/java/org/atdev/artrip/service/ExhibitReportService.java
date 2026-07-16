package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibitReport.ExhibitReport;
import org.atdev.artrip.domain.exhibitReport.ExhibitReportCreatedEvent;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.ExhibitReportRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.ExhibitReportCommand;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExhibitReportService {

    private final ExhibitReportRepository exhibitReportRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Long create(Long userId, ExhibitReportCommand command) {
        User user = userRepository.findById(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        ExhibitReport report = ExhibitReport.create(user, command.title(), command.country());
        exhibitReportRepository.save(report);

        eventPublisher.publishEvent(new ExhibitReportCreatedEvent(
                report.getExhibitReportId(),
                user.getUserId(),
                report.getTitle()
        ));

        return report.getExhibitReportId();
    }

}
