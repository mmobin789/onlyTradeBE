package onlytrade.app.login.routing

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.addRouting() {
    routing {
        get("/") { //todo remove
            call.respondText("Hello World! Locally!")
        }
        staticResources("/static", "static", index = "login.html")
        login()

        // Static plugin. Try to access `/static/login.html`


    }
}