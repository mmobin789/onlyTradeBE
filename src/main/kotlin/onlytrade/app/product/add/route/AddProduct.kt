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
import onlytrade.app.login.data.user.UserRepository
import onlytrade.app.utils.AWSUploadServiceS3
import onlytrade.app.viewmodel.product.add.repository.data.remote.model.response.AddProductResponse

fun Route.addProduct() = post("/product/add") {
    call.principal<UserPrincipal>()?.run {
        val user = UserRepository.findUserByCredential(name)
        val multipart = call.receiveMultipart()
        multipart.forEachPart { part ->
            if (part is PartData.FileItem) {
                // fileName = part.originalFileName ?: "uploaded.jpg"
                // Save the file to a folder
                //  val file = File("uploads/$fileName")
                part.provider().toInputStream().use {

                    /*   val byteArrayOutputStream = ByteArrayOutputStream()
                   it.toByteReadChannel().copyTo(byteArrayOutputStream)*/
                    // val resource = this::class.java.classLoader?.getResource("static/test.png")?.readBytes()
                    //todo working here
                    ///  ProductsRepository.addProduct(AddProductRequest(), user.id)
                    AWSUploadServiceS3.uploadFile(
                        key = "otDevImages/${user.id}/${789}/products/{}/{pid}", //todo
                        byteArray = it.readBytes()
                    )
                }
            }
            part.dispose()
        }

        call.respond(
            HttpStatusCode.Processing, AddProductResponse(
                msg = "Product added for review."
            )
        )
    }
}