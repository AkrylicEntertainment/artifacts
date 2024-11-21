package dev.nateweisz.bytestore.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig(
    val clientRegistrationRepository: ClientRegistrationRepository,
    val authorizedClientSecret: OAuth2AuthorizedClientService
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { customizer -> customizer.disable() }
        .authorizeHttpRequests { customizer -> customizer
            .requestMatchers("/", "/error").permitAll()
            .anyRequest().permitAll()

        }
        //.oauth2Login { auth -> auth
        //    .defaultSuccessUrl("/login/success", true)}
        .build()
}