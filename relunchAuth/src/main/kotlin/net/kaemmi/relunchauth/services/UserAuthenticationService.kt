package net.kaemmi.relunchauth.services

import net.kaemmi.relunchauth.models.Account
import net.kaemmi.relunchauth.models.AccountRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service


@Service
class UserAuthenticationService(private val accountRepository: AccountRepository) : UserDetailsService {

    @Autowired
    private val passwordEncoder: PasswordEncoder? = null

    override fun loadUserByUsername(userName: String): UserDetails {
        val account = accountRepository.findByUsername(userName)
            ?: throw IllegalArgumentException("no user with username $userName found")
        val authorities = listOf<GrantedAuthority>(SimpleGrantedAuthority(account.role))

        return User(account.id.toString(), account.password, authorities)
    }

}