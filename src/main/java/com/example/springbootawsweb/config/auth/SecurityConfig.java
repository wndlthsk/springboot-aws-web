package com.example.springbootawsweb.config.auth;

import com.example.springbootawsweb.domain.user.Role;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    private static final String[] PERMIT_ALL_PATTERNS = new String[]{
        "/",
        "/css/**",
        "/image/**",
        "/js/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // h2-console 화면 사용을 위한 옵션
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(
                frameOptionsConfig -> frameOptionsConfig.disable()));

        // url별 권한 관리 설정 옵션
        http.authorizeHttpRequests(authorize -> authorize
            .requestMatchers(PathRequest.toH2Console()).permitAll()
            .requestMatchers(Stream.of(PERMIT_ALL_PATTERNS).map(AntPathRequestMatcher::antMatcher)
                .toArray(AntPathRequestMatcher[]::new)).permitAll()
            .requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/**"))
            .hasRole(Role.USER.name())
            .anyRequest().authenticated());

        // logout 성공시 주소 이동
        http.logout(logout -> logout.logoutSuccessUrl("/"));

        // longin 후 사용자 정보 가져올 때 설정
        http.oauth2Login(oauth2Login -> oauth2Login
            .userInfoEndpoint(
                userInfoEndpoint -> userInfoEndpoint.userService(customOAuth2UserService)));

        return http.build();
    }

//    protected void configure(HttpSecurity http) throws Exception {
//        http
//            .csrf().disable()
//            .headers().frameOptions().disable()
//            .and()
//            .authorizeRequests()
//            .antMatchers("/", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile")
//            .permitAll()
//            .antMatchers("/api/v1/**").hasRole(Role.USER.name())
//            .anyRequest().authenticated()
//            .and()
//            .logout()
//            .logoutSuccessUrl("/")
//            .and()
//            .oauth2Login()
//            .userInfoEndpoint()
//            .userService(customOAuth2UserService);
//    }
}