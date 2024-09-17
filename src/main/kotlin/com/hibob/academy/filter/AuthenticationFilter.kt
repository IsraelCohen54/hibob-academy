package com.hibob.academy.filter

import com.hibob.academy.service.SessionService.Companion.SECRET_KEY
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.ext.Provider
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

const val CREATE_TOKEN_PATH = "createToken"
const val COOKIE_NAME = "JWT"

@Component
@Provider
class AuthenticationFilter: ContainerRequestFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path == CREATE_TOKEN_PATH) return

        verify(requestContext.cookies[COOKIE_NAME]?.value)
        logger.info("Authorized successfully")
    }

    fun verify(cookie: String?): Jws<Claims> {
        cookie?.let {
            return try {
                Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(it)
            } catch (e: Exception) {
                throw Exception("unauthorized error: ${e.message}")
            }
        } ?: throw Exception("unauthorized error")
    }
}