package onlytrade.app

import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.EmailLoginRequest
import onlytrade.app.viewmodel.login.repository.data.remote.model.request.PhoneLoginRequest
import java.util.UUID
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class LoginApiTest {

    @Test
    fun testLoginRedirectOnRoot() = testApplication {
        application {
            module()
        }
        // Perform the GET request to /login
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status) // Check 200 OK
            // Check if the response is HTML
            assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), contentType())
        }.bodyAsText().run {
            assertTrue {
                contains("emailLoginUrl")
                contains("phoneLoginUrl")
            }
        }

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

        val response = client.config {
            install(ContentNegotiation) {
                json()
            }
        }.post("/login/phone") {
            contentType(ContentType.Application.Json)
            setBody(PhoneLoginRequest(phone, pwd))
        }
        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun testLoginByEmail() = testApplication {
        val email = "${UUID.randomUUID()}@ot.ot"
        val pwd = "1234567"
        application {
            module()
        }

        val response = client.config {
            install(ContentNegotiation) {
                json()
            }
        }.post("/login/email") {
            setBody(EmailLoginRequest(email, pwd))
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}