package com.hibob.academy.resource

import com.hibob.academy.filter.COOKIE_NAME
import com.hibob.academy.service.SessionService
import jakarta.ws.rs.Consumes
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.NewCookie
import jakarta.ws.rs.core.Response
import org.springframework.stereotype.Component

@Component// sprint boot init
@Produces(MediaType.APPLICATION_JSON)
@Path("/api/createToken")
class TokenController(private val sessionService: SessionService) {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    fun createToken(loginRequest:LoginRequest): Response {
        val creatJwrToken = sessionService.createJwtToken(loginRequest)
        val cookie = NewCookie.Builder(COOKIE_NAME).value(creatJwrToken).path("/api/").build()
        return Response.ok().cookie(cookie).build()
    }

    @POST
    @Path("/try")
    @Consumes(MediaType.APPLICATION_JSON)
    fun createToken1(loginRequest:LoginRequest): Response {
        return Response.ok().build()
    }
}

data class LoginRequest(val email: String, val isAdmin: Boolean)