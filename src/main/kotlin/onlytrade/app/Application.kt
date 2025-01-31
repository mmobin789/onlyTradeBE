package onlytrade.app

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import onlytrade.app.login.routing.addRouting
import onlytrade.app.login.templating.addTemplating


fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }
    install(ContentNegotiation) {
        json()

    }
    install(Resources)

    install(Authentication) {
        basic("login-auth") {
            validate { credentials ->
                // Validate credentials (username and password)
                if (credentials.name.isNotBlank() && credentials.password.isNotBlank()) {
                    UserIdPrincipal(credentials.name) // Return principal if valid
                } else {
                    null // Invalid credentials
                }
            }
        }
    }

    addTemplating()

    addRouting()


}