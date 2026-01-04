package org.atdev.artrip.global.page;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Getter
@Setter
@ToString
@Schema(description = "페이징 및 정렬, 검색 조건 DTO")
public class Criteria {

    @Schema(description = "페이지 번호", example = "1")
    private int pageNum;

    @Schema(description = "페이지 당 항목 수", example = "20")
    private int amount;

    @Schema(description = "검색 필드", example = "")
    private String searchValue;

    @Schema(description = "정렬 필드", example = "createdAt")
    private String sortField;

    @Schema(description = "정렬 방향 (ASC 또는 DESC)", example = "DESC")
    private String sortDirection;

    public Criteria(){
        this(1, 10);
    }

    public Criteria(int pageNum, int amount){
        this.pageNum = pageNum;
        this.amount = amount;
        this.sortField = "createdAt";
        this.sortDirection = "DESC";
    }

    public Pageable toPageable(){
        Sort sort = Sort.by(
                "DESC".equalsIgnoreCase(sortDirection) ? Sort.Direction.DESC : Sort.Direction.ASC , sortField);

        int validPageNum = Math.max(1, this.pageNum);
        int validAmount = Math.max(1, this.amount);

        return PageRequest.of(validPageNum - 1 , validAmount, sort);
    }

}
