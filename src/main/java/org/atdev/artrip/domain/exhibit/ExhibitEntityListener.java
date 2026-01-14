package org.atdev.artrip.domain.exhibit;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.atdev.artrip.elastic.service.ExhibitIndexService;
import org.atdev.artrip.global.apipayload.code.status.ExhibitErrorCode;
import org.atdev.artrip.global.apipayload.exception.GeneralException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExhibitEntityListener {

    private static ApplicationContext context;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        context = applicationContext;
    }

    @PostPersist
    public void onPostPersist(Exhibit exhibit) {
        log.info("ExhibitEntityListener - ID={}, Title={}",
                exhibit.getExhibitId(), exhibit.getTitle());

        try {
            ExhibitIndexService indexService = context.getBean(ExhibitIndexService.class);
            indexService.indexExhibit(exhibit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND);
        }
    }

    @PostUpdate
    public void onPostUpdate(Exhibit exhibit) {
        log.info("Exhibit DB Update - ID={}, Title={}",
                exhibit.getExhibitId(), exhibit.getTitle());

        try {
            ExhibitIndexService indexService = context.getBean(ExhibitIndexService.class);
            indexService.indexExhibit(exhibit);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND);
        }
    }

    @PostRemove
    public void onPostRemove(Exhibit exhibit) {
        log.info("Exhibit DB Delete - ID={}, Title={}",
                exhibit.getExhibitId(), exhibit.getTitle());

        try {
            ExhibitIndexService indexService = context.getBean(ExhibitIndexService.class);
            indexService.deleteExhibit(exhibit.getExhibitId());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(ExhibitErrorCode._EXHIBIT_NOT_FOUND);
        }
    }
}
