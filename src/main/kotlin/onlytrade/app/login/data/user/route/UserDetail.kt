package onlytrade.app.login.data.user.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.util.logging.Logger
import onlytrade.app.login.data.JwtConfig.JWT_AUTH
import onlytrade.app.login.data.JwtConfig.JWT_USERNAME_CLAIM
import onlytrade.app.login.data.user.UserRepository.findUserByCredential
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.GetUserDetailResponse

fun Route.getUserDetail(logger: Logger) = authenticate(JWT_AUTH) {
    get("/user/{id}") {
        call.principal<JWTPrincipal>()?.run {

            val username = payload.getClaim(JWT_USERNAME_CLAIM).asString()
            findUserByCredential(credential = username)?.let { user ->
                logger.info("User Found:${user.id}")
                if (user.id == call.parameters["id"]?.toLongOrNull()) {
                    val statusCode = HttpStatusCode.OK
                    call.respond(statusCode, GetUserDetailResponse(statusCode.value, user = user))
                } else {
                    val statusCode = HttpStatusCode.NotFound
                    call.respond(
                        statusCode,
                        GetUserDetailResponse(statusCode.value, error = statusCode.description)
                    )
                }

            } ?: run {
                val statusCode = HttpStatusCode.NotFound
                call.respond(
                    statusCode,
                    GetUserDetailResponse(
                        statusCode = statusCode.value,
                        error = statusCode.description
                    )
                )
            }
        } ?: run {
            val statusCode = HttpStatusCode.Unauthorized
            call.respond(
                statusCode,
                GetUserDetailResponse(statusCode = statusCode.value, error = statusCode.description)
            )
        }
    }
}