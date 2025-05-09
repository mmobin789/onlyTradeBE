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
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.AddOfferRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.AddOfferResponse

fun Route.addOffer(log: Logger) = authenticate(JWT_AUTH) {
    post("/offer/add") {
        call.principal<JWTPrincipal>()?.run {
            val user = findUserByCredential(payload.getClaim(JWT_USERNAME_CLAIM).asString())
            log.info("User Found:${user?.id}")
            try {
                val addOfferRequest = call.receive<AddOfferRequest>()

                when (addOfferRequest.offerMakerId) {
                    addOfferRequest.offerReceiverId -> { //illogical case.
                        val statusCode = HttpStatusCode.BadRequest
                        call.respond(
                            statusCode,
                            AddOfferResponse(
                                statusCode = statusCode.value,
                                error = statusCode.description
                            )
                        )
                    }

                    user?.id -> {
                        OfferRepository.getOfferMade(
                            offerMakerId = user.id,
                            offerReceiverProductId = addOfferRequest.offerReceiverProductId
                        )?.run {
                            call.respond(
                                AddOfferResponse(
                                    statusCode = HttpStatusCode.OK.value,
                                    error = HttpStatusCode.OK.description,
                                    offer = this
                                )
                            )
                        } ?: run {
                            val placedOffer = OfferRepository.addOffer(addOfferRequest)
                            val statusCode = HttpStatusCode.Created
                            call.respond(
                                statusCode,
                                AddOfferResponse(
                                    offer = placedOffer,
                                    statusCode = statusCode.value,
                                )
                            )
                        }
                    }

                    else -> {
                        val statusCode = HttpStatusCode.Unauthorized
                        call.respond(
                            statusCode,
                            AddOfferResponse(
                                statusCode = statusCode.value,
                                error = statusCode.description
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                val statusCode = HttpStatusCode.BadRequest
                log.error(e.message)
                call.respond(
                    statusCode,
                    AddOfferResponse(
                        statusCode = statusCode.value,
                        error = e.message
                    )
                )
            }

        } ?: run {
            val statusCode = HttpStatusCode.Unauthorized
            call.respond(
                statusCode,
                AddOfferResponse(statusCode = statusCode.value, error = statusCode.description)
            )
        }
    }
}