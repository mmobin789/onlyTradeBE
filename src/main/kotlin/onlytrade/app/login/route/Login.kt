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
import onlytrade.app.login.data.UserRepository.addUserByEmail
import onlytrade.app.login.data.UserRepository.addUserByPhone
import onlytrade.app.login.data.UserRepository.findUserByEmail
import onlytrade.app.login.data.UserRepository.findUserByPhone
import onlytrade.app.login.session.UserSession
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.login() = authenticate("login-auth") {
    post("login/phone") {
        call.principal<UserIdPrincipal>()?.also {
            val pPhone = it.name
            val user = findUserByPhone(pPhone)
            if (user == null) {
                val phone = addUserByPhone(phone = pPhone).phone
                call.sessions.set(UserSession(name = phone!!, count = 1)) //todo
                //call.sessions.set(userSession?.copy(count = userSession.count + 1)) in case of other requests session gets extended.
                call.respond(LoginResponse("Login success with Phone: $phone"))
            } else {
                call.respond(
                    HttpStatusCode.Found, LoginResponse("User already exists: ${user.phone}")
                )
            }
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
    post("login/email") {
        call.principal<UserIdPrincipal>()?.also {
            val pEmail = it.name
            val user = findUserByEmail(pEmail)
            if (user == null) {
                val email = addUserByEmail(email = pEmail).email
                call.sessions.set(UserSession(name = email!!, count = 1))
                call.respond(LoginResponse("Login success with Email: $email"))
            } else {
                call.respond(
                    HttpStatusCode.Found, LoginResponse("User already exists: ${user.email}")
                )
            }
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
}


