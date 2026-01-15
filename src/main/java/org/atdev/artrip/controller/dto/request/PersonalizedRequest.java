package org.atdev.artrip.controller.dto.request;

import lombok.*;
import org.atdev.artrip.service.dto.RandomQuery;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class PersonalizedRequest extends BaseRandomRequest {

    public RandomQuery toQuery(Long userId, ImageResizeRequest resize) {
        return createBaseQueryBuilder(userId, resize)
                .build();
    }
}
