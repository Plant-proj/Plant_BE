package dev.plant.backend.domain.user.service;

import dev.plant.backend.domain.oauth.service.KakaoService;
import dev.plant.backend.domain.user.domain.User;
import dev.plant.backend.domain.oauth.dto.KakaoUserInfoResponseDto;
import dev.plant.backend.domain.user.dto.*;
import dev.plant.backend.domain.user.exception.NotFoundUserException;
import dev.plant.backend.domain.user.repository.UserRepository;
import dev.plant.backend.global.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final KakaoService kakaoService;

    public LoginResponseDto loginWithKakao(KakaoUserInfoResponseDto kakaoUser) {
        Long kakaoId = kakaoUser.getId();
        String nickname = kakaoUser.getKakaoAccount().getProfile().getNickName();
        String profileImageUrl = kakaoUser.getKakaoAccount().getProfile().getProfileImageUrl();

        Optional<User> userOptional = userRepository.findByKakaoId(kakaoId);

        boolean isNewUser = false;
        Integer accountBalance = null;

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            accountBalance = user.getAccountBalance();
        } else {
            User newUser = User.builder()
                    .kakaoId(kakaoId)
                    .nickname(nickname)
                    .profileImage(profileImageUrl)
                    .build();
            userRepository.save(newUser);
            isNewUser = true;
        }

        return new LoginResponseDto(isNewUser, nickname, profileImageUrl, accountBalance);
    }
    //회원 탈퇴
    @Transactional
    public void withdraw(HttpSession session){

        String accessToken = (String) session.getAttribute("accessToken");
        Long kakaoId = getKakaoId(session);

        if (accessToken != null){
            kakaoService.unlinkKakaoAccount(accessToken);
        }
        //DB에서 회원 삭제
        userRepository.deleteByKakaoId(kakaoId);

        session.invalidate();
    }
    //계좌 잔액 수정 및 입력
    @Transactional
    public UserBalanceResponse changeAccountBalance(HttpSession session, UserBalanceRequest userBalanceRequest){

        Long kakaoId = getKakaoId(session);
        User user = getUser(kakaoId);

        Integer accountBalance = user.getAccountBalance();

        if(!userBalanceRequest.getAccountBalance().equals(accountBalance)) { //기존 값과 다르면 새로운 값 저장
            user.updateAccountBalance(userBalanceRequest.getAccountBalance());
        }
        return new UserBalanceResponse(user.getAccountBalance());
    }

    //Kakao ID Get
    public long getKakaoId(HttpSession session){
        Long kakaoId = (Long) session.getAttribute("kakaoId");
        return kakaoId;
    }
    //user Get
    public User getUser(Long kakaoId){
        User user = userRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));
        return user;
    }

}
