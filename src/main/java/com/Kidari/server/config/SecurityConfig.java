package com.Kidari.server.config;
import com.Kidari.server.config.jwt.JwtFilter;
import com.Kidari.server.config.jwt.JwtUtils;
import com.Kidari.server.config.oauth2.CustomOAuth2UserService;
import com.Kidari.server.config.oauth2.OAuth2SuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final OAuth2SuccessHandler successHandler;
    private final CustomOAuth2UserService customOAuth2UserService;

    private String[] permitList = {
            "/login/oauth2/code/github",
            "/api/auth/**",
            "/api/auth/*",
            "/api/auth/giver/login",
            "/api/auth/receiver/signin",
            "/api/auth/receiver/login",
            "/redisTest",
            "/redisTest/*",
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> {
                    auth
                            .requestMatchers(permitList).permitAll()
                            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                            .anyRequest().hasAnyAuthority("ROLE_MEMBER");
                })
                // jwtFilter를 UsernamePasswordAuthenticationFilter 앞에 추가
                .addFilterBefore(new JwtFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .oauth2Login((login) -> {
                    login
                            .redirectionEndpoint(redirectionEndpointConfig ->
                                    redirectionEndpointConfig
                                            .baseUri("/login/oauth2/code/github")
                            )
                            .successHandler(successHandler) // Oauth 2.0 로그인 성공 시의 핸들러
                            .userInfoEndpoint(endpoint ->
                                    endpoint
                                            .userService(customOAuth2UserService) // SecurityContext 설정
                            );
                })
                .logout(log -> log
                        .logoutUrl("/logout")
//                        .logoutSuccessHandler(new CustomLogoutSuccessHandler())
                )


        ;


        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOrigin("http://도메인:포트");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
