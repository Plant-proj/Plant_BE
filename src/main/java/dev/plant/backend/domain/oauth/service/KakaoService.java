package dev.plant.backend.domain.oauth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.plant.backend.domain.oauth.dto.KakaoTokenResponseDto;
import dev.plant.backend.domain.oauth.dto.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class KakaoService {

    private final WebClient kakaoAuthWebClient;
    private final WebClient kakaoApiWebClient;

    @Value("${kakao.client_id}")
    private String clientId;

    public String getAccessTokenFromKakao(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = kakaoAuthWebClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", clientId)
                        .queryParam("code", code)
                        .build(true))
                .retrieve()
                .bodyToMono(KakaoTokenResponseDto.class)
                .block();

        log.info("[KakaoService] access_token = {}", kakaoTokenResponseDto.getAccessToken());
        log.info("[KakaoService] refresh_token = {}", kakaoTokenResponseDto.getRefreshToken());

        log.info("[KakaoService] scope = {}", kakaoTokenResponseDto.getScope());

        return Objects.requireNonNull(kakaoTokenResponseDto).getAccessToken();
    }

    public KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = kakaoApiWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .path("/v2/user/me")
                        .build(true))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(KakaoUserInfoResponseDto.class)
                .block();

        log.info("[KakaoService] Auth ID = {}", userInfo.getId());
        log.info("[KakaoService] NickName = {}", userInfo.getKakaoAccount().getProfile().getNickName());
        log.info("[KakaoService] ProfileImageUrl = {}", userInfo.getKakaoAccount().getProfile().getProfileImageUrl());

        return userInfo;
    }

    //로그아웃
    public void kakaoDisconnect(String accessToken) throws JsonProcessingException {
        String response = WebClient.create("https://kapi.kakao.com")
                .post()
                .uri("/v1/user/logout")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        log.info("[KakaoService] logout response = {}", response);
    }

    //회원 탈퇴
    public void unlinkKakaoAccount(String accessToken) {
        WebClient.create("https://kapi.kakao.com")
                .post()
                .uri("/v1/user/unlink")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response -> log.info("[KakaoService] unlink response = {}", response))
                .doOnError(e -> {
                    throw new RuntimeException("카카오 계정 연결 실패", e); //카카오 계정 연결 실패시 에러
                })
                .block();
    }

}
