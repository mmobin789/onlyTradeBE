package onlytrade.app.product

import io.ktor.server.routing.Route
import io.ktor.util.logging.Logger
import onlytrade.app.product.add.route.addProduct

fun Route.productRoutes(log: Logger) {
    addProduct(log = log)
}