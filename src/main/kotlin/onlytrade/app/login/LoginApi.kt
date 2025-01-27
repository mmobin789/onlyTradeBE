package onlytrade.app.login

import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import onlytrade.app.Greeting


fun Route.loginByMobile() {
    post("/login") {
        val params = call.receiveParameters()
        val mobile = params["mobile"]
        val pwd = params["pwd"]
        call.respondText("Login Info: ${Greeting().greet()}")
    }
}

fun Route.loginByEmail() {
    post("/login") {
        val params = call.receiveParameters()
        val email = params["email"]
        val pwd = params["pwd"]
        call.respondText("Login Info: ${Greeting().greet()}")
    }
}
