package worker.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .authorizeHttpRequests( auth -> auth
                                .requestMatchers("/RequestLogging").hasRole("ADMIN")
                                .requestMatchers("/register/user").hasRole("BOOTSTRAP")
                                .requestMatchers("/database/getdatabases").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/database/getcollections/**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/database/**").hasAnyRole("ADMIN")
                                .requestMatchers("/collection/**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/document/***").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/api/auth/**").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("/write/Query").permitAll()
                                .requestMatchers("/broadcast/Query").permitAll()
                                .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
        ;
        return http.build();
    }
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}