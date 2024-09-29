package com.hibob.academy.filter


import com.hibob.feedback.service.FeedbackService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.ws.rs.container.ContainerRequestContext
import jakarta.ws.rs.container.ContainerRequestFilter
import jakarta.ws.rs.core.Response
import org.slf4j.LoggerFactory

const val CREATE_TOKEN_PATH = "api/createToken"
const val COOKIE_NAME = "JWT"

//@Component
//@Provider
class AuthenticationFilter: ContainerRequestFilter {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun filter(requestContext: ContainerRequestContext) {

        if (requestContext.uriInfo.path != CREATE_TOKEN_PATH) {
            verify(requestContext.cookies[COOKIE_NAME]?.value, requestContext)
            logger.info("Authorized successfully")
        }
    }

    fun verify(cookie: String?, requestContext: ContainerRequestContext) {
        cookie?.let {
            try {
                val claims: Claims = Jwts.parser()
                    .setSigningKey(FeedbackService.SECRET_KEY)
                    .parseClaimsJws(it)
                    .body

                val companyId = (claims["companyId"] as Number?)?.toLong()
                val employeeId = (claims["employeeId"] as Number?)?.toLong()

                requestContext.setProperty("companyId", companyId)
                requestContext.setProperty("employeeId", employeeId)

            } catch (e: Exception) {
                abortUnauthorized(requestContext, e)
            }
        } ?: abortUnauthorized(requestContext, Exception("No cookie"))
    }

    private fun abortUnauthorized(requestContext: ContainerRequestContext, e: Exception) {
        requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
        logger.warn("Authorized failure - exception: $e")
    }
}