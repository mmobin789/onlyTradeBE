plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
    alias(libs.plugins.kotlinSerialization)
}

group = "onlytrade.app"
version = "1.0.0"
application {
    mainClass.set("onlytrade.app.ApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "true"}")
}
val osName = System.getProperty("os.name").lowercase()
val tcnative_classifier = when {
    osName.contains("win") -> "windows-x86_64"
    osName.contains("linux") -> "linux-x86_64"
    osName.contains("mac") -> "osx-x86_64"
    else -> null
}

/*ktor { // to create a fat jar with custom name.
    fatJar {
        archiveFileName.set("OT-BE.jar")
    }
}*/

ktor {
    docker {
        localImageName.set("OTBE-docker-image")
        imageTag.set("0.0.1-preview")
        jreVersion.set(JavaVersion.VERSION_17)
        portMappings.set(listOf(
            io.ktor.plugin.features.DockerPortMapping(
                80,
                8080,
                io.ktor.plugin.features.DockerPortMappingProtocol.TCP
            )
        ))

        externalRegistry.set(
            io.ktor.plugin.features.DockerImageRegistry.dockerHub(
                appName = provider { "ktor-app" },
                username = providers.environmentVariable("DOCKER_HUB_USERNAME"), //mmobin789
                password = providers.environmentVariable("DOCKER_HUB_PASSWORD") //1994
            )
        )
    }
}
dependencies {
    implementation(projects.onlyTradeBusiness)
    implementation(libs.logback)
    implementation(libs.ktor.server.core.jvm)
    implementation(libs.ktor.server.netty.jvm)
    implementation(libs.ktor.serialization)
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)
    implementation(libs.ktor.server.resources)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.request.validation)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.html.builder)
    implementation(libs.ktor.network.tls.certificates)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.thymeleaf.jvm)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.exposed.dao)
    implementation(libs.h2)
    implementation(libs.postgresql)
   // implementation(libs.kotlin.css)

    if (tcnative_classifier != null) {
        implementation("io.netty:netty-tcnative-boringssl-static:2.0.69.Final:$tcnative_classifier")
    } else {
        implementation("io.netty:netty-tcnative-boringssl-static:2.0.69.Final")
    }

    testImplementation(libs.ktor.server.test.host.jvm)
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.client.content.negotiation.jvm)
    testImplementation(libs.ktor.client.auth)

}