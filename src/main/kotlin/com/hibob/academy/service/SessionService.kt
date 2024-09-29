package com.hibob.academy.service


import com.hibob.feedback.dao.LoggedInUser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.stereotype.Component
import java.sql.Date
import java.time.LocalDateTime
import java.time.ZoneOffset


@Component
class SessionService {

    companion object {
        val SECRET_KEY = "secretWOw1234567890qwertyuiopasdfghjklzxcvbnm"
    }

    fun createJwtToken(loginRequest: LoggedInUser): String {
        return Jwts.builder()
            .setHeaderParam("typ", "JWT")
            .claim("employeeId", loginRequest.employeeId)
            .claim("companyId", loginRequest.companyId)
            .setExpiration(Date.from(LocalDateTime.now().plusDays(1).toInstant(ZoneOffset.UTC)))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact()
    }
}