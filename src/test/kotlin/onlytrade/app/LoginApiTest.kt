package onlytrade.app

import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BasicAuthCredentials
import io.ktor.client.plugins.auth.providers.basic
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.testing.testApplication
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

            install(Authentication) {
                basic {
                    validate {
                        if (it.name == phone && it.password == pwd) UserIdPrincipal(it.name) else null
                    }
                }
            }
            module()
        }

        client.config {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                basic {
                    credentials { BasicAuthCredentials(phone, pwd) }
                }
            }
        }

        val response = client.post("/login/phone") {
            basicAuth(phone, pwd)
            contentType(ContentType.Application.Json)
            //  setBody(PhoneLoginRequest(phone, pwd))
        }

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("Login with Phone: Success ;D", response.bodyAsText())
    }

    @Test
    fun testLoginByEmail() = testApplication {

        val email = "mm@m.com"
        val pwd = "1234567"
        application {
            install(Authentication) {
                basic {
                    validate {
                        if (it.name == email && it.password == pwd) UserIdPrincipal(it.name) else null
                    }
                }
            }
            module()
        }
        client.config {
            install(ContentNegotiation) {
                json()
            }
            install(Auth) {
                basic {
                    credentials { BasicAuthCredentials(email, pwd) }
                }
            }
        }

        val response = client.post("/login/email") {
            basicAuth(email, pwd)
            contentType(ContentType.Application.Json)
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val actual = response.bodyAsText()
        assertEquals("Login with Email: Success ;D", actual)
    }
}