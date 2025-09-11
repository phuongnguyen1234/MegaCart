package com.megacart.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Bật annotation @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ExceptionHandlingFilter exceptionHandlingFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Kích hoạt và cấu hình CORS.
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",
                                // Các API công khai cho tất cả mọi người
                                "/api/san-pham/**",
                                "/api/danh-muc/**",
                                "/api/filter-options/**",
                                "/", "/index.html",
                                "/favicon.ico",
                                "/css/**", "/js/**", "/assets/**", "/static/**"
                        ).permitAll()
                        // Chỉ ADMIN và NHAN_VIEN mới được truy cập vào các API quản trị
                        .requestMatchers("/api/admin/**").hasAnyAuthority("ADMIN", "NHAN_VIEN")
                        // Các API còn lại (ví dụ: giỏ hàng, đặt hàng) yêu cầu phải đăng nhập
                        // (Spring Security sẽ tự kiểm tra, nhưng việc khai báo rõ ràng giúp dễ đọc hơn)
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // **QUAN TRỌNG**: Thêm bộ lọc xử lý exception vào đầu chuỗi
                .addFilterBefore(exceptionHandlingFilter, JwtAuthFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // **QUAN TRỌNG**: URL của frontend
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173", "https://megacart-1fwi.onrender.com"));
        // Các phương thức cho phép
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        // Cho phép tất cả các header
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Cho phép gửi cookie và header Authorization
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Áp dụng cho tất cả các endpoint bắt đầu bằng /api/
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
