package dev.plant.backend.domain.oauth.controller;

import dev.plant.backend.domain.oauth.service.KakaoService;
import dev.plant.backend.domain.oauth.dto.KakaoUserInfoResponseDto;
import dev.plant.backend.domain.user.dto.LoginResponseDto;
import dev.plant.backend.domain.user.service.UserService;
import dev.plant.backend.global.response.ApiResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/users", produces = "application/json; charset=UTF8")
public class KakaoAuthController {

    private final KakaoService kakaoService;
    private final UserService userService;

    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/login")
    public ResponseEntity<Void> redirectToKakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + client_id
                + "&redirect_uri=" + redirect_uri
                + "&response_type=code";

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(kakaoAuthUrl));

        return ResponseEntity.status(302).headers(headers).build();
    }

    @GetMapping("/callback")
    public ResponseEntity<ApiResponse<LoginResponseDto>> callback(
            @RequestParam("code") String code,
            HttpSession session
    ) {
        String accessToken = kakaoService.getAccessTokenFromKakao(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

        LoginResponseDto loginResponse = userService.loginWithKakao(userInfo);

        session.setAttribute("kakaoId", userInfo.getId());

        String message = loginResponse.isNewUser()
                ? "신규 유저입니다. 잔액 정보를 먼저 입력해주세요."
                : "로그인이 완료되었습니다.";

        HttpStatus status = loginResponse.isNewUser() ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.ok(ApiResponse.of(status.value(), message, loginResponse));
    }

}
