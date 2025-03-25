package onlytrade.app.login.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import onlytrade.app.login.data.user.UserRepository.addUserByEmail
import onlytrade.app.login.data.user.UserRepository.addUserByPhone
import onlytrade.app.login.data.user.UserRepository.findUserByEmail
import onlytrade.app.login.data.user.UserRepository.findUserByPhone
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.EmailLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.PhoneLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.login() {
    post("login/phone") {
        val loginRequest = call.receive<PhoneLoginRequest>()
        val user = findUserByPhone(loginRequest.phone)
        if (user == null) {
            val phone =
                addUserByPhone(phone = loginRequest.phone, password = loginRequest.password).phone
            call.respond(HttpStatusCode.Created, LoginResponse("Signup success with Phone: $phone"))
        } else if (user.phone == loginRequest.phone && user.password == loginRequest.password) {
            call.respond(
                HttpStatusCode.Found, LoginResponse("Login success with Phone: ${user.phone}")
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }
    }
    post("login/email") {
        val loginRequest = call.receive<EmailLoginRequest>()
        val user = findUserByEmail(loginRequest.email)
        if (user == null) {
            val email =
                addUserByEmail(email = loginRequest.email, password = loginRequest.password).email
            call.respond(HttpStatusCode.Created, LoginResponse("Signup success with Email: $email"))
        } else if (user.email == loginRequest.email && user.password == loginRequest.password) {
            call.respond(
                HttpStatusCode.Found, LoginResponse("User already exists: ${user.email}")
            )
        } else {
            call.respond(HttpStatusCode.Unauthorized)
        }

    }
}


