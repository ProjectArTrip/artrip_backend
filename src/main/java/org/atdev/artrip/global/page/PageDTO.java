package org.atdev.artrip.global.page;

import lombok.*;
import org.springframework.data.domain.Page;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDTO {

    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalItems;

    private int pageStart;
    private int pageEnd;
    private boolean next, prev;
    private int total;
    private boolean first, last;

    public static PageDTO from(Page<?> page) {
        int currentPage = page.getNumber() + 1;
        int totalPages = page.getTotalPages();

        int pageEnd = (int) (Math.ceil(currentPage / 10.0)) * 10;
        int pageStart = pageEnd - 9;

        if (pageEnd > totalPages) {
            pageEnd = totalPages;
        }

        return PageDTO.builder()
                .currentPage(currentPage)
                .pageSize(page.getSize())
                .totalPages(totalPages)
                .totalItems(page.getTotalElements())
                .pageStart(pageStart)
                .pageEnd(pageEnd)
                .next(pageEnd < totalPages)
                .prev(pageStart > 1)
                .first(page.isFirst())
                .last(page.isLast())
                .build();
    }

    public static PageDTO from(Criteria cri, long total) {
        int totalPages = (int) Math.ceil(total * 1.0 / cri.getAmount());
        int currentPage = cri.getPageNum();

        int pageEnd = (int) (Math.ceil(currentPage / 10.0)) * 10;
        int pageStart = pageEnd - 9;

        if (pageEnd > totalPages) {
            pageEnd = totalPages;
        }

        return PageDTO.builder()
                .currentPage(currentPage)
                .pageSize(cri.getAmount())
                .totalPages(totalPages)
                .totalItems(total)
                .pageStart(pageStart)
                .pageEnd(pageEnd)
                .next(pageEnd < totalPages)
                .prev(pageStart > 1)
                .first(currentPage == 1)
                .last(currentPage == totalPages)
                .build();
    }
}
