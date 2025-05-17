package onlytrade.app.utils

import io.imagekit.sdk.ImageKit
import io.imagekit.sdk.config.Configuration
import io.imagekit.sdk.models.DeleteFolderRequest
import io.imagekit.sdk.models.FileCreateRequest

/**
 * uses imagekit.io impl.
 */
object ImageUploadService {

    private val devEnv = System.getenv("BASE_URL").contains("-dev")

    private val imageKit = ImageKit.getInstance().apply {
        config = Configuration(
            "public_JbEOxy+vPRhChIBaX7ZaqHNR68s=",
            "private_X/R+XVvK1DCDVZM9JL7ugWPvqSI=",
            "https://ik.imagekit.io/ywetwhs4e9/${if (devEnv) "otdev" else "ot"}/"
        )
    }

    fun buildProductImagePath(
        userId: Long,
        categoryId: Long,
        subcategoryId: Long,
        productId: Long
    ) = "${
        if (devEnv) "dev" else "prod"
    }/products/$userId/$categoryId/$subcategoryId/$productId"


    fun buildUserImagePath(
        userId: Long
    ) = "${
        if (devEnv) "dev" else "prod"
    }/users/$userId/docs"

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
    fun deleteImages(path: String) = try {
        imageKit.deleteFolder(DeleteFolderRequest().apply {
            folderPath = path
        }).responseMetaData.httpStatusCode
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}