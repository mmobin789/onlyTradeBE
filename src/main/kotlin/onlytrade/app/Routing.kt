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
import onlytrade.app.product.add.route.addProduct

fun Application.addRouting() {
    val baseUrl = System.getenv("BASE_URL") ?: "http://127.0.0.1:80/"
    routing {
        get("/") {
            call.respondRedirect("/login")
        }
        staticResources("/", "static")


        get("/login") {
            val loginUi = LoginUi(
                loginEmailUrl = "${baseUrl}login/email",
                loginPhoneUrl = "${baseUrl}login/phone",
            )

            val uiData = mapOf("ui" to loginUi)
            call.respond(ThymeleafContent("login", uiData))
        }
        login()
        addProduct()

    }
}