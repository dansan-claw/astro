group = "space.astro"

plugins {
    base
    java

    alias(libs.plugins.jvm)
    alias(libs.plugins.spring)
    alias(libs.plugins.serialization)
    alias(libs.plugins.sentry)
}

repositories {
    maven {
        url = uri("https://maven.pkg.github.com/bot-astro/jda")
        credentials {
            this.username = property("ghpMavenUser").toString()
            this.password = property("ghpMavenPat").toString()
        }
    }
    mavenCentral()
}

// Migrate this to buildSrc or conventional builds when version catalogs are supported
subprojects {
    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/bot-astro/jda")
            credentials {
                this.username = property("ghpMavenUser").toString()
                this.password = property("ghpMavenPat").toString()
            }
        }
        mavenCentral()
        maven("https://jitpack.io/")
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "io.sentry.jvm.gradle")

    base {
        archivesName.set("${group.toString().replace(".", "-")}-$name")
    }

    group = "${parent?.group}.${parent?.name}"

    kotlin {
        jvmToolchain {
            this.languageVersion.set(JavaLanguageVersion.of(17))
        }
    }

    sentry {
        // Generates a JVM (Java, Kotlin, etc.) source bundle and uploads your source code to Sentry.
        // This enables source context, allowing you to see your source
        // code as part of your stack traces in Sentry.
        includeSourceContext = true

        org = "giuliopime"
        projectName = name
        authToken = System.getenv("SENTRY_AUTH_TOKEN")
    }
}
