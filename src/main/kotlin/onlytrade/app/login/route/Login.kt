package onlytrade.app.login.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.sessions.sessions
import io.ktor.server.sessions.set
import onlytrade.app.login.data.UserRepository
import onlytrade.app.login.session.UserSession
import onlytrade.app.login.data.response.LoginResponse


fun Route.login() = authenticate("login-auth") {
    post("login/phone") {
        call.principal<UserIdPrincipal>()?.also {
            val phone = UserRepository.addUserByPhone(phone = it.name).phone
            call.sessions.set(UserSession(name = phone!!, count = 1)) //todo
            //call.sessions.set(userSession?.copy(count = userSession.count + 1)) in case of other requests session gets extended.
            call.respond(LoginResponse("Login success with Phone: $phone"))
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
    post("login/email") {
        call.principal<UserIdPrincipal>()?.also {
            val email = UserRepository.addUserByEmail(email = it.name).email
            call.sessions.set(UserSession(name = email!!, count = 1))
            call.respond(LoginResponse("Login success with Email: $email"))
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}


