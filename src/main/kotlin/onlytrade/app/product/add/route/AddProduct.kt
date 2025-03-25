package onlytrade.app.product.add.route

import com.sun.security.auth.UserPrincipal
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.principal
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import onlytrade.app.login.data.LoginConst
import onlytrade.app.login.data.user.UserRepository
import onlytrade.app.product.ProductsRepository
import onlytrade.app.viewmodel.product.add.repository.data.remote.request.AddProductRequest
import onlytrade.app.viewmodel.product.add.repository.data.remote.response.AddProductResponse

fun Route.addProduct() = authenticate(LoginConst.BASIC_AUTH) {
    post("/product/add") {
        call.principal<UserPrincipal>()?.run {
            val user = UserRepository.findUserByCredential(name)!!
            var addProductRequest = AddProductRequest(
                name = "",
                subcategoryId = -1,
                description = "",
                estPrice = -1.0
            )

            withContext(Dispatchers.IO) {
                val productImages = ArrayList<ByteArray>(15)
                val multipart = call.receiveMultipart()
                multipart.forEachPart { part ->
                    if (part is PartData.FileItem) part.provider().toInputStream().use {
                        productImages.add(it.readBytes())

                    }
                    else if (part is PartData.FormItem) {
                        addProductRequest = Json.decodeFromString(part.value)
                    }
                    part.dispose()
                }

                addProductRequest = addProductRequest.copy(productImages = productImages)

                val productId = ProductsRepository.addProduct(
                    userId = user.id, addProductRequest = addProductRequest
                )

                call.respond(
                    HttpStatusCode.Processing, AddProductResponse(
                        msg = "Product against id:$productId added for review."
                    )
                )
            }
        } ?: call.respond(HttpStatusCode.Unauthorized)
    }
}