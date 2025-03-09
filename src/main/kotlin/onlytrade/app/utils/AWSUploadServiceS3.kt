package onlytrade.app.utils

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toByteArray

object AWSUploadServiceS3 {
    private const val BUCKET = "elasticbeanstalk-ap-south-1-879381256721"
    private const val REGION = "ap-south-1"
    private val s3Client = S3Client {
        region = REGION
        credentialsProvider = StaticCredentialsProvider {

        }
    } // Change region accordingly

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
    ) = "otDevImages/$userId/$categoryId/$productId/$imageNo"

    suspend

    fun uploadFile(
        bucketName: String = BUCKET,
        key: String,
        byteArray: ByteArray
    ) {
        val request = PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromBytes(byteArray)
            contentType = "image/jpg"
        }
        s3Client.putObject(request)
    }

    suspend fun downloadFile(bucketName: String = BUCKET, key: String): ByteArray? {
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
    }
}