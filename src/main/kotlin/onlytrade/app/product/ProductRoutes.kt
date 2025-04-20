package onlytrade.app.product

import io.ktor.server.routing.Route
import io.ktor.util.logging.Logger
import onlytrade.app.product.route.addProduct
import onlytrade.app.product.route.getProducts

fun Route.productRoutes(log: Logger) {
    addProduct(log)
    getProducts(log)
}