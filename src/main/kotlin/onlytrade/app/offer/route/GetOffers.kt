package onlytrade.app.offer.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import onlytrade.app.login.data.JwtConfig.JWT_AUTH
import onlytrade.app.offer.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.GetOffersResponse

fun Route.getOffers() = authenticate(JWT_AUTH) {
    get("/offers") {
        call.principal<JWTPrincipal>()?.run {
            val offers = OfferRepository.getOffers()
            if (offers.isNotEmpty()) {
                val statusCode = HttpStatusCode.OK
                call.respond(statusCode, GetOffersResponse(statusCode.value, offers = offers))
            } else {
                val statusCode = HttpStatusCode.NotFound
                call.respond(
                    statusCode,
                    GetOffersResponse(statusCode.value, error = "No offers found.")
                )
            }

        } ?: run {
            val statusCode = HttpStatusCode.Unauthorized
            call.respond(
                statusCode,
                GetOffersResponse(statusCode = statusCode.value, error = statusCode.description)
            )
        }
    }
}