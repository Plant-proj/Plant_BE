package dev.plant.backend.domain.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DailyRecordResponse {

    private Long date; // 유닉스 타임스탬프 (년월일)
    private Integer totalBalance;
    private List<RecordDetailDto> dailyRecords;

}
