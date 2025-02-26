package onlytrade.app

import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.ThymeleafContent
import onlytrade.app.login.data.ui.LoginUi
import onlytrade.app.login.route.login

fun Application.addRouting() {
    val localEnv = System.getenv("DEV_MODE") == "0" // change 1 to 0 as default value to enable local environment.
    routing {
        get("/") {
            call.respondRedirect("/login")
        }
        staticResources("/", "static")


        get("/login") {
            val loginUi = LoginUi(
                loginEmailUrl = if (localEnv) "http://127.0.0.1:80/login/email" else "http://onlytrade.ap-south-1.elasticbeanstalk.com/login/email",
                loginPhoneUrl = if (localEnv) "http://127.0.0.1:80/login/phone" else "http://onlytrade.ap-south-1.elasticbeanstalk.com/login/phone",
            )

            val uiData = mapOf("ui" to loginUi)
            call.respond(ThymeleafContent("login", uiData))
        }
        login()

        // Static plugin. Try to access `/static/login.html`


    }
}