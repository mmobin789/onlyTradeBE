package onlytrade.app.product.add.route

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
import onlytrade.app.login.data.user.UserRepository
import onlytrade.app.product.ProductsRepository
import onlytrade.app.product.ProductsRepository.setProductImages
import onlytrade.app.utils.ImageUploadService
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.add.repository.data.remote.response.AddProductResponse

fun Route.addProduct(log: Logger) = authenticate(JwtConfig.JWT_AUTH) {
    post("/product/add") {
        call.principal<JWTPrincipal>()?.run {
            val username = payload.getClaim(JWT_USERNAME_CLAIM).asString()
            val user = UserRepository.findUserByCredential(credential = username)!!
            var addProductRequest = AddProductRequest(
                name = "",
                subcategoryId = -1,
                description = "",
                estPrice = -1.0
            )
            log.info("User Found:${user.id}")
            var isError = false
            withContext(Dispatchers.IO) {
                val productImages = ArrayList<ByteArray>(15)
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
                            call.respond(
                                HttpStatusCode.NotAcceptable,
                                AddProductResponse(
                                    status = HttpStatusCode.NotAcceptable.value,
                                    msg = error
                                )
                            )
                            return@forEachPart
                        }
                    } else if (part is PartData.FormItem && part.name == "AddProductRequest") {
                        addProductRequest = Json.decodeFromString(part.value)
                        log.info("Add Product Request Received:${part.value}")

                    }
                    part.dispose()
                }

                //   addProductRequest = addProductRequest.copy(productImages = productImages)

                if (isError) {
                    return@withContext
                }

                log.info("Product Images Found:${productImages.size}")

                val productId = ProductsRepository.addProduct(
                    userId = user.id, addProductRequest = addProductRequest
                )

                // ✅ Step 2: Upload images in parallel
                val imageUrls = productImages.mapIndexed { index, bytes ->
                    async {
                        val folderPath = ImageUploadService.buildImagePath(
                            rootFolderName = "products",
                            userId = user.id,
                            categoryId = 786, // TODO: Get from CategoryRepo based on subcategory.
                            subcategoryId = addProductRequest.subcategoryId,
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

                call.respond(
                    HttpStatusCode.Created, AddProductResponse(
                        status = HttpStatusCode.Created.value,
                        msg = "Product successfully in review."
                    )
                )
            }
        } ?: run {
            call.respond(
                HttpStatusCode.Unauthorized,
                AddProductResponse(
                    status = HttpStatusCode.Unauthorized.value,
                    msg = "Invalid UserCredentials."
                )
            )
        }
    }
}