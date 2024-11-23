package dev.nateweisz.bytestore.web

import jakarta.servlet.ServletContext
import org.springframework.boot.web.servlet.ServletContextInitializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository


@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { customizer -> customizer.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) }
        .authorizeHttpRequests { customizer -> customizer
            .requestMatchers("/", "/error").permitAll()
            .requestMatchers("/api/user/").authenticated()
            .anyRequest().permitAll()

        }
        .sessionManagement { sessions -> sessions
            .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        }
        .oauth2Login { auth -> auth
            .defaultSuccessUrl("/api/auth/login/success", true)}
        .build()

    @Bean
    fun servletContextInitializer(): ServletContextInitializer {
        return ServletContextInitializer { servletContext: ServletContext ->
            servletContext.sessionCookieConfig.name = "SKIBIDI_AUTH_TOKEN"
            servletContext.sessionCookieConfig.isHttpOnly = true
            servletContext.sessionCookieConfig.isSecure = true
            servletContext.sessionCookieConfig.path = "/"
        }
    }
}