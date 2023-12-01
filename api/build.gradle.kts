plugins {
    alias(libs.plugins.ksp)
}
dependencies {
    api(platform(project(":dependencies")))
    ksp(platform(project(":dependencies")))
    api(libs.swaggerAnnotations)
    implementation("me.ahoo.wow:wow-api")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    api("jakarta.validation:jakarta.validation-api")
    ksp("me.ahoo.wow:wow-compiler")
}
