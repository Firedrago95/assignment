package com.example.mission.global.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    @Value("\${jwt.secret:defaultSecretKeyForLocalDevelopmentWhichNeedsToBeLongEnough1234567890}")
    private val secretKeyString: String,
    @Value("\${jwt.expiration:3600000}") // 1시간
    private val expiration: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secretKeyString.toByteArray())

    fun createToken(userId: Long, email: String, role: String): String {
        val now = Date()
        val validity = Date(now.time + expiration)

        return Jwts.builder()
            .subject(userId.toString())
            .claim("email", email)
            .claim("role", role)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key)
            .compact()
    }

    fun getUserId(token: String): Long {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .subject
            .toLong()
    }

    fun getRole(token: String): String {
        return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
            .get("role", String::class.java)
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token)
            true
        } catch (e: Exception) {
            false
        }
    }
}
