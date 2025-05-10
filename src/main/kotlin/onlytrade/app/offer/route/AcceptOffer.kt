package onlytrade.app.offer.route

import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.util.logging.Logger
import onlytrade.app.login.data.JwtConfig.JWT_AUTH
import onlytrade.app.login.data.JwtConfig.JWT_USERNAME_CLAIM
import onlytrade.app.login.data.user.UserRepository.findUserByCredential
import onlytrade.app.offer.OfferRepository
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AcceptOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AcceptOfferResponse

fun Route.acceptOffer(log: Logger) = authenticate(JWT_AUTH) {
    post("/offer/accept") {
        call.principal<JWTPrincipal>()?.run {
            val user = findUserByCredential(payload.getClaim(JWT_USERNAME_CLAIM).asString())
            log.info("User Found:${user?.id}")
            try {
                val acceptOfferRequest = call.receive<AcceptOfferRequest>()
                val offerId = acceptOfferRequest.offerId

                if (offerId < 1) { //illogical case.
                    val statusCode = HttpStatusCode.BadRequest
                    call.respond(
                        statusCode,
                        AcceptOfferResponse(
                            statusCode = statusCode.value,
                            error = statusCode.description
                        )
                    )
                } else if (OfferRepository.acceptOffer(offerId)) {
                    call.respond(
                        AcceptOfferResponse(
                            statusCode = HttpStatusCode.Accepted.value,
                            error = HttpStatusCode.Accepted.description,
                            acceptedOfferId = offerId
                        )
                    )
                } else {
                    val statusCode = HttpStatusCode.NotFound
                    call.respond(
                        statusCode,
                        AcceptOfferResponse(
                            statusCode = statusCode.value,
                            error = statusCode.description
                        )
                    )
                }
            } catch (e: Exception) {
                val statusCode = HttpStatusCode.BadRequest
                log.error(e.message)
                call.respond(
                    statusCode,
                    AcceptOfferResponse(
                        statusCode = statusCode.value,
                        error = e.message
                    )
                )
            }

        } ?: run {
            val statusCode = HttpStatusCode.Unauthorized
            call.respond(
                statusCode,
                AcceptOfferResponse(statusCode = statusCode.value, error = statusCode.description)
            )
        }
    }
}