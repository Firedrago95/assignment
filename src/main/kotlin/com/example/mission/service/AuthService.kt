package com.example.mission.service

import com.example.mission.domain.user.Role
import com.example.mission.domain.user.User
import com.example.mission.domain.user.LoginHistory
import com.example.mission.domain.user.LoginHistoryRepository
import com.example.mission.domain.user.UserRepository
import com.example.mission.dto.LoginRequest
import com.example.mission.dto.SignupRequest
import com.example.mission.dto.TokenResponse
import com.example.mission.global.auth.JwtTokenProvider
import com.example.mission.global.exception.BusinessException
import com.example.mission.global.exception.ErrorCode
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val loginHistoryRepository: LoginHistoryRepository
) {

    @Transactional
    fun signup(request: SignupRequest) {
        if (userRepository.findByEmail(request.email) != null) {
            throw BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS)
        }

        val user = User(
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            name = request.name,
            role = Role.MEMBER
        )

        userRepository.save(user)
    }

    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw BusinessException(ErrorCode.INVALID_CREDENTIALS)

        if (!passwordEncoder.matches(request.password, user.passwordHash)) {
            throw BusinessException(ErrorCode.INVALID_CREDENTIALS)
        }

        loginHistoryRepository.save(LoginHistory(user = user))

        val token = jwtTokenProvider.createToken(user.id, user.email, user.role.name)
        return TokenResponse(token)
    }
}
