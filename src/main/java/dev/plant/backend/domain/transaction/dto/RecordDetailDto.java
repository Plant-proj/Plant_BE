package dev.plant.backend.domain.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RecordDetailDto {

    private Long id;
    private String type;
    private Integer amount;
    private String category;
    private String memo;
    private Long date; // 유닉스 타임스탬프

}
