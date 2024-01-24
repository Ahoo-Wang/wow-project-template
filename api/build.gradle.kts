plugins {
    alias(libs.plugins.ksp)
}
dependencies {
    api(platform(project(":dependencies")))
    ksp(platform(project(":dependencies")))
    api(libs.swaggerAnnotations)
    api("me.ahoo.wow:wow-api")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("jakarta.validation:jakarta.validation-api")
    api("me.ahoo.coapi:coapi-api")
    api("io.projectreactor:reactor-core")
    implementation("org.springframework:spring-web")
    ksp("me.ahoo.wow:wow-compiler")
}
