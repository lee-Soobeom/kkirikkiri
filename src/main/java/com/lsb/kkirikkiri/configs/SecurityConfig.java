package com.lsb.kkirikkiri.configs;

import com.lsb.kkirikkiri.configs.oauth2.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login")
                        .successHandler((request, response, authentication) -> {
                            com.lsb.kkirikkiri.entities.user.UserEntity user = (com.lsb.kkirikkiri.entities.user.UserEntity) request.getSession().getAttribute("sessionUser");

                            boolean isMissingInfo = (user != null && (
                                    user.getName() == null ||
                                            user.getAddressPrimary() == null ||
                                            user.getContact() == null
                            ));

                            if (isMissingInfo) {
                                request.getSession().setAttribute("needsAdditionalInfo", true);
                                response.sendRedirect("/?mode=social_reg");
                            } else {
                                response.sendRedirect("/");
                            }
                        })
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                );
        return http.build();
    }

}
