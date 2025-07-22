package dev.plant.backend.domain.user.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@Builder
public class UserBalanceRequest {
    private Integer accountBalance;
}
