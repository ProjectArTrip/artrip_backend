package org.atdev.artrip.utils.page;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Schema(description = "페이징 및 정렬, 검색 조건 DTO")
public record PageQuery(

        @Schema(description = "0-based 페이지", example = "0")
        Integer page,

        @Schema(description = "페이지 크기", example = "20")

        Integer size,

        @Schema(description = "정렬 필드", example = "createdAt")
        String sort,

        @Schema(description = "ASC|DESC", example = "DESC")
        String direction
) {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;
    private static final String DEFAULT_SORT = "createdAt";

    public Pageable toPageable() {
        int safePage = Math.max(0, page == null ? DEFAULT_PAGE : page);
        int rawSize = size == null ? DEFAULT_SIZE : size;
        int safeSize = Math.min(MAX_SIZE, Math.max(1, rawSize));
        String safeSort = (sort == null || sort.isBlank()) ? DEFAULT_SORT : sort;
        Sort.Direction safeDir = "ASC".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        return PageRequest.of(safePage, safeSize, Sort.by(safeDir, safeSort));
    }


}
