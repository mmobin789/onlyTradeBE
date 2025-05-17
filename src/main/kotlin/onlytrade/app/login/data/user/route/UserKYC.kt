package onlytrade.app.login.data.user.route

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.util.logging.Logger
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlytrade.app.login.data.JwtConfig
import onlytrade.app.login.data.JwtConfig.JWT_USERNAME_CLAIM
import onlytrade.app.login.data.user.UserRepository.findUserByCredential
import onlytrade.app.login.data.user.UserRepository.setUserDocs
import onlytrade.app.utils.ImageUploadService
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.KycResponse

fun Route.userKyc(log: Logger) = authenticate(JwtConfig.JWT_AUTH) {
    post("/kyc") {
        call.principal<JWTPrincipal>()?.run {
            val username = payload.getClaim(JWT_USERNAME_CLAIM).asString()
            findUserByCredential(credential = username)?.run {
                log.info("User Found:${id}")
                var isError = false
                withContext(Dispatchers.IO) {
                    val docs = ArrayList<ByteArray>(3)
                    try {
                        val multipart = call.receiveMultipart()
                        multipart.forEachPart { part ->
                            log.info("Received part: ${part.name}, Type: ${part::class.simpleName}")
                            if (part is PartData.FileItem && part.name?.contains("userDoc") == true) {
                                log.info("KYC Request Image Received:${part.name}")
                                try {
                                    part.provider()
                                        .toInputStream().use {
                                            docs.add(it.readBytes())

                                        }
                                } catch (e: Exception) {
                                    isError = true
                                    val error = "Failed to read user image part: ${e.message}"
                                    log.error(error)
                                    val statusCode = HttpStatusCode.NotAcceptable
                                    call.respond(
                                        statusCode,
                                        KycResponse(
                                            statusCode = statusCode.value,
                                            error = error
                                        )
                                    )
                                    return@forEachPart
                                }
                            } /*else if (part is PartData.FormItem && part.name == "userId") {
                                kycRequest = part.value
                                        log.info("Add Product Request Received:${part.value}")


                            }*/
                            part.dispose()
                        }

                    } catch (e: Exception) {
                        isError = true
                        val statusCode = HttpStatusCode.BadRequest
                        log.error(e.message)
                        call.respond(
                            statusCode,
                            KycResponse(statusCode = statusCode.value, error = e.message)
                        )
                    }

                    if (isError) {
                        return@withContext
                    }

                    log.info("User Docs Found:${docs.size}")

                    // ✅ Step 2: Upload images in parallel
                    val imageUrls = docs.mapIndexed { index, bytes ->
                        val folderPath = ImageUploadService.buildUserImagePath(userId = id)
                        ImageUploadService.uploadFile(
                            name = "userDoc${index + 1}.jpg",
                            folderPath = folderPath,
                            byteArray = bytes
                        )
                    }.joinToString(",") // ✅ Collect all URLs

                    log.info("User Image URLs: $imageUrls")

                    // ✅ Step 3: Update user with doc URLs
                    setUserDocs(id, imageUrls)
                    val statusCode = HttpStatusCode.Accepted
                    call.respond(
                        statusCode, KycResponse(
                            statusCode = statusCode.value
                        )
                    )
                }
            } ?: run {
                val statusCode = HttpStatusCode.Unauthorized
                call.respond(
                    statusCode,
                    KycResponse(
                        statusCode = statusCode.value,
                        error = statusCode.description
                    )
                )
            }
        }
    }
}