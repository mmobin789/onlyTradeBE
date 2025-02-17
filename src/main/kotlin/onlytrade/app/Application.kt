package onlytrade.app

import io.ktor.http.HttpHeaders
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.basic
import io.ktor.server.auth.session
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondRedirect
import io.ktor.server.response.respondText
import io.ktor.server.sessions.SessionTransportTransformerEncrypt
import io.ktor.server.sessions.Sessions
import io.ktor.server.sessions.cookie
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.util.hex
import onlytrade.app.db.configureDatabases
import onlytrade.app.login.session.UserSession
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import java.io.File


fun main() {
    embeddedServer(
        Netty,
        configure = {
            envConfig()
        },
        module = Application::module
    ).start(wait = true)
}

fun Application.module() {

    install(CallLogging)

    install(CORS) {
        anyMethod()
        allowHost("localhost:80")
        allowHost("127.0.0.1:80")
        allowCredentials = true
        allowNonSimpleContentTypes = true
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
    }

    install(Sessions) {
        val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
        val secretSignKey = hex("6819b57a326945c1968f45236589")
        cookie<UserSession>("user_session") {
            cookie.path = "/"
            cookie.maxAgeInSeconds = 180
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }

    install(StatusPages) {
        exception<IllegalStateException> { call, cause ->
            call.respondText("App in illegal state as ${cause.message}")
        }
    }
    install(ContentNegotiation) {
        json()

    }

    // Install Thymeleaf plugin
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "static/"
            suffix = ".html"
            characterEncoding = Charsets.UTF_8.name()
        })
    }
    install(Resources)

    install(Authentication) {
        basic("login-auth") {
            validate { credentials ->
                // Validate credentials (username and password)
                if (credentials.name.isNotBlank() && credentials.password.isNotBlank()) {
                    UserIdPrincipal(credentials.name) // Return principal if valid
                } else {
                    null // Invalid credentials
                }
            }
        }
        session<UserSession>("auth-session") {
            validate { session ->
                if (session.name.startsWith("jet")) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/login") // user will need to login again at this point.
            }
        }

        //jwt {
        // Configure jwt authentication
        //  }
    }
    configureDatabases()

    addRouting()
}

private fun ApplicationEngine.Configuration.envConfig() { //todo

    val keyStoreFile = File("build/keystore.jks")
    val keyStore = buildKeyStore {
        certificate("sampleAlias") {
            password = "foobar"
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, "123456")

    connector {
        port = 80
        host = "0.0.0.0"
    }
    sslConnector(
        keyStore = keyStore,
        keyAlias = "sampleAlias",
        keyStorePassword = { "123456".toCharArray() },
        privateKeyPassword = { "foobar".toCharArray() }) {
        port = 8443
        keyStorePath = keyStoreFile
    }
}

/*
suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}*/
