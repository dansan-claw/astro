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
            library("springCore", "org.springframework", "spring-core").versionRef("spring")
            alias("springContext").to("org.springframework", "spring-context").versionRef("spring")
            alias("springBoot").to("org.springframework.boot", "spring-boot")
                .versionRef("springBoot")
            alias("springBootStarter").to("org.springframework.boot", "spring-boot-starter")
                .versionRef("springBoot")
            alias("springBootWebflux").to("org.springframework.boot", "spring-boot-starter-webflux")
                .versionRef("springBoot")
            alias("springBootActuator").to(
                "org.springframework.boot",
                "spring-boot-starter-actuator"
            ).versionRef("springBoot")
            alias("springBootConfigurationProcessor").to(
                "org.springframework.boot",
                "spring-boot-configuration-processor"
            ).versionRef("springBoot")

            // Logging
            version("kotlinLogging", "2.1.21")
            alias("kotlinLogging").to("io.github.microutils", "kotlin-logging")
                .versionRef("kotlinLogging")

            // Databases
            version("lettuce", "6.1.5.RELEASE")
            alias("lettuce").to("io.lettuce", "lettuce-core").versionRef("lettuce")

            // Serializing
            version("jacksonKotlin", "2.13.0")
            version("kotlinxSerialization", "1.3.1")
            alias("jacksonKotlin").to("com.fasterxml.jackson.module", "jackson-module-kotlin")
                .versionRef("jacksonKotlin")
            alias("kotlinxSerialization").to("org.jetbrains.kotlinx", "kotlinx-serialization-json")
                .versionRef("kotlinxSerialization")

            // Kotlin
            version("coroutines", "1.5.2")
            alias("coroutinesCore").to("org.jetbrains.kotlinx", "kotlinx-coroutines-core")
                .versionRef("coroutines")
            alias("coroutinesReactor").to("org.jetbrains.kotlinx", "kotlinx-coroutines-reactor")
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
