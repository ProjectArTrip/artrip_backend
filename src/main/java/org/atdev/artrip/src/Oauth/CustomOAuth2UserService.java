package org.atdev.artrip.src.Oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {//DefaultOAuth2UserService<< OAuth2 로그인시 사용자 정보 가져옴
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();// 구글 카카오 네이버 등 자동식별
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        String name = userInfo.getName();
        String email = userInfo.getEmail();

        if (email == null || email.isBlank()) {
            email = registrationId + "_" + UUID.randomUUID() + "@social.com";//이메일은 배포할때 비즈앱으로 전환하면 받을거임
        }                                                                    // 그전까진 임의 생성

        saveOrUpdateUser(email,name,registrationId);

        Map<String, Object> attributes = new HashMap<>(userInfo.getAttributes());
        attributes.put("email", email);

        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                attributes,
                "email"
        );
    }

    private Member saveOrUpdateUser(String email, String nickname, String registrationId) {
        Member member = memberRepository.findByEmail(email)
                .orElse(Member.builder()
                        .email(email)
                        .name(nickname)
                        .password(passwordEncoder.encode("OAUTH_USER_" + UUID.randomUUID()))
                        .gender(Gender.NONE)  // 기본값 설정
                        .address("소셜로그인")  // 기본값 설정
                        .specAddress("소셜로그인")  // 기본값 설정
                        .role(Role.USER)
                        .build());
        if (registrationId.equals("kakao")) {
            // 카카오 특화 처리
        } else if (registrationId.equals("google")) {
            // 구글 특화 처리
        } else if (registrationId.equals("naver")) {
            // 네이버 특화 처리
        }


        return memberRepository.save(member);
    }



}
