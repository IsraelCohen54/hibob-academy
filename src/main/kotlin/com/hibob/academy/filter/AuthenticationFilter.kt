package com.hibob.academy.filter

import com.hibob.academy.service.SessionService.Companion.SECRET_KEY
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jws
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.Provider
import org.springframework.stereotype.Component
import org.slf4j.LoggerFactory


const val CREATE_TOKEN_PATH = "createToken"
const val COOKIE_NAME = "JWT"

@Component
@Provider
class AuthenticationFilter: ContainerRequestFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(requestContext: ContainerRequestContext) {


        if (requestContext.uriInfo.path == CREATE_TOKEN_PATH) return

        try {
            val verificationResult = verify(requestContext.cookies[COOKIE_NAME]?.value)
            if (verificationResult == null) {
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
            } else {
                logger.info("Authorized successfully")
            }
        } catch (e: Exception) {
            logger.error("Error during verification", e)
            requestContext.abortWith(Response.status(Response.Status.INTERNAL_SERVER_ERROR).build())
        }
    }

    fun verify(cookie: String?): Jws<Claims>? {
        cookie?.let {
            try {
                Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(it)
            } catch (e: Exception) {
                return null
            }
        }
        return null
    }
}