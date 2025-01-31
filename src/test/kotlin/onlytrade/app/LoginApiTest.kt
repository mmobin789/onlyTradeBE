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
import kotlin.test.Test
import kotlin.test.assertEquals

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
        val phone = "03217000000"
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
        assertEquals("Login with Phone: Success ;D", loginResponse.msg)
    }

    @Test
    fun testLoginByEmail() = testApplication {

        val email = "mm@m.com"
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
        assertEquals("Login with Email: Success ;D", loginResponse.msg)
    }
}