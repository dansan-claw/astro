version = "1.0-SNAPSHOT"

dependencies {
    // Generalized dependency bundles
    implementation(libs.bundles.base)
    implementation(libs.bundles.web)
    implementation(libs.bundles.serialization)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.jwt)
    implementation(libs.bundles.caching)

    // Database
    implementation(libs.mongo)

    // Project
    implementation(project(":shared:core"))
}

plugins {
    id("com.google.cloud.tools.jib")
}

jib {
    from {
        image = "openjdk@sha256:f9be8e89a2bbf973dcd6c286f85bb0f68a8f9d5fa7c6241eb59f07add4a24789"
    }

    to {
        image = "ghcr.io/Astro-Discord-Bot/$name"
        tags = setOf(System.getenv("SEMAPHORE_GIT_SHA"), "latest")
        auth {
            username = System.getenv("GITHUB_ACTOR")
            password = System.getenv("GITHUB_TOKEN")
        }
    }
}

sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = true

    org = "giuliopime"
    projectName = "astro-central-api"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}