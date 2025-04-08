package onlytrade.app

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.UserIdPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.thymeleaf.Thymeleaf
import onlytrade.app.db.configureDatabases
import onlytrade.app.login.data.LoginConst
import onlytrade.app.login.data.LoginConst.JWT_AUDIENCE
import onlytrade.app.login.data.LoginConst.JWT_ISSUER
import onlytrade.app.login.data.LoginConst.JWT_SECRET
import onlytrade.app.login.data.LoginConst.JWT_USERNAME_CLAIM
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver


fun main() {
    embeddedServer(
        Netty,
        port = try {
            System.getenv("PORT").toIntOrNull() ?: 80
        } catch (e: NullPointerException) {
            80
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
        allowHost("onlytrade-dev-9c057a85ddfa.herokuapp.com/")
        allowHost("www.onlytrade.co")
        allowHost("onlytrade.co")
        allowCredentials = true
        allowNonSimpleContentTypes = true
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
    }

    /*  install(Sessions) {
          val secretEncryptKey = hex("00112233445566778899aabbccddeeff")
          val secretSignKey = hex("6819b57a326945c1968f45236589")
          cookie<UserSession>("user_session") {
              cookie.path = "/"
              cookie.maxAgeInSeconds = 180
              transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
          }
      }*/

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
    val log = this.log
    install(Authentication) {
        jwt(LoginConst.JWT_AUTH) {
            realm = "OT Web"
            verifier(
                JWT.require(Algorithm.HMAC256(JWT_SECRET))
                    .withAudience(JWT_AUDIENCE).withIssuer(JWT_ISSUER).build()
            )
            validate { credentials ->
                credentials.payload.getClaim(JWT_USERNAME_CLAIM).asString()?.run {
                    UserIdPrincipal(this).also {
                        log.info("UserIdPrincipal set = ${it.name}")
                    }// Return principal if valid

                }
            }
        }
        /*   session<UserSession>("auth-session") {
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
           }*/

        //jwt {
        // Configure jwt authentication
        //  }
    }
    configureDatabases()

    addRouting()
}

/*
suspend inline fun ApplicationCall.respondCss(builder: CssBuilder.() -> Unit) {
    this.respondText(CssBuilder().apply(builder).toString(), ContentType.Text.CSS)
}*/
