package dev.plant.backend.domain.transaction.dto;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class AddRecordRequest {
   private String type;
   private String category;
   private String title;
   private Integer amount;
   private String memo;
   private Long date; //유닉스 타임스탬프
}
