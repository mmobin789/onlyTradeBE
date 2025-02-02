package onlytrade.app.login.routing

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import onlytrade.app.login.data.UserRepository
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.login() = authenticate("login-auth") {
    post("login/phone") {
        call.principal<UserIdPrincipal>()?.also {
            val phone = UserRepository.addUserByPhone(phone = it.name).phone
            call.respond(LoginResponse("Login success with Phone: $phone"))

        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
    post("login/email") {
        call.principal<UserIdPrincipal>()?.also {
            val email = UserRepository.addUserByEmail(email = it.name).email
            call.respond(LoginResponse("Login success with Email: $email"))
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}


