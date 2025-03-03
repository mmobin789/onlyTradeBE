package onlytrade.app.product.add.route

import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.utils.io.jvm.javaio.toInputStream
import onlytrade.app.utils.ImageUploadService
import java.io.File

fun Route.addProduct() = post("/product/add") {
    val multipart = call.receiveMultipart()
    var fileName = ""
    multipart.forEachPart { part ->
        if (part is PartData.FileItem) {
            fileName = part.originalFileName ?: "uploaded.jpg"
            // Save the file to a folder
            val file = File("uploads/$fileName")
            part.provider().toInputStream().copyTo(file.outputStream())

            // val resource = this::class.java.classLoader?.getResource("static/test.png")?.readBytes()
            ImageUploadService.uploadFile(
                key = "otProdImages/",
                byteArray = file.readBytes()
            )
        }
        part.dispose()
    }

    call.respondText("File uploaded successfully: $fileName")
}