package com.example.springbootawsweb.config.auth;

import com.example.springbootawsweb.config.auth.dto.OAuthAttributes;
import com.example.springbootawsweb.config.auth.dto.SessionUser;
import com.example.springbootawsweb.domain.user.User;
import com.example.springbootawsweb.domain.user.UserRepository;
import jakarta.servlet.http.HttpSession;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // 현재 로그인 진행 중인 서비스 구분하는 코드
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        // OAuth2 로그인 시 키가 되는 필드 값을 의미(==pk), 구글 기본 코드:sub, 네이버/카카오는 기본 지원x
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
            .getUserInfoEndpoint().getUserNameAttributeName();

        // OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담을 클래스, 다른 소셜 로그인도 이 클래스를 사용
        OAuthAttributes attributes = OAuthAttributes.of(
            registrationId, userNameAttributeName, oAuth2User.getAttributes()
        );

        User user = saveOrUpdate(attributes);

        // SessionUser: 세션에 사용자 정보를 저장하기 위한 DTO
        // User 클래스를 사용하지 않는 이유 :
        // User 클래스를 세션에 저장하려고 하면 User 클래스에 직렬화를 구현하지 않았다는 에러가 발생.
        // User 클래스에 직렬화 코드를 넣는다?
        // 엔티티이기 때문에 언제 다른 엔티티와 관계가 형성될지 모른다.
        // ex) @OneToMany, @ManyToMany 등 자식 엔티티가 있는 경우 직렬화 대상에 자식들까지 포함되므로
        // 성능 이슈, 부수 효과가 발생할 확률이 높음
        // -> 직렬화 기능을 가진 세션 DTO를 추가로 만드는 것이 운영 및 유지보수에 좋다.
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(
            new SimpleGrantedAuthority(user.getRoleKey())),
            attributes.getAttributes(),
            attributes.getNameAttributeKey()
        );
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
            .map(entity -> entity.update(
                attributes.getName(),
                attributes.getPicture())
            )
            .orElse(attributes.toEntity());

        return userRepository.save(user);
    }

}
