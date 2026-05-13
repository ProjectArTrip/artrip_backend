package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.MaintenanceState;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.maintenance.Maintenance;
import org.atdev.artrip.global.apipayload.code.error.MaintenanceErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.MaintenanceRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.AdminMaintenanceUpsertCommand;
import org.atdev.artrip.service.dto.result.MaintenanceStatusResult;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MaintenanceStatusResult getMaintenanceStatus() {
        Optional<Maintenance> maintenance = maintenanceRepository.findLatest(PageRequest.of(0, 1))
                .stream()
                .findFirst();

        if (maintenance.isEmpty()) {
            return MaintenanceStatusResult.empty();
        }
        return MaintenanceStatusResult.from(maintenance.get(), LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public MaintenanceStatusResult getAdminMaintenanceStatus(Long userId) {
        User admin = userRepository.findById(userId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        if (admin.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }

        Optional<Maintenance> maintenance = maintenanceRepository.findLatest(PageRequest.of(0, 1))
                .stream()
                .findFirst();

        if (maintenance.isEmpty()) {
            return MaintenanceStatusResult.empty();
        }
        return MaintenanceStatusResult.from(maintenance.get(), LocalDateTime.now());
    }

    @Transactional
    public MaintenanceStatusResult upsertMaintenance(AdminMaintenanceUpsertCommand command) {
        User admin = userRepository.findById(command.userId()).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));

        if (admin.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }

        command.validate();

        Optional<Maintenance> latestMaintenance = maintenanceRepository.findLatest(PageRequest.of(0, 1)).stream().findFirst();

        Maintenance maintenance = Maintenance.createOrUpdate(
                latestMaintenance,
                command.state(),
                command.title(),
                command.message(),
                command.startAt(),
                command.endAt(),
                command.buttonText(),
                command.forceExit(),
                command.refreshAfterSeconds()
        );

        if (latestMaintenance.isEmpty()) {
            maintenanceRepository.save(maintenance);
        }

        return MaintenanceStatusResult.from(maintenance, LocalDateTime.now());
    }
}
