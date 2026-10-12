plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.allopen)
    implementation(libs.ksp.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.test.retry.gradle.plugin)
}
