package org.atdev.artrip.domain.auth.Oauth;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.domain.Enum.Provider;
import org.atdev.artrip.domain.Enum.Role;
import org.atdev.artrip.domain.SocialAccounts;
import org.atdev.artrip.domain.auth.data.User;
import org.atdev.artrip.domain.auth.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Provider provider = Provider.valueOf(registrationId.toUpperCase());

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        String providerId = userInfo.getId();

        User user = saveOrUpdateUser(provider, providerId,userInfo);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())),
                userInfo.getAttributes(),
                userInfo.getNameAttributeKey()
        );
    }


    @Transactional
    protected User saveOrUpdateUser(Provider provider, String providerId, OAuth2UserInfo userInfo) {

        Optional<User> userOpt = userRepository.findBySocialAccountsProviderAndProviderId(provider, providerId);

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            boolean isUpdated = user.updateUserInfo(userInfo.getName(), userInfo.getEmail());
            if (isUpdated) {
                return userRepository.save(user);
            }
            return user;
        }

        User newUser = User.builder()
                .name(userInfo.getName())
                .email(userInfo.getEmail())
                .role(Role.USER)
                .build();

        SocialAccounts social = SocialAccounts.builder()
                .user(newUser)
                .provider(provider)
                .providerId(providerId)
                .build();

        newUser.getSocialAccounts().add(social);
        return userRepository.save(newUser);
    }
}
