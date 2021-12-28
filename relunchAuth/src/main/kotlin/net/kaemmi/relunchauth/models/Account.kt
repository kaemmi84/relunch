package net.kaemmi.relunchauth.models

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*


@Entity
class Account(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long,
    @Column(unique=true)
    val username: String,
    val password: String,
    val role: String
)

interface AccountRepository : JpaRepository<Account, Long> {
    fun findByUsername(username: String): Account?
}