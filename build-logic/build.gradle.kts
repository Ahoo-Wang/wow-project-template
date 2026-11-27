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
    // 把 Wow 的插件（me.ahoo.wow、me.ahoo.wow.api、me.ahoo.wow.service）放到约定插件的类路径上，
    // 由约定插件按 id 应用，版本取版本目录中的 `wow`。
    implementation(libs.wow.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.test.retry.gradle.plugin)
}
