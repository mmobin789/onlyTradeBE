package onlytrade.app

import io.ktor.server.application.Application
import io.ktor.server.application.log
import io.ktor.server.routing.routing
import onlytrade.app.login.loginRoutes
import onlytrade.app.offer.route.offerRoutes
import onlytrade.app.product.productRoutes

fun Application.addRouting() {
    routing {
        loginRoutes(log)
        productRoutes(log)
        offerRoutes(log)
    }
}