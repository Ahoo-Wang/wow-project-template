plugins {
    alias(libs.plugins.ksp)
}
dependencies {
    api(platform(project(":dependencies")))
    ksp(platform(project(":dependencies")))
    api(libs.swagger.annotations)
    api("me.ahoo.wow:wow-api")
    api("me.ahoo.wow:wow-apiclient")
    api("com.fasterxml.jackson.core:jackson-annotations")
    api("jakarta.validation:jakarta.validation-api")
    api("me.ahoo.coapi:coapi-api")
    api("me.ahoo.cocache:cocache-api")
    api("io.projectreactor:reactor-core")
    implementation("org.springframework:spring-web")
    ksp("me.ahoo.wow:wow-compiler")
}
