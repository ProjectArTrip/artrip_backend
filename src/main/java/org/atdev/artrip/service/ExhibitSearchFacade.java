package org.atdev.artrip.service;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.service.dto.command.ExhibitFilterCommand;
import org.atdev.artrip.service.dto.result.ExhibitFilterResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class ExhibitSearchFacade {

    private final HomeService homeService;
    private final SearchHistoryService searchHistoryService;

    public ExhibitFilterResult searchAndSaveHistory(ExhibitFilterCommand command) {

        ExhibitFilterResult result = homeService.getFilterExhibit(command);

        if (command.userId() != null && StringUtils.hasText(command.query())) {
            searchHistoryService.saveSearchHistory(command.userId(), command.query());
        }

        return result;
    }
}
