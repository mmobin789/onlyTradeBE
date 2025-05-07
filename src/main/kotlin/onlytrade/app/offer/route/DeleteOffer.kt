package onlytrade.app.offer.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import onlytrade.app.login.data.JwtConfig.JWT_AUTH
import onlytrade.app.offer.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.DeleteOfferResponse

fun Route.deleteOffer() = authenticate(JWT_AUTH) {
    delete("/offer/{id}") {
        call.principal<JWTPrincipal>()?.run {
            try {
                val offerId = call.parameters["id"]?.toLongOrNull()
                if (offerId != null && offerId > 0 && OfferRepository.deleteOffer(offerId)) {
                    val statusCode = HttpStatusCode.OK
                    call.respond(
                        statusCode,
                        DeleteOfferResponse(statusCode.value, deletedOfferId = offerId)
                    )
                } else {
                    val statusCode = HttpStatusCode.NotFound
                    call.respond(
                        statusCode,
                        DeleteOfferResponse(
                            statusCode.value,
                            error = "No offer found matching id = $offerId."
                        )
                    )
                }
            } catch (e: Exception) {
                val statusCode = HttpStatusCode.BadRequest
                call.respond(
                    statusCode,
                    DeleteOfferResponse(statusCode.value, error = e.message)
                )
            }

        } ?: run {
            val statusCode = HttpStatusCode.Unauthorized
            call.respond(
                statusCode,
                DeleteOfferResponse(statusCode = statusCode.value, error = statusCode.description)
            )
        }
    }
}