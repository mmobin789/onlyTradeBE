package onlytrade.app.login.data

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object LoginConst {
    // JWT Configuration
    const val JWT_SECRET = "otWeb"
    const val JWT_ISSUER = "ktor.io"
    const val JWT_AUDIENCE = "ktor-client"
    private const val JWT_EXPIRES_IN = 60 * 60 * 24 // 1 day
    const val JWT_AUTH = "login-auth-jwt"
    const val JWT_USERNAME_CLAIM = "jwt-username"

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