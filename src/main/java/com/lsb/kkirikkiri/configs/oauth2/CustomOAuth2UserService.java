package com.lsb.kkirikkiri.configs.oauth2;

import com.lsb.kkirikkiri.entities.user.UserEntity;
import com.lsb.kkirikkiri.mappers.UserMapper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserMapper userMapper;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo;
        if ("kakao".equals(registrationId)) {
            userInfo = new KakaoOAuth2UserInfo(attributes);
        } else if ("naver".equals(registrationId)) {
            userInfo = new NaverOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인: " + registrationId);
        }

        UserEntity user = userMapper.findBySocialId(registrationId.toUpperCase(), userInfo.getSocialId());

        if (user == null) {
            user = userMapper.findByEmail(userInfo.getEmail());

            if (user != null) {
                user.setSocialId(userInfo.getSocialId());
                user.setSocialTypeCode(registrationId.toUpperCase());
                userMapper.updateSocialInfo(user);
            } else  {
                user = UserEntity.builder()
                        .email(userInfo.getEmail())
                        .nickname(userInfo.getNickname())
                        .profileImagePath(userInfo.getProfileImage())
                        .socialTypeCode(registrationId.toUpperCase())
                        .socialId(userInfo.getSocialId())
                        .status("GENERAL")
                        .build();

                userMapper.insertSocialUser(user);
            }
        }

        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attr.getRequest().getSession(true);
        session.setAttribute("sessionUser", user);

        String nameAttributeKey = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                nameAttributeKey
        );
    }
}
