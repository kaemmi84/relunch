package net.kaemmi.relunchClient.config

import org.springframework.context.annotation.Bean
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
class SecurityConfig {
    @Bean
    @Throws(Exception::class)
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeRequests {
                it.anyRequest().authenticated()
            }
            .oauth2Login {
                it.loginPage(
                    "/oauth2/authorization/articles-client-oidc"
                )
            }
            .oauth2Client(Customizer.withDefaults())
        return http.build()
    }
}