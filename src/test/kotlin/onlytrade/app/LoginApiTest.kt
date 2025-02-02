package onlytrade.app

import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.testing.testApplication
import kotlinx.serialization.json.Json
import onlytrade.app.viewmodel.login.repository.data.remote.model.response.LoginResponse
import java.util.UUID
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class LoginApiTest {

    @Test
    fun testRoot() = testApplication {
        application {
            module()
        }
        val response = client.get("/")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Hello World! Locally!", response.bodyAsText())
    }

    @Test
    fun testLoginByPhone() = testApplication {
        val phoneNoBuilder = StringBuilder("03")

        repeat(9) {
            phoneNoBuilder.append(Random.nextInt(0, 9))
        }

        val phone = phoneNoBuilder.toString()
        val pwd = "1234567"

        application {
            module()
        }

        /*   client.config {
               install(ContentNegotiation) {
                   json()
               }
           }*/

        val response = client.post("/login/phone") {
            basicAuth(phone, pwd)
            contentType(ContentType.Application.Json)
            //  setBody(PhoneLoginRequest(phone, pwd))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val loginResponse = Json.decodeFromString<LoginResponse>(response.body())
        assertEquals("Login success with Phone: $phone", loginResponse.msg)
    }

    @Test
    fun testLoginByEmail() = testApplication {
        val email = "${UUID.randomUUID()}@ot.ot"
        val pwd = "1234567"
        application {
            module()
        }
        /*\  client.config {
              install(ContentNegotiation) {
                  json()
              }
             install(Auth) {
                  basic {
                      credentials { BasicAuthCredentials(email, pwd) }
                  }
              }
          }*/

        val response = client.post("/login/email") {
            basicAuth(email, pwd)
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val loginResponse = Json.decodeFromString<LoginResponse>(response.body())
        assertEquals("Login success with Email: $email", loginResponse.msg)
    }
}