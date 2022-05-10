plugins {
    id("com.google.cloud.tools.jib") version "3.1.1"
}

subprojects {
    //jib {
    //from {
    //image = "openjdk@sha256:f9be8e89a2bbf973dcd6c286f85bb0f68a8f9d5fa7c6241eb59f07add4a24789"
    //}

    //to {
    //image = "ghcr.io/soundcloud/$name"
    //tags = setOf(System.getenv("SEMAPHORE_GIT_SHA"), "latest")
    //auth {
    //username = System.getenv("GITHUB_ACTOR")
    //password = System.getenv("GITHUB_TOKEN")
    //}
    //}
    //}
}
