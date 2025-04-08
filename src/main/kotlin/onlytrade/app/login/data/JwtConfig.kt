package onlytrade.app.login.data

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    // JWT Configuration
    private val JWT_SECRET = System.getenv("JWT_SECRET") ?: "otWeb-local"
    private const val JWT_ISSUER = "ktor.io"
    private const val JWT_AUDIENCE = "ktor-client"
    private val JWT_EXPIRES_IN = System.getenv("JWT_EXPIRES_IN").toIntOrNull() ?: 86400  // in seconds 1 day here and 6 months on web.
    const val JWT_AUTH = "login-auth-jwt"
    const val JWT_USERNAME_CLAIM = "jwt-username"

    val jwtVerifier: JWTVerifier = JWT.require(Algorithm.HMAC256(JWT_SECRET))
        .withAudience(JWT_AUDIENCE).withIssuer(JWT_ISSUER).build()

    // JWT Token Generation
    fun generateJWTToken(username: String): String {
        return JWT.create()
            .withSubject("Authentication")
            .withIssuer(JWT_ISSUER)
            .withAudience(JWT_AUDIENCE)
            .withClaim(JWT_USERNAME_CLAIM, username)
            .withExpiresAt(Date(System.currentTimeMillis() + JWT_EXPIRES_IN * 1000))
            .sign(Algorithm.HMAC256(JWT_SECRET))
    }

}