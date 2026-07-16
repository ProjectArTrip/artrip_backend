package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.constants.Role;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.curation.Curation;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.global.apipayload.code.error.CurationErrorCode;
import org.atdev.artrip.global.apipayload.code.error.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.code.error.UserErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.atdev.artrip.repository.CurationRepository;
import org.atdev.artrip.repository.ExhibitRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.command.AdminCurationCreateCommand;
import org.atdev.artrip.service.dto.command.AdminCurationSearchCommand;
import org.atdev.artrip.service.dto.command.AdminCurationUpdateCommand;
import org.atdev.artrip.service.dto.result.AdminCurationCreateResult;
import org.atdev.artrip.service.dto.result.AdminCurationDetailResult;
import org.atdev.artrip.service.dto.result.AdminCurationListResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCurationService {

    private final CurationRepository curationRepository;
    private final UserRepository userRepository;
    private final ExhibitRepository exhibitRepository;

    @Transactional(readOnly = true)
    public Page<AdminCurationListResult> list(Long adminId, AdminCurationSearchCommand command, Pageable pageable) {

        findAdminOrThrow(adminId);
        return curationRepository.searchForAdmin(command, pageable);
    }

    @Transactional(readOnly = true)
    public AdminCurationDetailResult detail(Long adminId, Long curationId) {
        findAdminOrThrow(adminId);

        Curation curation = curationRepository.findByIdWithExhibits(curationId, null).orElseThrow(() -> new GeneralException(CurationErrorCode._CURATION_NOT_FOUND));

        return AdminCurationDetailResult.from(curation);
    }

    @Transactional
    public AdminCurationCreateResult create(AdminCurationCreateCommand command) {
        findAdminOrThrow(command.adminId());

        Curation curation = Curation.of(
                command.title(),
                command.refreshCycle(),
                command.visibleFrom(),
                command.visibleTo(),
                command.active(),
                command.sortType()
        );
        curation.replaceExhibits(resolveExhibits(command.exhibitIds()));
        curationRepository.save(curation);

        return AdminCurationCreateResult.from(curation.getCurationId());
    }

    @Transactional
    public void update(AdminCurationUpdateCommand command) {
        findAdminOrThrow(command.adminId());

        Curation curation = curationRepository.findById(command.curationId()).orElseThrow(() -> new GeneralException(CurationErrorCode._CURATION_NOT_FOUND));

        curation.updateInfo(
                command.title(),
                command.refreshCycle(),
                command.visibleFrom(),
                command.visibleTo(),
                command.active(),
                command.sortType()
        );

        curation.replaceExhibits(resolveExhibits(command.exhibitIds()));
    }

    @Transactional
    public void delete(Long adminId, Long curationId) {
        findAdminOrThrow(adminId);

        Curation curation = curationRepository.findById(curationId).orElseThrow(() -> new GeneralException(CurationErrorCode._CURATION_NOT_FOUND));

        curationRepository.delete(curation);
    }

    private List<Exhibit> resolveExhibits(List<Long> exhibitIds) {
        if (exhibitIds.isEmpty()) return List.of();

        Map<Long, Exhibit> exhibitById = exhibitRepository.findAllById(exhibitIds).stream()
                .collect(Collectors.toMap(Exhibit::getExhibitId, e -> e));

        if (exhibitById.size() != new HashSet<>(exhibitIds).size()) {
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND);
        }

        return exhibitIds.stream().map(exhibitById::get).toList();
    }

    private User findAdminOrThrow(Long adminId) {
        User user = userRepository.findById(adminId).orElseThrow(() -> new GeneralException(UserErrorCode._USER_NOT_FOUND));
        if (user.getRole() != Role.ADMIN) {
            throw new GeneralException(UserErrorCode._USER_FORBIDDEN);
        }

        return user;
    }
}
