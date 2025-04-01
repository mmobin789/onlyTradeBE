package onlytrade.app.utils

import io.imagekit.sdk.ImageKit
import io.imagekit.sdk.config.Configuration
import io.imagekit.sdk.models.FileCreateRequest

object ImageUploadService {

    private val imageKit = ImageKit.getInstance().apply {
        config = Configuration(
            "public_JbEOxy+vPRhChIBaX7ZaqHNR68s=",
            "private_X/R+XVvK1DCDVZM9JL7ugWPvqSI=",
            "https://ik.imagekit.io/ywetwhs4e9/otdev/"
        )
    }


    fun buildImageUrl(
        userId: Int,
        categoryId: Int,
        productId: Int,
        imageNo: Int
    ) =
        "https://$BUCKET.s3.$REGION.amazonaws.com/otDevImages/$userId/$categoryId/products/$productId/$imageNo"

    fun buildImagePath(
        userId: Int,
        categoryId: Int,
        productId: Int,
        imageNo: Int
    ) = "productImages/$userId/$categoryId/$productId/$imageNo"

    suspend

    fun uploadFile(
        name: String,
        byteArray: ByteArray
    ): String? = FileCreateRequest(byteArray, name).run {
        try {
            imageKit.upload(this).url
        } catch (e: Exception) {
            null
        }
    }

    /*    suspend fun downloadFile(bucketName: String = BUCKET, key: String): ByteArray? {
            val request = GetObjectRequest {
                this.bucket = bucketName
                this.key = key
            }
            return s3Client.getObject(request) { response ->
                response.body?.toByteArray()
            }

        }

        suspend fun deleteFile(bucketName: String = BUCKET, key: String) {
            val request = DeleteObjectRequest {
                this.bucket = bucketName
                this.key = key
            }
            s3Client.deleteObject(request)
        }*/
}