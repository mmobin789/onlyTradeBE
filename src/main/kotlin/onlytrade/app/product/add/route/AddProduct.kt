package onlytrade.app.product.add.route

import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.util.logging.Logger
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import onlytrade.app.login.data.LoginConst
import onlytrade.app.login.data.user.UserRepository
import onlytrade.app.product.ProductsRepository
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.add.repository.data.remote.response.AddProductResponse

fun Route.addProduct(log: Logger) = authenticate(LoginConst.BASIC_AUTH) {
    post("/product/add") {
        call.principal<UserIdPrincipal>()?.run {
            val user = UserRepository.findUserByCredential(name)!!
            var addProductRequest = AddProductRequest(
                name = "",
                subcategoryId = -1,
                description = "",
                estPrice = -1.0
            )
            log.info("User Found:${user.id}")
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
                            val error = "Failed to read product image part: ${e.message}"
                            log.error(error)
                            call.respond(
                                HttpStatusCode.NotAcceptable,
                                AddProductResponse(error)
                            )
                        }
                    } else if (part is PartData.FormItem && part.name == "AddProductRequest") {
                        addProductRequest = Json.decodeFromString(part.value)
                        log.info("Add Product Request Received:${part.value}")

                    }
                    part.dispose()
                }

                addProductRequest = addProductRequest.copy(productImages = productImages)

                log.info("Product Images Found:${productImages.size}")

                val productId = ProductsRepository.addProduct(
                    userId = user.id, addProductRequest = addProductRequest
                )

                log.info("Added Product for id:$productId")

                call.respond(
                    HttpStatusCode.Processing, AddProductResponse(
                        msg = "Product against id:$productId added for review."
                    )
                )
            }
        } ?: run {
            call.respond(
                HttpStatusCode.Unauthorized,
                AddProductResponse(msg = "401 due to null UserIdCredentials.")
            )
        }
    }
}