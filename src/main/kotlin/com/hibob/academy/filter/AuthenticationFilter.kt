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
import org.springframework.web.bind.annotation.RequestMethod

@Component // todo ask - who does init it actually?
@Provider // todo ask - web return Boolean to authentication caller check?
class AuthenticationFilter: ContainerRequestFilter {
    override fun filter(requestContext: ContainerRequestContext) {

        //verification logic
        if (requestContext.uriInfo.path == "createToken") return

        val res = verify(requestContext.cookies["JWT"]?.value)
        if (res == null) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build())
        }
        print("authorized successfully")
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