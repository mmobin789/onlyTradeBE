package onlytrade.app.login

import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route


fun Route.login() = route("/login") {
        post("/phone") {
            call.respondText("Login with Phone: Success ;D")
        }
        post("/email") {
            call.respondText("Login with Email: Success ;D")
        }
    }


