package dev.plant.backend.domain.oauth.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.plant.backend.domain.oauth.service.KakaoService;
import dev.plant.backend.domain.oauth.dto.KakaoUserInfoResponseDto;
import dev.plant.backend.domain.user.dto.LoginResponseDto;
import dev.plant.backend.domain.user.service.UserService;
import dev.plant.backend.global.ErrorCode;
import dev.plant.backend.global.exception.ApiException;
import dev.plant.backend.global.response.ApiResponse;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

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
        session.setAttribute("accessToken", accessToken);

        String message = loginResponse.isNewUser()
                ? "신규 유저입니다. 잔액 정보를 먼저 입력해주세요."
                : "로그인이 완료되었습니다.";

        HttpStatus status = loginResponse.isNewUser() ? HttpStatus.CREATED : HttpStatus.OK;

        return ResponseEntity.ok(ApiResponse.of(status.value(), message, loginResponse));
    }

    @GetMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session, HttpServletResponse response) {
        String accessToken = (String) session.getAttribute("accessToken");
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                kakaoService.kakaoDisconnect(accessToken); // 카카오 서버 세션 무효화
            } catch (JsonProcessingException e) {
                throw new RuntimeException("카카오 로그아웃 실패", e);
            }
        } else {
            log.warn("accessToken 없음: 카카오 로그아웃은 수행되지 않음");
        }

        session.invalidate();
        deleteAccessCookie(response, "accessToken");
        deleteJsessionCookie(response, "JSESSIONID");

        return ResponseEntity.ok(ApiResponse.ok( "로그아웃이 완료되었습니다.", null));
    }


    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> delete(HttpSession session, HttpServletResponse response) {
        try {
            if (session == null || session.getAttribute("kakaoId") == null) {
                        throw  new ApiException((ErrorCode.NOT_FOUND_RESOURCE));
            }
            userService.withdraw(session);

            deleteAccessCookie(response, "accessToken");

            deleteJsessionCookie(response, "JSESSIONID");

            return ResponseEntity.ok(ApiResponse.ok("회원 탈퇴가 완료되었습니다", null));

        } catch (Exception e) {
            throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
    //Access Cookie 삭제
    private void deleteAccessCookie(HttpServletResponse response, String cookieName) {
        Cookie accessTokenCookie = new Cookie(cookieName, null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);
    }
    // JSESSIONID 쿠키 삭제
    private void deleteJsessionCookie(HttpServletResponse response, String jsessionId) {
        Cookie jsessionCookie = new Cookie(jsessionId, null);
        jsessionCookie.setMaxAge(0);
        jsessionCookie.setPath("/");
        response.addCookie(jsessionCookie);
    }

}
