package dev.plant.backend.domain.transaction.dto;


import dev.plant.backend.domain.transaction.domain.Transaction;
import dev.plant.backend.domain.transaction.model.*;
import lombok.*;

import java.time.ZoneOffset;

@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)

public class UpdateRecordResponse {

    private Long id;
    private Type type;
    private Category category;
    private String title;
    private Integer amount;
    private String memo;
    private Long date;
    private Long createdAt;
    private Long updatedAt;

    public static UpdateRecordResponse of(Transaction transaction) {
        return UpdateRecordResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .title(transaction.getTitle())
                .amount(transaction.getAmount())
                .memo(transaction.getMemo())
                .date(transaction.getDate())
                .createdAt(transaction.getCreatedAt().toEpochSecond(ZoneOffset.UTC))
                .updatedAt(transaction.getUpdateAt().toEpochSecond(ZoneOffset.UTC))
                .build();

    }

}
