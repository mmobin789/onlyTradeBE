package onlytrade.app.login

import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse


fun Route.login() = route("/login") {
    post("/phone") {
        call.respond(LoginResponse("Login with Phone: Success ;D"))
    }
    post("/email") {
        call.respond(LoginResponse("Login with Email: Success ;D"))
    }
}


