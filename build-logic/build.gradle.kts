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
    // Puts the Wow plugins (me.ahoo.wow, me.ahoo.wow.api, me.ahoo.wow.service) on every module's class path, so
    // module scripts apply them by id without a version: the version is the catalog's `wow`.
    implementation(libs.wow.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.test.retry.gradle.plugin)
}
