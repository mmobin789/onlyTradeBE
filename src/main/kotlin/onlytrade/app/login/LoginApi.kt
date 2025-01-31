package onlytrade.app.login

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.login() = authenticate("login-auth") {
    post("login/phone") {
        call.principal<UserIdPrincipal>()?.also {
            call.respond(LoginResponse("Login with Phone: Success ;D"))
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
    post("login/email") {
        call.principal<UserIdPrincipal>()?.also {
            call.respond(LoginResponse("Login with Email: Success ;D"))
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}


