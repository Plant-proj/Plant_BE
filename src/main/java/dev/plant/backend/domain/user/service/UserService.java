package dev.plant.backend.domain.user.service;

import dev.plant.backend.domain.user.domain.User;
import dev.plant.backend.domain.oauth.dto.KakaoUserInfoResponseDto;
import dev.plant.backend.domain.user.dto.LoginResponseDto;
import dev.plant.backend.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
