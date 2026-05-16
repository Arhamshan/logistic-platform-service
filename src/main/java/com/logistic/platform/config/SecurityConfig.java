package com.logistic.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/**").permitAll()        // ✅ Actuator endpoints
                        .requestMatchers("/assets/**").permitAll()          // ✅ Admin UI static files
                        .requestMatchers("/login", "/logout").permitAll()   // ✅ Admin UI login page
                        .requestMatchers("/instances", "/instances/**").permitAll()  // ✅ client registration
                        .requestMatchers("/*.css", "/*.js", "/*.ico").permitAll()
                        .requestMatchers("/v1/**").permitAll()        // ✅ Your API endpoints — fix 401
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")                                // ✅ Admin UI uses this
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .permitAll()
                )
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/actuator/**")            // ✅ Disable CSRF for actuator
                        .ignoringRequestMatchers("/instances")              // ✅ Admin client registration
                        .ignoringRequestMatchers("/instances/**")
                        .ignoringRequestMatchers("/v1/**")            // ✅ Disable CSRF for API
                )
                .httpBasic(Customizer.withDefaults());                  // ✅ Needed for client registration

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}