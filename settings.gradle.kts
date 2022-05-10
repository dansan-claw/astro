// Incubating version catalog preview feature
@file:Suppress("UnstableApiUsage")

rootProject.name = "astro"

// Include gradle modules
include(":services:central-api")
include(":shared:core")

// Enable catalogs preview feature
enableFeaturePreview("VERSION_CATALOGS")

// Declare centralized dependency management
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            // Spring
            version("spring", "5.3.19")
            version("springBoot", "2.6.7")
            library("springCore", "org.springframework", "spring-core")
                .versionRef("spring")
            library("springContext", "org.springframework", "spring-context")
                .versionRef("spring")
            library("springBoot", "org.springframework.boot", "spring-boot")
                .versionRef("springBoot")
            library("springBootStarter", "org.springframework.boot", "spring-boot-starter")
                .versionRef("springBoot")
            library("springBootWebflux", "org.springframework.boot", "spring-boot-starter-webflux")
                .versionRef("springBoot")
            library("springBootActuator", "org.springframework.boot", "spring-boot-starter-actuator")
                .versionRef("springBoot")
            library("springBootConfigurationProcessor", "org.springframework.boot", "spring-boot-configuration-processor")
                .versionRef("springBoot")

            // Logging
            version("kotlinLogging", "2.1.21")
            library("kotlinLogging", "io.github.microutils", "kotlin-logging")
                .versionRef("kotlinLogging")

            // Databases
            version("lettuce", "6.1.5.RELEASE")
            library("lettuce", "io.lettuce", "lettuce-core")
                .versionRef("lettuce")

            // Serializing
            version("jacksonKotlin", "2.13.0")
            version("kotlinxSerialization", "1.3.1")
            library("jacksonKotlin", "com.fasterxml.jackson.module", "jackson-module-kotlin")
                .versionRef("jacksonKotlin")
            library("kotlinxSerialization", "org.jetbrains.kotlinx", "kotlinx-serialization-json")
                .versionRef("kotlinxSerialization")

            // Kotlin
            version("coroutines", "1.5.2")
            library("coroutinesCore", "org.jetbrains.kotlinx", "kotlinx-coroutines-core")
                .versionRef("coroutines")
            library("coroutinesReactor", "org.jetbrains.kotlinx", "kotlinx-coroutines-reactor")
                .versionRef("coroutines")

            // Base bundle
            bundle(
                "base",
                listOf(
                    "kotlinLogging",
                    "springBootConfigurationProcessor",
                )
            )

            // Core Spring bundle
            bundle(
                "springCore",
                listOf(
                    "springCore",
                    "springContext",
                    "springBoot",
                )
            )

            // Web service bundle
            bundle(
                "web",
                listOf(
                    "springBootWebflux",
                    "springBootActuator",
                    "jacksonKotlin",
                )
            )

            // coroutines
            bundle(
                "coroutines",
                listOf(
                    "coroutinesCore",
                    "coroutinesReactor"
                )
            )

            bundle(
                "serialization",
                listOf(
                    "jacksonKotlin",
                    "kotlinxSerialization",
                )
            )
        }
    }
}
