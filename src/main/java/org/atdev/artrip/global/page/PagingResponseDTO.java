package org.atdev.artrip.global.page;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponseDTO<T> {

    private List<T> data;
    private PageDTO pageInfo;

    public static <T> PagingResponseDTO<T> from(Page<T> page) {
        return PagingResponseDTO.<T>builder()
                .data(page.getContent())
                .pageInfo(PageDTO.from(page))
                .build();
    }

    public static <T> PagingResponseDTO<T> of(Page<?> page, List<T> data) {
        return PagingResponseDTO.<T>builder()
                .data(data)
                .pageInfo(PageDTO.from(page))
                .build();
    }
}
