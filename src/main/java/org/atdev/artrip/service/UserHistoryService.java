package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.auth.User;
import org.atdev.artrip.domain.exhibit.Exhibit;
import org.atdev.artrip.domain.exhibit.RecentExhibit;
import org.atdev.artrip.repository.RecentExhibitRepository;
import org.atdev.artrip.repository.UserRepository;
import org.atdev.artrip.service.dto.result.ExhibitRecentResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserHistoryService {

    private final RecentExhibitRepository recentExhibitRepository;
    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void addRecentView(Long userId, Exhibit exhibit) {
        User user = userRepository.getReferenceById(userId);

        recentExhibitRepository.findByUserAndExhibit(user, exhibit)
                .ifPresentOrElse(
                        RecentExhibit::updateViewAt,
                        () -> recentExhibitRepository.save(new RecentExhibit(user, exhibit))
                );
    }

    @Transactional(readOnly = true)
    public List<ExhibitRecentResult> getRecentViews(Long userId) {
        User user = userRepository.getReferenceById(userId);

        List<RecentExhibit> histories = recentExhibitRepository.findTop20ByUserOrderByViewAtDesc(user);

        return histories.stream()
                .map(history -> ExhibitRecentResult.from(history.getExhibit()))
                .toList();
    }


}