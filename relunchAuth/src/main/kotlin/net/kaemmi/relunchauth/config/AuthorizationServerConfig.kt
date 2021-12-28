package net.kaemmi.relunchauth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import net.kaemmi.relunchauth.utils.KeyGeneratorUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.config.ProviderSettings
import org.springframework.security.oauth2.server.authorization.config.TokenSettings
import org.springframework.security.web.SecurityFilterChain
import java.time.Duration
import java.util.*

@Configuration(proxyBeanMethods = false)
class AuthorizationServerConfig {

    @Value("\${security.client-id}")
    private val clientId: String? = null

    @Value("\${security.secret}")
    private val secret: String? = null

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(10)

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http.cors().and())

        http.cors().and().csrf().disable()
                .formLogin(withDefaults<FormLoginConfigurer<HttpSecurity>>())

        return http.build()
    }

    @Bean
    fun registeredClientRepository(
        jdbcTemplate: JdbcTemplate,
        passwordEncoder: BCryptPasswordEncoder
    ): RegisteredClientRepository {
        val registeredClientRepository = JdbcRegisteredClientRepository(jdbcTemplate)
        val registeredClientParametersMapper = JdbcRegisteredClientRepository.RegisteredClientParametersMapper()
        val yourClientId = this.clientId
        val yourSecret = this.secret

        registeredClientParametersMapper.setPasswordEncoder(passwordEncoder)
        registeredClientRepository.setRegisteredClientParametersMapper(registeredClientParametersMapper)

        val client = registeredClientRepository.findByClientId(yourClientId)

        if (client == null) {
            val registeredClient: RegisteredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .tokenSettings(
                    TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(1))
                        .refreshTokenTimeToLive(Duration.ofDays(90))
                        .build()
                )
                .clientId(yourClientId)
                .clientSecret(yourSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .redirectUri("http://127.0.0.1:8081/authorized")
                .redirectUri("https://oauth.pstmn.io/v1/callback")
                .scope(OidcScopes.OPENID)
                .scope("")
                .scope("yourapplication.read")
                .scope("yourapplication.write")
                .build()

            registeredClientRepository.save(registeredClient)
        }

        return registeredClientRepository
    }

    @Bean
    fun authorizationService(
        jdbcTemplate: JdbcTemplate,
        registeredClientRepository: RegisteredClientRepository
    ): OAuth2AuthorizationService {
        return JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository)
    }

    @Bean
    fun jwkSource(): JWKSource<SecurityContext> {
        val rsaKey = KeyGeneratorUtils.generateRSAKey()
        val jwkSet = JWKSet(rsaKey)
        return JWKSource<SecurityContext> { jwkSelector, _ -> jwkSelector.select(jwkSet) }
    }

    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    @Bean
    fun providerSettings(): ProviderSettings {

        val x = ProviderSettings.builder().issuer("http://localhost:9000").build()
        return x
    }
}