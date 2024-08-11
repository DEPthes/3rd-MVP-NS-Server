package depth.mvp.ns.global.config.security;

import depth.mvp.ns.global.config.security.handler.JwtAuthenticationEntryPoint;
import depth.mvp.ns.global.config.security.handler.JwtAccessDeniedHandler;
import depth.mvp.ns.global.config.security.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        // 인증 실패 시 처리할 엔트리 포인트
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        // 인가 실패 시 처리할 핸들러
                        .accessDeniedHandler(new JwtAccessDeniedHandler())
                )
                .authorizeHttpRequests(authorize -> authorize
                        // 특정 GET 요청만 인증 없이 접근 가능
                        .requestMatchers(HttpMethod.GET, "/api/v1/board/{boardId}").permitAll()
                        .requestMatchers("/api/v1/board/**").authenticated()
                        .requestMatchers(
                                antMatcher("/"),
                                antMatcher("/error"),
                                antMatcher("/favicon.ico"),
                                antMatcher("/swagger"),
                                antMatcher("/swagger-ui.html"),
                                antMatcher("/swagger-ui/**"),
                                antMatcher("/api-docs"),
                                antMatcher("/api-docs/**"),
                                antMatcher("/v3/api-docs/**"),
                                antMatcher("/auth/**"),
                                antMatcher("/api/v1/report/generate"),
                                antMatcher("/api/v1/theme/**"))
                        .permitAll()
                        .anyRequest()
                        .authenticated());

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
