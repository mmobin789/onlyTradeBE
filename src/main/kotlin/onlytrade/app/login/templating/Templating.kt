package onlytrade.app.login.templating

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.thymeleaf.Thymeleaf
import io.ktor.server.thymeleaf.ThymeleafContent
import io.ktor.utils.io.charsets.name
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver

fun Application.addTemplating() { //todo may use it.
    install(Thymeleaf) {
        setTemplateResolver(ClassLoaderTemplateResolver().apply {
            prefix = "templates/thymeleaf/"
            suffix = ".html"
            characterEncoding = Charsets.UTF_8.name
        })
    }

    routing {
        get("/html-thymeleaf") {
            call.respond(
                ThymeleafContent("index", mapOf("user" to ThymeleafUser(1, "user1"))),
                typeInfo = null
            )
        }
    }
}

data class ThymeleafUser(val id: Int, val name: String)