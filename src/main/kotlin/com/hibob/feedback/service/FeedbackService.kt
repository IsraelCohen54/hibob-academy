package com.hibob.feedback.service



import com.hibob.academy.resource.LoginRequestWithId
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset


@Component
class FeedbackService {

    companion object {
        val SECRET_KEY = "secretWOw1234567890qwertyuiopasdfghjklzxcvbnm"
    }

    fun createJwtToken(loginRequest: LoginRequestWithId): String {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("employeeId", loginRequest.employeeId)
            .claim("companyId", loginRequest.companyId)
            .setIssuedAt(Date.valueOf(LocalDate.now()))
            .setExpiration(Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }
}