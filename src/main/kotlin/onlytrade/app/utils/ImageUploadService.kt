package onlytrade.app.utils

import io.imagekit.sdk.ImageKit
import io.imagekit.sdk.config.Configuration
import io.imagekit.sdk.models.DeleteFolderRequest
import io.imagekit.sdk.models.FileCreateRequest

/**
 * uses imagekit.io impl.
 */
object ImageUploadService {

    private val imageKit = ImageKit.getInstance().apply {
        config = Configuration(
            "public_JbEOxy+vPRhChIBaX7ZaqHNR68s=",
            "private_X/R+XVvK1DCDVZM9JL7ugWPvqSI=",
            "https://ik.imagekit.io/ywetwhs4e9/otdev/"
        )
    }

    fun buildImagePath(
        rootFolderName: String,
        userId: Int,
        categoryId: Int,
        subcategoryId: Int,
        productId: Int
    ) = "$rootFolderName/$userId/$categoryId/$subcategoryId/$productId"

    fun uploadFile(
        name: String,
        folderPath: String,
        byteArray: ByteArray
    ): String? = FileCreateRequest(byteArray, name).run {
        try {
            folder = folderPath
            imageKit.upload(this).url
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    //todo test this
    fun deleteAllProductImages() = try {
        imageKit.deleteFolder(DeleteFolderRequest().apply {
            folderPath = "/products"
        }).responseMetaData.httpStatusCode
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}