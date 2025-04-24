package onlytrade.app.login

import io.ktor.http.HttpStatusCode
import io.ktor.server.http.content.staticResources
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.thymeleaf.ThymeleafContent
import onlytrade.app.login.data.JwtConfig.generateJWTToken
import onlytrade.app.login.data.ui.LoginUi
import onlytrade.app.login.data.user.UserRepository.addUserByEmail
import onlytrade.app.login.data.user.UserRepository.addUserByPhone
import onlytrade.app.login.data.user.UserRepository.findUserByEmail
import onlytrade.app.login.data.user.UserRepository.findUserByPhone
import onlytrade.app.utils.BcryptUtils.checkPassword
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.EmailLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.PhoneLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.loginRoutes() {

    get("/") {
        call.respondRedirect("/login")
    }
    staticResources("/", "static")


    get("/login") {
        val baseUrl = System.getenv("BASE_URL") ?: "http://127.0.0.1:80/"
        val loginUi = LoginUi(
            loginEmailUrl = "${baseUrl}login/email",
            loginPhoneUrl = "${baseUrl}login/phone",
        )

        val uiData = mapOf("ui" to loginUi)
        call.respond(ThymeleafContent("login", uiData))
    }

    post("login/phone") {
        try {
            val loginRequest = call.receive<PhoneLoginRequest>()
            val user = findUserByPhone(loginRequest.phone)
            val storedHashPwd = user?.password
            if (user == null) {
                val newUser =
                    addUserByPhone(phone = loginRequest.phone, password = loginRequest.password)
                val phone = newUser.phone!!
                val token = generateJWTToken(username = phone)
                val statusCode = HttpStatusCode.OK
                call.respond(
                    statusCode,
                    LoginResponse(statusCode = statusCode.value, jwtToken = token, user = newUser)
                )
            } else if (user.phone == loginRequest.phone && checkPassword(
                    password = loginRequest.password,
                    hashedPassword = storedHashPwd!!
                )
            ) {
                val token = generateJWTToken(username = user.phone!!)
                val statusCode = HttpStatusCode.OK
                call.respond(
                    statusCode,
                    LoginResponse(statusCode = statusCode.value, jwtToken = token, user = user)
                )
            } else {
                val statusCode = HttpStatusCode.NotFound
                call.respond(
                    statusCode,
                    LoginResponse(statusCode = statusCode.value, error = statusCode.description)
                )
            }
        } catch (e: Exception) {
            val statusCode = HttpStatusCode.BadRequest
            call.respond(
                statusCode,
                LoginResponse(statusCode = statusCode.value, error = e.message)
            )
        }
    }
    post("login/email") {
        try {
            val loginRequest = call.receive<EmailLoginRequest>()
            val user = findUserByEmail(loginRequest.email)
            val storedHashPwd = user?.password
            if (user == null) {
                val newUser =
                    addUserByEmail(email = loginRequest.email, password = loginRequest.password)
                val email = newUser.email!!
                val token = generateJWTToken(username = email)
                val statusCode = HttpStatusCode.OK
                call.respond(
                    statusCode,
                    LoginResponse(statusCode = statusCode.value, jwtToken = token, user = newUser)
                )
            } else if (user.email == loginRequest.email && checkPassword(
                    password = loginRequest.password,
                    hashedPassword = storedHashPwd!!
                )
            ) {
                val token = generateJWTToken(username = user.email!!)
                val statusCode = HttpStatusCode.OK
                call.respond(
                    statusCode,
                    LoginResponse(statusCode = statusCode.value, jwtToken = token, user = user)
                )
            } else {
                val statusCode = HttpStatusCode.NotFound
                call.respond(
                    statusCode,
                    LoginResponse(statusCode = statusCode.value, error = statusCode.description)
                )
            }
        } catch (e: Exception) {
            val statusCode = HttpStatusCode.BadRequest
            call.respond(
                statusCode,
                LoginResponse(statusCode = statusCode.value, error = e.message)
            )
        }

    }
}


