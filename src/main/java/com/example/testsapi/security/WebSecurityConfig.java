package com.example.testsapi.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers("/test/**").permitAll()
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/emails/**").permitAll()
                .requestMatchers("/users/**").permitAll()
                .requestMatchers("/api/users/**").permitAll()
                .requestMatchers("/security-questions/**", "/security_questions/**").permitAll()
                .requestMatchers("/api/questions/**").permitAll()
                .requestMatchers("/api/objectives/**").permitAll()
                .requestMatchers("/api/secondary-objectives/**").permitAll()
                .requestMatchers("/api/time-length/**").permitAll()
                .requestMatchers("/api/response/**").permitAll()
                .requestMatchers("/api/responses/**").permitAll()
                .requestMatchers("/api/source_code/**").permitAll()
                .requestMatchers("/session/validate/**").permitAll()
                .requestMatchers("/question_code_link/**").permitAll()
                .requestMatchers("/feedback/**").permitAll()
                .requestMatchers("/tests/**").permitAll()
                .requestMatchers("/testResolve/**").permitAll()
                .requestMatchers("/feedbackTest/**").permitAll()
                .requestMatchers("/api/question_image/**").permitAll()
                .anyRequest().authenticated();

        http.authenticationProvider(authenticationProvider(null, null));
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
