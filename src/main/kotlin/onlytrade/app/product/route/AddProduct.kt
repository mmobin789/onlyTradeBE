package onlytrade.app.product.route

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
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import onlytrade.app.login.data.JwtConfig
import onlytrade.app.login.data.JwtConfig.JWT_USERNAME_CLAIM
import onlytrade.app.login.data.user.UserRepository.findUserByCredential
import onlytrade.app.product.ProductRepository
import onlytrade.app.product.ProductRepository.setProductImages
import onlytrade.app.utils.ImageUploadService
import onlytrade.app.viewmodel.product.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.repository.data.remote.response.AddProductResponse

fun Route.addProduct(log: Logger) = authenticate(JwtConfig.JWT_AUTH) {
    post("/product/add") {
        call.principal<JWTPrincipal>()?.run {
            val username = payload.getClaim(JWT_USERNAME_CLAIM).asString()
            findUserByCredential(credential = username)?.run {
                log.info("User Found:${id}")
                var addProductRequest: AddProductRequest? = null
                var isError = false
                withContext(Dispatchers.IO) {
                    val productImages = ArrayList<ByteArray>(15)
                    try {
                        val multipart = call.receiveMultipart()
                        multipart.forEachPart { part ->
                            log.info("Received part: ${part.name}, Type: ${part::class.simpleName}")
                            if (part is PartData.FileItem && part.name?.contains("productImage") == true) {
                                log.info("Add Product Request Image Received:${part.name}")
                                try {
                                    part.provider()
                                        .toInputStream().use {
                                            productImages.add(it.readBytes())

                                        }
                                } catch (e: Exception) {
                                    isError = true
                                    val error = "Failed to read product image part: ${e.message}"
                                    log.error(error)
                                    val statusCode = HttpStatusCode.NotAcceptable
                                    call.respond(
                                        statusCode,
                                        AddProductResponse(
                                            statusCode = statusCode.value,
                                            error = error
                                        )
                                    )
                                    return@forEachPart
                                }
                            } else if (part is PartData.FormItem && part.name == "AddProductRequest") {
                                addProductRequest = try {
                                    Json.decodeFromString<AddProductRequest>(part.value).also {
                                        log.info("Add Product Request Received:${part.value}")
                                    }
                                } catch (e: Exception) {
                                    isError = true
                                    val error =
                                        "Failed to read AddProductRequest part: ${e.message}"
                                    log.error(error)
                                    val statusCode = HttpStatusCode.NotAcceptable
                                    call.respond(
                                        statusCode,
                                        AddProductResponse(
                                            statusCode = statusCode.value,
                                            error = error
                                        )
                                    )
                                    return@forEachPart
                                }


                            }
                            part.dispose()
                        }

                    } catch (e: Exception) {
                        isError = true
                        val statusCode = HttpStatusCode.BadRequest
                        log.error(e.message)
                        call.respond(
                            statusCode,
                            AddProductResponse(statusCode = statusCode.value, error = e.message)
                        )
                    }

                    if (isError) {
                        return@withContext
                    }

                    log.info("Product Images Found:${productImages.size}")

                    val productId = ProductRepository.addProduct(
                        userId = id, addProductRequest = addProductRequest!!
                    )

                    // ✅ Step 2: Upload images in parallel
                    val imageUrls = productImages.mapIndexed { index, bytes ->
                        async {
                            val folderPath = ImageUploadService.buildProductImagePath(
                                userId = id,
                                categoryId = addProductRequest!!.categoryId,
                                subcategoryId = addProductRequest!!.subcategoryId,
                                productId = productId
                            )
                            ImageUploadService.uploadFile(
                                name = "productImage${index + 1}.jpg",
                                folderPath = folderPath,
                                byteArray = bytes
                            )
                        }
                    }.awaitAll().filterNotNull().joinToString(",") // ✅ Collect all URLs

                    log.info("Product Image URLs: $imageUrls")

                    // ✅ Step 3: Update product with image URLs
                    setProductImages(productId, imageUrls)
                    val statusCode = HttpStatusCode.Created
                    call.respond(
                        statusCode, AddProductResponse(
                            statusCode = statusCode.value
                        )
                    )
                }
            } ?: run {
                val statusCode = HttpStatusCode.Unauthorized
                call.respond(
                    statusCode,
                    AddProductResponse(
                        statusCode = statusCode.value,
                        error = statusCode.description
                    )
                )
            }
        } ?: run {
            val statusCode = HttpStatusCode.Unauthorized
            call.respond(
                statusCode,
                AddProductResponse(
                    statusCode = statusCode.value,
                    error = statusCode.description
                )
            )
        }
    }
}