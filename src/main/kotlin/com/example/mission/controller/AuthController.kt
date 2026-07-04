package com.example.mission.controller

import com.example.mission.dto.LoginRequest
import com.example.mission.dto.SignupRequest
import com.example.mission.dto.TokenResponse
import com.example.mission.service.AuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/signup")
    fun signup(@RequestBody request: SignupRequest): ResponseEntity<Void> {
        authService.signup(request)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }
}
