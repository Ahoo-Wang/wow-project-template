/**
 * 可发布的 Kotlin 库：api、api-client、domain。
 */
plugins {
    id("wow.kotlin-conventions")
    `java-library`
    id("org.jetbrains.dokka")
    id("org.jetbrains.kotlinx.kover")
    id("wow.publish-conventions")
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    "api"(platform(project(":dependencies")))
}

publishing {
    publications {
        create<MavenPublication>("mavenLibrary") {
            from(components["java"])
        }
    }
}
