package dev.plant.backend.domain.user.controller;

import dev.plant.backend.domain.user.dto.*;
import dev.plant.backend.domain.user.service.UserService;
import dev.plant.backend.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/users",produces = "application/json; charset=UTF8")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PatchMapping("/balance")
    public ResponseEntity<ApiResponse<UserBalanceResponse>> updateBalance(HttpSession session, @Valid @RequestBody UserBalanceRequest userBalanceRequest) {
        UserBalanceResponse userBalanceResponse = userService.changeAccountBalance(session, userBalanceRequest);
        return ResponseEntity.ok(ApiResponse.ok("총 잔액이 성공적으로 설정되었습니다", userBalanceResponse));
    }
}
