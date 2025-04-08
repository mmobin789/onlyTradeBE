package onlytrade.app.login.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import onlytrade.app.login.data.JwtConfig.generateJWTToken
import onlytrade.app.login.data.user.UserRepository.addUserByEmail
import onlytrade.app.login.data.user.UserRepository.addUserByPhone
import onlytrade.app.login.data.user.UserRepository.findUserByEmail
import onlytrade.app.login.data.user.UserRepository.findUserByPhone
import onlytrade.app.utils.BcryptUtils.checkPassword
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.EmailLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.PhoneLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.login() {
    post("login/phone") {
        val loginRequest = call.receive<PhoneLoginRequest>()
        val user = findUserByPhone(loginRequest.phone)
        val storedHashPwd = user?.password
        if (user == null) {
            val newUser =
                addUserByPhone(phone = loginRequest.phone, password = loginRequest.password)
            val phone = newUser.phone!!
            val token = generateJWTToken(username = phone)
            call.respond(
                HttpStatusCode.OK,
                LoginResponse(status = HttpStatusCode.OK.value, jwtToken = token, user = newUser)
            )
        } else if (user.phone == loginRequest.phone && checkPassword(
                password = loginRequest.password,
                hashedPassword = storedHashPwd!!
            )
        ) {
            val token = generateJWTToken(username = user.phone!!)
            call.respond(
                HttpStatusCode.OK,
                LoginResponse(status = HttpStatusCode.OK.value, jwtToken = token, user = user)
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                LoginResponse(status = HttpStatusCode.NotFound.value, jwtToken = null, user = null)
            )
        }
    }
    post("login/email") {
        val loginRequest = call.receive<EmailLoginRequest>()
        val user = findUserByEmail(loginRequest.email)
        val storedHashPwd = user?.password
        if (user == null) {
            val newUser =
                addUserByEmail(email = loginRequest.email, password = loginRequest.password)
            val email = newUser.email!!
            val token = generateJWTToken(username = email)
            call.respond(
                HttpStatusCode.OK,
                LoginResponse(status = HttpStatusCode.OK.value, jwtToken = token, user = newUser)
            )
        } else if (user.email == loginRequest.email && checkPassword(
                password = loginRequest.password,
                hashedPassword = storedHashPwd!!
            )
        ) {
            val token = generateJWTToken(username = user.email!!)
            call.respond(
                HttpStatusCode.OK,
                LoginResponse(status = HttpStatusCode.OK.value, jwtToken = token, user = user)
            )
        } else {
            call.respond(
                HttpStatusCode.NotFound,
                LoginResponse(status = HttpStatusCode.NotFound.value, jwtToken = null, user = null)
            )
        }

    }
}


