package onlytrade.app.login.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import onlytrade.app.login.data.LoginConst
import onlytrade.app.login.data.user.UserRepository.addUserByEmail
import onlytrade.app.login.data.user.UserRepository.addUserByPhone
import onlytrade.app.login.data.user.UserRepository.findUserByEmail
import onlytrade.app.login.data.user.UserRepository.findUserByPhone
import onlytrade.app.login.data.user.UserRepository.setUserLoggedInByEmail
import onlytrade.app.login.data.user.UserRepository.setUserLoggedInByPhone
import onlytrade.app.product.add.route.addProduct
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.login() = authenticate(LoginConst.BASIC_AUTH) {
    post("login/phone") {
        call.principal<UserIdPrincipal>()?.also {
            val pPhone = it.name
            val user = findUserByPhone(pPhone)
            if (user == null) {
                val phone = addUserByPhone(phone = pPhone).phone
                // call.sessions.set(UserSession(name = phone!!, count = 1)) //todo
                //call.sessions.set(userSession?.copy(count = userSession.count + 1)) in case of other requests session gets extended.
                call.respond(LoginResponse("Login success with Phone: $phone"))
            } else if (setUserLoggedInByPhone(pPhone)) {

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
                //  call.sessions.set(UserSession(name = email!!, count = 1))
                call.respond(LoginResponse("Login success with Email: $email"))
            } else if (setUserLoggedInByEmail(pEmail)) {
                call.respond(
                    HttpStatusCode.Found, LoginResponse("User already exists: ${user.email}")
                )
            }
        } ?: also {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }

    addProduct()
}


