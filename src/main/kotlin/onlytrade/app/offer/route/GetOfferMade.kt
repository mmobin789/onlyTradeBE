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
import onlytrade.app.viewmodel.product.offer.repository.data.remote.request.GetOfferMadeRequest
import onlytrade.app.viewmodel.product.offer.repository.data.remote.response.GetOfferResponse

fun Route.getOfferMade(log: Logger) = authenticate(JWT_AUTH) {
    post("/offer/made") {
        call.principal<JWTPrincipal>()?.run {
            val user = findUserByCredential(payload.getClaim(JWT_USERNAME_CLAIM).asString())
            log.info("User Found:${user?.id}")
            try {
                val (offerMakerId, productId) = call.receive<GetOfferMadeRequest>()

                if (offerMakerId <= 0 || productId <= 0) { //illogical case.
                    val statusCode = HttpStatusCode.BadRequest
                    call.respond(
                        statusCode,
                        GetOfferResponse(
                            statusCode = statusCode.value,
                            error = statusCode.description
                        )
                    )
                } else {
                    OfferRepository.getOfferMade(
                        offerMakerId = offerMakerId,
                        productId = productId
                    )?.run {
                        val statusCode = HttpStatusCode.OK
                        call.respond(
                            statusCode,
                            GetOfferResponse(
                                offer = this,
                                statusCode = statusCode.value,
                            )
                        )
                    } ?: run {
                        val statusCode = HttpStatusCode.NotFound
                        call.respond(
                            statusCode,
                            GetOfferResponse(
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
                    GetOfferResponse(
                        statusCode = statusCode.value,
                        error = e.message
                    )
                )
            }

        } ?: run {
            val statusCode = HttpStatusCode.Unauthorized
            call.respond(
                statusCode,
                GetOfferResponse(statusCode = statusCode.value, error = statusCode.description)
            )
        }
    }
}