package dev.plant.backend.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponseDto {

    @JsonProperty("isNewUser")
    private boolean newUser;

    private String nickname;

    private String profileImage;

    private Integer accountBalance;

}