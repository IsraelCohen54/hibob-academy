package com.hibob.academy.service


import com.hibob.academy.resource.LoginRequest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset


@Component
class SessionService {

    companion object {
        val SECRET_KEY = "secretWOw1234567890qwertyuiopasdfghjklzxcvbnm"
    }

    fun createJwtToken(loginRequest: LoginRequest): String {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("email", loginRequest.email)
            .claim("isAdmin", loginRequest.isAdmin)
            .setIssuedAt(Date.valueOf(LocalDate.now()))
            .setExpiration(Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }
}