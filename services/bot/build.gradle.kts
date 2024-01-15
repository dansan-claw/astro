version = "1.0-SNAPSHOT"

plugins {
    id("com.google.cloud.tools.jib")
}

dependencies {
    implementation(libs.bundles.base)
    implementation(libs.bundles.web)
    implementation(libs.bundles.caching)
    implementation(libs.bundles.discord) {
        exclude(
            group = "club.minnced",
            module = "opus-java"
        )
    }

    implementation(libs.guava)
    implementation(libs.bigquery)
    implementation(libs.nanoid)
    implementation(libs.chargebee)

    implementation(project(":shared:core"))

    testImplementation(libs.junit)
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

sentry {
    // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
    // This enables source context, allowing you to see your source
    // code as part of your stack traces in Sentry.
    includeSourceContext = true

    org = "giuliopime"
    projectName = "astro-bot"
    authToken = System.getenv("SENTRY_AUTH_TOKEN")
}