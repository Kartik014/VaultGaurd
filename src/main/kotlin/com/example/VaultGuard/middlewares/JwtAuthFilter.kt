package com.example.VaultGuard.middlewares

import com.example.VaultGuard.models.User
import com.example.VaultGuard.utils.JwtUtils
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import kotlin.text.split
import kotlin.text.startsWith

class JwtAuthFilter(private val jwtUtils: JwtUtils): OncePerRequestFilter() {
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val authHeader = request.getHeader("Authorization")
            if(authHeader != null && authHeader.startsWith("Bearer ")){
                val token = authHeader.split(" ")[1]
                val email: String = jwtUtils.extractEmail(token).toString()
                val userID: String = jwtUtils.extractIdFromClaim(token).toString()
                val username: String = jwtUtils.extractUsername(token).toString()
                val role: String = jwtUtils.extractUserRole(token).toString()

                if(SecurityContextHolder.getContext().authentication == null){
                    if(jwtUtils.validateToken(token, userID)){
                        val user = User(userID, username, email, "", role)
                        val authorities = listOf(SimpleGrantedAuthority(role))
                        val authToken = UsernamePasswordAuthenticationToken(user, null, authorities)
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    }
                }
            }
        } catch (ex: ExpiredJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired")
            return
        } catch (ex: MalformedJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
            return
        } catch (ex: UnsupportedJwtException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unsupported token")
            return
        } catch (ex: IllegalArgumentException) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token claims string is empty")
            return
        }

        filterChain.doFilter(request, response)
    }
}