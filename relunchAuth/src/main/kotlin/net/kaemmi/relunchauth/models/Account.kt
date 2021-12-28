package net.kaemmi.relunchauth.models

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*


@Entity
class Account(
    @Column(unique=true)
    val username: String,
    val password: String,
    val role: String
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0
}

interface AccountRepository : JpaRepository<Account, Long> {
    fun findByUsername(username: String): Account?
}