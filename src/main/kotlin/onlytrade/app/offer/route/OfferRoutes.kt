package onlytrade.app.offer.route

import io.ktor.server.routing.Route
import io.ktor.util.logging.Logger

fun Route.offerRoutes(logger: Logger) {
    addOffer(logger)
    getOffers()
    deleteOffer()
}