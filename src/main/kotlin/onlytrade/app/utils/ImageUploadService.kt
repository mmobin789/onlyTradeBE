package onlytrade.app.utils

import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.DeleteObjectRequest
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.fromFile
import aws.smithy.kotlin.runtime.content.toByteArray
import java.io.File

object ImageUploadService {
    private val s3Client = S3Client {
        region = "ap-south-1"
        credentialsProvider = StaticCredentialsProvider {
            // accountId= "879381256721"
            accessKeyId = "AKIA4ZPZVDII3IHRMLO3"
            secretAccessKey = "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"
        }
    } // Change region accordingly

    suspend fun uploadFile(
        bucketName: String = "elasticbeanstalk-ap-south-1-879381256721",
        key: String,
        byteArray: ByteArray
    ) {
        val request = PutObjectRequest {
            bucket = bucketName
            this.key = key
            body = ByteStream.fromBytes(byteArray)
            contentType = "image/png"
        }
        s3Client.putObject(request)
    }

    suspend fun downloadFile(bucketName: String, key: String): ByteArray? {
        val request = GetObjectRequest {
            this.bucket = bucketName
            this.key = key
        }
        return s3Client.getObject(request) { response ->
            response.body?.toByteArray()
        }

    }

    suspend fun deleteFile(bucketName: String, key: String) {
        val request = DeleteObjectRequest {
            this.bucket = bucketName
            this.key = key
        }
        s3Client.deleteObject(request)
    }
}