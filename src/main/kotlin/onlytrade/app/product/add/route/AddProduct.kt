package onlytrade.app.product.add.route

import com.sun.security.auth.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import onlytrade.app.login.data.user.UserRepository
import onlytrade.app.product.ProductsRepository
import onlytrade.app.utils.AWSUploadServiceS3
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.add.repository.data.remote.response.AddProductResponse

fun Route.addProduct() = post("/product/add") {
    call.principal<UserPrincipal>()?.run {
        val user = UserRepository.findUserByCredential(name)
        var addProductRequest = AddProductRequest("", -1, "", -1.0)

        withContext(Dispatchers.IO) {
            val productImages = ArrayList<ByteArray>(15)
            val multipart = call.receiveMultipart()
            multipart.forEachPart { part ->
                if (part is PartData.FileItem)
                    part.provider().toInputStream().use {
                        productImages.add(it.readBytes())

                    }
                else if (part is PartData.FormItem) {
                    addProductRequest = when (part.name) {
                        "subcategoryId" -> addProductRequest.copy(
                            subcategoryId = part.value.toIntOrNull() ?: -1
                        )

                        "name" -> addProductRequest.copy(name = part.value)
                        "estPrice" -> addProductRequest.copy(
                            estPrice = part.value.toDoubleOrNull() ?: -1.0
                        )

                        else -> addProductRequest.copy(description = part.value)
                    }
                }
                part.dispose()
            }


            val productId = ProductsRepository.addProduct(
                userId = user.id,
                addProductRequest = addProductRequest
            )


            val urlsBuilder = StringBuilder(productImages.size)

            productImages.forEachIndexed { index, bytes ->
                val filepath = AWSUploadServiceS3.buildImagePath(
                    userId = user.id,
                    categoryId = addProductRequest.subcategoryId,
                    productId = productId,
                    imageNo = index + 1
                )
                AWSUploadServiceS3.uploadFile(
                    key = filepath,
                    byteArray = bytes
                )

                val url = AWSUploadServiceS3.buildImageUrl(
                    userId = user.id,
                    categoryId = addProductRequest.subcategoryId,
                    productId = productId,
                    imageNo = index + 1
                )

                urlsBuilder.append(url).also {
                    if (index < productImages.lastIndex)
                        it.append(",")
                }
            }

            ProductsRepository.setProductImages(
                id = productId, imageUrls = urlsBuilder.toString()
            )
            call.respond(
                HttpStatusCode.Processing, AddProductResponse(
                    msg = "Product added for review."
                )
            )
        }
    }
}